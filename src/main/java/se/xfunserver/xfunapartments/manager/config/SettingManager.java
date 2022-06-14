package se.xfunserver.xfunapartments.manager.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;
import se.xfunserver.xfunapartments.apartments.expansions.ApartmentExpansion;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.xFunApartments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingManager {

    private final xFunApartments plugin;
    private final FileConfiguration config;

    @Getter
    private final List<ApartmentExpansion> apartmentExpansions;

    public SettingManager(xFunApartments plugin) {
        this.plugin              = plugin;
        this.config              = plugin.getConfig();
        this.apartmentExpansions = loadExpansions();
    }

    private List<ApartmentExpansion> loadExpansions() {

        ConfigurationSection section = config.getConfigurationSection("settings.apartments.expansions.types");
        List<ApartmentExpansion> expansions = new ArrayList<>();

        if (section == null)
            return expansions;

        for (String key : section.getKeys(false)) {
            ConfigurationSection expansionSection = section.getConfigurationSection(key);
            ApartmentExpansion.ApartmentExpansionBuilder expansionBuilder = ApartmentExpansion.builder();


            boolean enabled = expansionSection.getBoolean("enabled");

            String apartmentTypeStr = expansionSection.getString("apartment");
            if (apartmentTypeStr == null)
                continue;

            ApartmentType apartmentType = plugin.getStorageManager()
                    .getTypeByName(apartmentTypeStr).orElse(null);

            if (apartmentType == null)
                continue;

            String priceStr = expansionSection.getString("cost");
            if (priceStr == null)
                continue;

            double price = Double.parseDouble(priceStr);

            ConfigurationSection schematicSection = expansionSection.getConfigurationSection(key + ".options.schematics");
            List<ApartmentSchematic<?>> schematics = loadSchematics(schematicSection);

            expansions.add(expansionBuilder.schematics(schematics)
                    .enabled(enabled)
                    .price(price)
                    .type(apartmentType)
                    .build());
        }

        return expansions;
    }

    public List<ApartmentSchematic<?>> loadSchematics(ConfigurationSection schematicSection) {
        List<ApartmentSchematic<?>> schematics = new ArrayList<>();
        for (String schematicKey : schematicSection.getKeys(false)) {
            ConfigurationSection schematicSec = schematicSection.getConfigurationSection(schematicKey);

            File file = new File(plugin.getDataFolder(), "schematics/" + schematicSec.getString("schematic"));
            if (!file.exists())
                continue;

            String loadBlockStr = schematicSec.getString("load_block");
            if (loadBlockStr == null)
                continue;

            Material material = Material.valueOf(loadBlockStr);
            final ApartmentSchematic<?> schematic = plugin.getWorldEditHook().loadApartmentSchematic(schematicKey, file, material);
            schematics.add(schematic);
        }

        return schematics;
    }
}
