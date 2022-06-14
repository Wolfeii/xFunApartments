package se.xfunserver.xfunapartments.worldedit;

import org.bukkit.Location;

public interface ApartmentFactoryCompat<S> {

    WorldEditRegion pasteSchematic(S apartmentSchematic, Location location);

    Iterable<WorldEditVector> loop(WorldEditRegion region);
}
