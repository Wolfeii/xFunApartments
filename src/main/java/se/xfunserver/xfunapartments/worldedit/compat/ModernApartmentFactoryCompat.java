package se.xfunserver.xfunapartments.worldedit.compat;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import se.xfunserver.xfunapartments.worldedit.ApartmentFactoryCompat;
import se.xfunserver.xfunapartments.worldedit.WorldEditRegion;
import se.xfunserver.xfunapartments.worldedit.WorldEditVector;
import se.xfunserver.xfunapartments.worldedit.hooks.ModernWEHook;
import se.xfunserver.xfunapartments.xFunApartments;

import java.util.Iterator;

public class ModernApartmentFactoryCompat implements ApartmentFactoryCompat<Clipboard> {

    private final World world;

    public ModernApartmentFactoryCompat(xFunApartments plugin) {
        this.world = BukkitAdapter.adapt(plugin.getWorldManager().getApartmentWorld());
    }

    @Override
    public WorldEditRegion pasteSchematic(Clipboard apartmentSchematic, Location location) {
        if (apartmentSchematic == null) {
            Bukkit.getLogger().info("ModernApartmentFactoryCompat -> apartmentSchematic = null");
        } else if (location == null) {
            Bukkit.getLogger().info("ModernApartmentFactoryCompat -> location = null");
        }

        try {
            if (location != null) {
                if (apartmentSchematic != null) {
                    location.setY(apartmentSchematic.getOrigin().getY());
                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                        final BlockVector3 centerVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
                        final Operation operation = new ClipboardHolder(apartmentSchematic)
                                .createPaste(editSession)
                                .to(centerVector)
                                .ignoreAirBlocks(true)
                                .build();

                        try {
                            Operations.complete(operation);
                            Region region = apartmentSchematic.getRegion();
                            region.setWorld(world);
                            region.shift(centerVector.subtract(apartmentSchematic.getOrigin()));

                            final WorldEditVector min = ModernWEHook.transform(region.getMinimumPoint());
                            final WorldEditVector max = ModernWEHook.transform(region.getMaximumPoint());

                            return new WorldEditRegion(min, max, location.getWorld());
                        }  catch (WorldEditException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    @Override
    public Iterable<WorldEditVector> loop(WorldEditRegion region) {

        final Iterator<BlockVector3> vectors = ModernWEHook.transform(region).iterator();
        final Iterator<WorldEditVector> weVecIterator = new Iterator<WorldEditVector>() {
            @Override
            public boolean hasNext() {
                return vectors.hasNext();
            }

            @Override
            public WorldEditVector next() {
                return ModernWEHook.transform(vectors.next());
            }
        };

        return () -> weVecIterator;
    }
}
