package se.xfunserver.xfunapartments.manager.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import se.xfunserver.xfunapartments.apartments.expansions.ApartmentExpansion;
import se.xfunserver.xfunapartments.model.config.YAMLFile;
import se.xfunserver.xfunapartments.xFunApartments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YAMLManager {

    private final xFunApartments plugin;

    public YAMLManager(xFunApartments plugin){
        this.plugin = plugin;
    }

    public YAMLFile loadIfNotExists(String name){
        YAMLFile configuration = new YAMLFile(plugin, plugin.getDataFolder() + File.separator + name);

        if(!configuration.exists())
            configuration.create();

        configuration.load();
        return configuration;
    }

}
