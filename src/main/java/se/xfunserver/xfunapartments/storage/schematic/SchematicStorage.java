package se.xfunserver.xfunapartments.storage.schematic;

import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.xFunApartments;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SchematicStorage {
    private final Map<ApartmentType, ApartmentSchematic<?>> schematics = new HashMap<>();
    private final xFunApartments plugin;
    private final File schematicsDir;

    public SchematicStorage(xFunApartments plugin) {
        this.plugin = plugin;

        this.schematicsDir = new File(plugin.getDataFolder(), "schematics");
    }

    public ApartmentSchematic<?> getByType(final ApartmentType type) {
        return schematics.get(type);
    }

}
