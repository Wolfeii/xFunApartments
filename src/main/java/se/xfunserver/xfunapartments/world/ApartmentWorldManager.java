package se.xfunserver.xfunapartments.world;

import org.bukkit.*;
import se.xfunserver.xfunapartments.api.model.Setting;
import se.xfunserver.xfunapartments.xFunApartments;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApartmentWorldManager {

    private final World apartmentWorld;
    private final Location defaultLocation;
    private final int borderDistance;
    private int distance = 0;
    private Direction direction;

    public ApartmentWorldManager(xFunApartments plugin) {
        this.apartmentWorld = Bukkit.createWorld(
                new WorldCreator(plugin.getStringSetting(Setting.WORLD_WORLD_NAME))
                        .type(WorldType.FLAT)
                        .generator(new EmptyWorldGenerator()));

        this.borderDistance = Integer.parseInt(plugin.getStringSetting(Setting.WORLD_APARTMENT_DISTANCE));
        this.direction = Direction.NORTH;
        defaultLocation = new Location(apartmentWorld, 0, 0, 0);
    }

    public World getApartmentWorld() {
        return apartmentWorld;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("Direction", direction.ordinal());
        map.put("Distance", distance);
        return map;
    }

    public synchronized Location getNextFreeLocation() {
        if (distance == 0) {
            distance++;
            return defaultLocation;
        }

        if (direction == null) direction = Direction.NORTH;
        Location loc = direction.addTo(defaultLocation, distance * borderDistance);
        direction = direction.next();
        if (direction == Direction.NORTH) distance++;
        return loc;
    }

    public enum Direction {
        NORTH(0, -1), NORTH_EAST(1, -1),
        EAST(1, 0), SOUTH_EAST(1, 1),
        SOUTH(0, 1), SOUTH_WEST(-1, 1),
        WEST(-1, 0), NORTH_WEST(-1, -1);

        private final int xMulti;
        private final int zMulti;

        Direction(int xMulti, int zMulti) {
            this.xMulti = xMulti;
            this.zMulti = zMulti;
        }

        Direction next() {
            return values()[(ordinal() + 1) % (values().length)];
        }

        Location addTo(Location loc, int value) {

            return loc.clone().add(value * (double) xMulti, 0, value * (double) zMulti);
        }
    }
}
