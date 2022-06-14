package se.xfunserver.xfunapartments.storage.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.api.model.Setting;
import se.xfunserver.xfunapartments.manager.config.YAMLManager;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.model.config.YAMLFile;
import se.xfunserver.xfunapartments.storage.StorageManager;
import se.xfunserver.xfunapartments.xFunApartments;

public class FlatfileStorageManager extends StorageManager {

    private final xFunApartments plugin;
    private YAMLFile apartmentDataFile,
                     apartmentTypeFile,
                     localeFile;

    protected FlatfileStorageManager(xFunApartments plugin) {
        super(plugin);
        this.plugin = plugin;
        plugin.getLogger().info("Laddar storage filer...");
    }

    @Override
    public void setup() {
        YAMLManager yamlManager = plugin.getYamlManager();
        this.apartmentDataFile = yamlManager.loadIfNotExists(plugin.getStringSetting(Setting.GENERAL_STORAGE_FLATFILE_APARTMENTS));
        this.apartmentTypeFile = yamlManager.loadIfNotExists(plugin.getStringSetting(Setting.GENERAL_STORAGE_FLATFILE_TYPE));
        this.localeFile = plugin.getDefaultLocaleFile();
    }

    @Override
    public void preload(Type type) {

        plugin.getLogger().info("Laddar " + type.name().toLowerCase());

        switch (type) {
            case APARTMENT:
                if (!apartmentDataFile.isLoaded())
                    apartmentDataFile.load();

                YamlConfiguration dataStorage = apartmentDataFile.getYamlConfiguration();
                ConfigurationSection apartmentsStorageSection = dataStorage.getConfigurationSection("apartments");

                if (apartmentsStorageSection == null) {
                    plugin.getLogger().warning("Konfigurations sektionen 'apartments' existerar inte i " + apartmentDataFile.getFile().getName());
                    return;
                }


        }
    }

    @Override
    public void storeApartment(Apartment apartment, boolean save) {

    }

    @Override
    public void updateApartment(Apartment apartment, Action action) {

    }

    @Override
    public void storeType(ApartmentType type) {

    }

    @Override
    public void removeType(ApartmentType type) {

    }
}
