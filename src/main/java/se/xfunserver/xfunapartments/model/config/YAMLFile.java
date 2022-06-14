package se.xfunserver.xfunapartments.model.config;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class YAMLFile {

    private final Plugin plugin;

    @Getter
    private final File file;
    @Getter
    private YamlConfiguration yamlConfiguration;

    public YAMLFile(Plugin plugin, String name) {
        this.plugin = plugin;
        this.file = new File(name);
    }

    public boolean exists() {
        return file.exists();
    }

    public void create() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            plugin.saveResource(file.getName(), false);
        }
    }

    public void load() {
        yamlConfiguration = new YamlConfiguration();

        try {
            yamlConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        return yamlConfiguration != null;
    }

    public void save() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
