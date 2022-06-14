package se.xfunserver.xfunapartments.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.xfunserver.xfunapartments.Constants;
import se.xfunserver.xfunapartments.apartments.expansions.ApartmentExpansion;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.worldedit.WorldEditVector;
import se.xfunserver.xfunapartments.xFunApartments;

import java.util.LinkedList;

public class Utility {

    private final xFunApartments plugin;

    public Utility(xFunApartments plugin){
        this.plugin = plugin;
    }


    public static String serializeExpansion(ApartmentExpansion expansion) {
        return expansion.getType().getName();
    }

    public static ApartmentExpansion deserializeExpansion(String serializedString) {
        return null;
    }

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + ";" +
                location.getX() + ";" +
                location.getY() + ";" +
                location.getZ() + ";" +
                location.getYaw() + ";" +
                location.getPitch();
    }

    public static Location deserializeLocation(String serializedLocation) {
        String[] splitLoc = serializedLocation.split(";");

        World world = Bukkit.getWorld(splitLoc[0]);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        double x = Double.parseDouble(splitLoc[1]);
        double y = Double.parseDouble(splitLoc[2]);
        double z = Double.parseDouble(splitLoc[3]);
        float yaw = Float.parseFloat(splitLoc[4]);
        float pitch = Float.parseFloat(splitLoc[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Float getYaw(BlockFace face) {
        switch (face) {
            case WEST:
                return 90f;
            case NORTH:
                return 180f;
            case EAST:
                return -90f;
            case SOUTH:
                return -180f;
            default:
                return 0f;
        }
    }

    public static WorldEditVector toWEVector(Vector bukkitVector) {
        return new WorldEditVector(bukkitVector.getX(), bukkitVector.getY(), bukkitVector.getZ());
    }


}
