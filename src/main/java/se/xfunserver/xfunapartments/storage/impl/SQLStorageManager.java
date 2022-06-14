package se.xfunserver.xfunapartments.storage.impl;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.xfunserver.xfunapartments.apartments.expansions.ApartmentExpansion;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.api.model.Setting;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.model.apartment.IApartment;
import se.xfunserver.xfunapartments.model.config.YAMLFile;
import se.xfunserver.xfunapartments.storage.StorageManager;
import se.xfunserver.xfunapartments.storage.sql.MySQLManager;
import se.xfunserver.xfunapartments.util.Utility;
import se.xfunserver.xfunapartments.xFunApartments;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SQLStorageManager extends StorageManager {

    private final xFunApartments plugin;
    private final MySQLManager mySQLManager;
    private final String prefix;
    private final YamlConfiguration defaultLocale;

    public SQLStorageManager(xFunApartments plugin, MySQLManager mySQLManager) {
        super(plugin);
        this.mySQLManager = mySQLManager;
        this.plugin = plugin;
        this.prefix = plugin.getStringSetting(Setting.GENERAL_STORAGE_MYSQL_PREFIX);
        this.defaultLocale = plugin.getDefaultLocaleFile().getYamlConfiguration();
    }

    @Override
    public void setup() {

    }

    @Override
    public void preload(Type storageType) {

        createTables();

        String table = getTable(storageType.name().toLowerCase());
        CachedRowSet result = mySQLManager.query("SELECT * FROM " + table);

        if (result == null) {
            plugin.getLogger().warning("Misslyckades att ladda " + storageType.name().toLowerCase() + " från MySQL source.");
            return;
        }

        plugin.getLogger().info("Laddar " + storageType.name());

        switch (storageType) {
            case LOCALE:
                try {
                    while (result.next()) {
                        String key = result.getString("l_key");
                        String value = result.getString("value");

                        cachedLocaleData.put(key, value);
                    }
                } catch (SQLException ex) {
                    plugin.getLogger().warning("Ett fel uppstod med att förladda " + storageType.name());
                    ex.printStackTrace();
                }

                List<LocaleItem> items = getDefaultLocaleValues();
                items.forEach(item -> {
                    if (!cachedLocaleData.containsKey(item.getKey())) {
                        mySQLManager.execute("INSERT INTO " + table + " (l_key, value) VALUES(?, ?)", item.getKey(), item.getValue());
                    }
                });

                break;
            case APARTMENT:
                try {
                    while (result.next()) {
                        UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                        UUID ownerId = UUID.fromString(result.getString("owner"));
                        Optional<ApartmentType> type = plugin.getStorageManager()
                                .getTypeByUniqueId(UUID.fromString(result.getString("type")));

                        if (!type.isPresent()) {
                            plugin.getLogger().warning("Apartment Typ specifierad av lägenhet '" + uniqueId.toString() + "' är ogiltlig.");
                            return;
                        }

                        IApartment.IApartmentBuilder apartmentBuilder = IApartment.builder()
                                .uniqueId(uniqueId)
                                .owner(ownerId)
                                .type(type.get());

                        List<ApartmentExpansion> expansions = Arrays.stream(result.getString("expansions").split("---"))
                                .map(Utility::deserializeExpansion)
                                .collect(Collectors.toList());

                        apartmentBuilder.expansions(expansions);

                        Apartment apartment = apartmentBuilder.build();
                        storeApartment(apartment, false);
                    }
                } catch (SQLException ex) {
                    plugin.getLogger().warning(provideExceptionErrorMessage(storageType));
                    ex.printStackTrace();
                }

                break;
            case TYPE:

                try {
                    while (result.next()) {
                        UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                        String name = result.getString("name");
                        Location entranceLocation = Utility.deserializeLocation(result.getString("entrance_location"));
                        double price = 0;

                        if (!result.getString("price").isEmpty())
                            price = result.getDouble("price");

                        ApartmentType.ApartmentTypeBuilder apartmentTypeBuilder = ApartmentType.builder()
                                .uniqueId(uniqueId)
                                .name(name)
                                .entranceLocation(entranceLocation)
                                .price(price);

                        cachedTypes.add(apartmentTypeBuilder.build());

                        plugin.getLogger().info("Laddade lägenhettyp '" + name + "' med identifier '" + uniqueId + "'");
                    }
                } catch (SQLException ex) {
                    plugin.getLogger().warning(provideExceptionErrorMessage(storageType));
                    ex.printStackTrace();
                }

                break;
        }
    }

    private void createTables() {

        String apartmentQuery = "" +
                "create tables if not exists xfun_apartments (\n" +
                "    apartmentId int auto_increment\n" +
                "        primary key,\n" +
                "    uniqueId text not null,\n" +
                "    owner    text not null\n" +
                ");";

        mySQLManager.execute(apartmentQuery);
    }

    @Override
    public void storeApartment(Apartment apartment, boolean save) {

        cachedApartments.add(apartment);

        if (save) {
            String expansions = apartment.getExpansions().stream()
                    .map(Utility::serializeExpansion).collect(Collectors.joining("---"));

            mySQLManager.execute("INSERT INTO " + getTable("apartments") + " (uniqueId, owner, type, expansions, wg_region, spawn) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    apartment.getUniqueId().toString(), apartment.getOwner().toString(), apartment.getType().getName(), expansions, apartment.getWGRegion().getId(),
                            Utility.serializeLocation(apartment.getSpawnLocation()));
        }
    }

    public List<LocaleItem> getDefaultLocaleValues() {
        List<LocaleItem> strings = new ArrayList<>();
        for (String key : defaultLocale.getKeys(false)) {
            if (defaultLocale.isString(key)) {
                strings.add(LocaleItem.builder().key(key).value(defaultLocale.getString(key)).build());
            } else {
                strings.addAll(getStringsFromSection(defaultLocale.getConfigurationSection(key), ""));
            }
        }

        return strings;
    }

    public List<LocaleItem> getStringsFromSection(ConfigurationSection section, String parent) {
        List<LocaleItem> strings = new ArrayList<>();
        if (section == null)
            return strings;

        parent += (parent.isEmpty() ? "" : ".") + (section.getName().equals("locale") ? "" : section.getName());

        for (String key : section.getKeys(false)) {

            if (section.isString(key)) {
                strings.add(LocaleItem.builder().key(parent + "." + key).value(section.getString(key)).build());
            } else {
                if (section.isConfigurationSection(key)) {
                    strings.addAll(getStringsFromSection(section.getConfigurationSection(key), parent));
                }
            }
        }

        return strings;
    }

    private String getTable(String name){
        return prefix + name;
    }

    @Override
    public void updateApartment(Apartment apartment, Action action) {
        switch (action) {
            case REMOVE: {
                cachedApartments.remove(apartment);
                mySQLManager.execute("DELETE FROM " + getTable("apartments") + " WHERE uniqueId = ?", apartment.getUniqueId().toString());
                break;
            }

            case EXPANSION: {
                updateValue(apartment, "expansions", apartment.getExpansions().stream()
                        .map(Utility::serializeExpansion).collect(Collectors.joining("---")));
                break;
            }

            default: {}
        }
    }

    @Override
    public void storeType(ApartmentType type) {
        String key = UUID.randomUUID().toString();
        mySQLManager.execute("INSERT INTO " + getTable("type") + " (uniqueId, name, price, entrance_location) VALUES(?, ?, ?, ?)",
                key, type.getName(), String.valueOf(type.getPrice()), Utility.serializeLocation(type.getEntranceLocation()));
    }

    @Override
    public void removeType(ApartmentType type) {
        cachedTypes.remove(type);
        mySQLManager.execute("DELETE FROM " + getTable("type") + " WHERE uniqueId=?", type.getUniqueId().toString());
    }

    private void updateValue(Apartment apartment, String col, String value) {
        mySQLManager.execute("UPDATE " + getTable("apartments") + " SET " + col + "=? WHERE uniqueId=?", value, apartment.getUniqueId().toString());
    }

    private String provideExceptionErrorMessage(Type type){
        return String.format("Ett fel uppstod med att ladda %s", type.name().toLowerCase());
    }

    @Builder
    public static class LocaleItem {

        @Getter
        private final String key;
        @Getter
        private final String value;
    }
}
