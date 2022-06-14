package se.xfunserver.xfunapartments;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import se.xfunserver.xfunapartments.apartments.ApartmentFactory;
import se.xfunserver.xfunapartments.apartments.ApartmentManager;
import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;
import se.xfunserver.xfunapartments.api.model.Setting;
import se.xfunserver.xfunapartments.commands.ApartmentsCommand;
import se.xfunserver.xfunapartments.locale.Locale;
import se.xfunserver.xfunapartments.manager.config.SettingManager;
import se.xfunserver.xfunapartments.manager.config.YAMLManager;
import se.xfunserver.xfunapartments.model.config.YAMLFile;
import se.xfunserver.xfunapartments.storage.StorageManager;
import se.xfunserver.xfunapartments.storage.impl.SQLStorageManager;
import se.xfunserver.xfunapartments.storage.sql.MySQLManager;
import se.xfunserver.xfunapartments.util.Utility;
import se.xfunserver.xfunapartments.world.ApartmentWorldManager;
import se.xfunserver.xfunapartments.worldedit.WorldEditHook;
import se.xfunserver.xfunapartments.worldedit.hooks.ModernWEHook;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class xFunApartments<T> extends JavaPlugin {

    @Getter private ApartmentFactory<ApartmentSchematic<?>, ?> apartmentFactory;
    @Getter private ApartmentWorldManager worldManager;
    @Getter private PaperCommandManager commandManager;
    @Getter private StorageManager storageManager;
    @Getter private MySQLManager mySQLManager;
    @Getter private Locale locale;

    @Getter private final ApartmentManager apartmentManager;
    @Getter private final SettingManager settingManager;
    @Getter private final YAMLManager yamlManager;

    @Getter private WorldEditHook<T> worldEditHook;
    @Getter private final Utility utility;
    @Getter private Economy economy;

    @Getter
    private YAMLFile defaultLocaleFile;

    public xFunApartments() {
        settingManager = new SettingManager(this);
        apartmentManager = new ApartmentManager(this);
        yamlManager = new YAMLManager(this);
        utility = new Utility(this);
    }

    @Override
    public void onEnable() {
        long profileStart = System.currentTimeMillis();

        getConfig().options().copyDefaults(true);
        saveConfig();

        worldManager = new ApartmentWorldManager(this);

        enableWeHook();

        // noinspection unchecked oh no
        apartmentFactory = (ApartmentFactory<ApartmentSchematic<?>, ?>) enableFactory(worldManager);

        enableStorage();

        locale = new Locale(this);

        enableCommands();

        if (!enableEconomy()) {
            getLogger().severe(() -> String.format("[%s] - Avstängd eftersom Vault inte hittades!",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        sendInfoMessage(String.format("xFunApartments v%s har laddats (%d ms).",
                getDescription().getVersion(), System.currentTimeMillis() - profileStart));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void enableStorage() {
        sendInfoMessage("Startar Storage.");

        defaultLocaleFile = yamlManager.loadIfNotExists("locale.yml");

        if (getStringSetting(Setting.GENERAL_STORAGE_TYPE)
                .equalsIgnoreCase(Constants.STORAGE_TYPE_MYSQL)) {

            mySQLManager = new MySQLManager(
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_SERVER),
                    Integer.parseInt(getStringSetting(Setting.GENERAL_STORAGE_MYSQL_PORT)),
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_DATABASE),
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_USERNAME),
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_PASSWORD),
                    Integer.parseInt(getStringSetting(Setting.GENERAL_STORAGE_MYSQL_MAXIMUM_POOLS)),
                    getLogger());

            storageManager = new SQLStorageManager(this, mySQLManager);

        } else if (getStringSetting(Setting.GENERAL_STORAGE_TYPE)
                .equalsIgnoreCase(Constants.STORAGE_TYPE_FLATFILE)) {

            getLogger().severe("din rackare, tänkte du precis använda yml filer???");
            this.setEnabled(false);
            return;
        }

        storageManager.load();
    }

    private void enableWeHook() {
        sendInfoMessage("Laddar WorldEdit Hook.");

        final Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
        String version = "";
        if (worldEdit != null) {
            version = worldEdit.getDescription().getVersion();
        }

        try {
            if (version.startsWith("6.")) {
                this.sendInfoMessage("Du använder en gammal version av WorldEdit, och en WorldEdit Hook" +
                        "kommer inte att laddas.");
            } else if (version.startsWith("7.") || version.startsWith("1.1")) {
                this.worldEditHook = (WorldEditHook<T>) Class.forName("se.xfunserver.xfunapartments.worldedit.hooks.ModernWEHook")
                        .getConstructor().newInstance();
            } else {
                throw new IllegalStateException("Ogiltig WorldEdit version: " + version);
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private ApartmentFactory<?, ?> enableFactory(ApartmentWorldManager apartmentManager) {
        return new ApartmentFactory<>(this, apartmentManager, worldEditHook.createApartmentFactoryCompat());
    }

    private void enableCommands() {
        sendInfoMessage("Aktiverar och registrerar kommandon.");

        commandManager = new PaperCommandManager(this);
        commandManager.getLocales().addBundleClassLoader(getClassLoader());

        commandManager.enableUnstableAPI("help");

        commandManager.registerCommand(new ApartmentsCommand(this));

        commandManager.getCommandConditions().addCondition(Double.class, "limits", (c, exec, value) -> {
           if (value == null) {
               return;
           }

           if (c.hasConfig("min") && c.getConfigValue("min", 0) > value) {
               throw new ConditionFailedException("Värdet måste vara >" + c.getConfigValue("min", 0));
           }

           if (c.hasConfig("max") && c.getConfigValue("max", 0) < value) {
               throw new ConditionFailedException(
                       "Värdet måste vara <" + c.getConfigValue("max", 0));
           }
        });
    }

    private boolean enableEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> registeredServiceProvider =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null)
            return false;

        economy = registeredServiceProvider.getProvider();
        return true;
    }

    private void sendInfoMessage(String message){
        getLogger().info(Constants.INFO_MESSAGE_PREFIX + message);
    }

    public Map<Setting, String> getSettingsMap() {
        Map<Setting, String> settingMap = new HashMap<>();
        Arrays.stream(Setting.values())
                .forEach(setting -> settingMap.put(setting, getStringSetting(setting)));

        return settingMap;
    }

    public boolean isPreload(StorageManager.Type type){
        return isSetting("storage.options.preload." + type.name().toLowerCase());
    }

    public boolean isSetting(String key) {
        Optional<Setting> setting = Setting.getSettingByKey(key);

        return getSetting(key);
    }

    public boolean getSetting(String key) {
        return getConfig().getBoolean("settings." + key);
    }

    public String getStringSetting(Setting setting) {
        return getConfig().getString("settings." + setting.getKey());
    }
}
