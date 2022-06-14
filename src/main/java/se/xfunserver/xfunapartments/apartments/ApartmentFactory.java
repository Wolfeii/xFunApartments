package se.xfunserver.xfunapartments.apartments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.model.apartment.IApartment;
import se.xfunserver.xfunapartments.util.Utility;
import se.xfunserver.xfunapartments.world.ApartmentWorldManager;
import se.xfunserver.xfunapartments.worldedit.ApartmentFactoryCompat;
import se.xfunserver.xfunapartments.worldedit.WorldEditRegion;
import se.xfunserver.xfunapartments.worldedit.WorldEditVector;
import se.xfunserver.xfunapartments.xFunApartments;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ApartmentFactory<A extends ApartmentSchematic<S>, S> {

    protected final xFunApartments plugin;
    private final ApartmentWorldManager manager;
    private final ApartmentFactoryCompat<S> compat;

    public ApartmentFactory(xFunApartments plugin, ApartmentWorldManager manager, ApartmentFactoryCompat<S> compat) {
        this.plugin = plugin;
        this.manager = manager;
        this.compat = compat;
    }

    public Apartment create(Player owner, ApartmentType apartmentType, A apartmentSchematic, final Location origin) {

        WorldEditRegion region = compat.pasteSchematic(apartmentSchematic.getSchematic(), origin);

        Location spawnLoc = null;

        WorldEditVector min = null;
        WorldEditVector max = null;

        World world = origin.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Världen för den givna positionen är null...");
        }

        for (WorldEditVector vector : compat.loop(region)) {
            final Block blockAt = world.getBlockAt((int) vector.getX(), (int) vector.getY(), (int) vector.getZ());
            Material type = blockAt.getType();

            if (type == Material.AIR || type.isAir()) continue;

            if (spawnLoc == null && type == Material.STICKY_PISTON) { // Is a spawn material block
                spawnLoc = new Location(origin.getWorld(), vector.getX() + 0.5, vector.getY() + 0.5, vector.getZ() + 0.5);
                Block block = spawnLoc.getBlock();
                if (block.getState().getData() instanceof Directional) {
                    spawnLoc.setYaw(Utility.getYaw(((Directional) block.getState().getData()).getFacing()));
                }

                blockAt.setType(Material.AIR);
                continue;
            }

            if (type == Material.RED_CONCRETE) { // Is a corner material block
                if (min == null) {
                    min = vector.copy();
                    continue;
                }

                if (max == null) {
                    max = vector.copy();
                    continue;
                }

                plugin.getLogger().warning("Apartment Schematic innehåller >2 hörn blocks!");
                break;
            }
        }

        if (min == null || max == null || min.equals(max)) {
            throw new IllegalArgumentException("Apartment Schematic kunde inte definera 2 olika hörn blocks, lägenheten kunde inte skapas.");
        }

        if (spawnLoc == null && origin.getWorld() != null) {
            spawnLoc = origin.getWorld().getHighestBlockAt(origin).getLocation();
            plugin.getLogger().warning(() -> "Inget spawn block hittades, så schematic root kommer att användas. Söker efter " + Material.STICKY_PISTON);
        }

        WorldEditRegion mainRegion = new WorldEditRegion(region.getMinimumPoint(), region.getMaximumPoint(), origin.getWorld());
        IWrappedRegion worldGuardRegion = createMainWorldGuardRegion(owner, mainRegion);

        Apartment apartment = IApartment.builder()
                .uniqueId(UUID.randomUUID())
                .owner(owner.getUniqueId())
                .spawnLocation(spawnLoc)
                .worldGuardRegion(worldGuardRegion)
                .expansions(new ArrayList<>())
                .type(apartmentType)
                .build();

        plugin.getStorageManager().storeApartment(apartment, true);

        return apartment;
    }

    protected IWrappedRegion createMainWorldGuardRegion(Player owner, WorldEditRegion r) {
        IWrappedRegion region = WorldGuardWrapper.getInstance().addCuboidRegion(
                        owner.getUniqueId().toString(),
                        r.getMinimumLocation(),
                        r.getMaximumLocation())
                .orElseThrow(() -> new RuntimeException("Could not create Main WorldGuard region"));

        region.getOwners().addPlayer(owner.getUniqueId());

        setMainFlags(region);
        return region;
    }

    public void setMainFlags(IWrappedRegion region) {
        final WorldGuardWrapper w = WorldGuardWrapper.getInstance();
        Stream.of(
                        w.getFlag("block-place", WrappedState.class),
                        w.getFlag("block-break", WrappedState.class),
                        w.getFlag("mob-spawning", WrappedState.class)
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(flag -> region.setFlag(flag, WrappedState.DENY));
    }

}
