package se.xfunserver.xfunapartments.model.apartment;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;

import java.util.UUID;

@Builder
public class ApartmentType {

    @Getter
    private final UUID uniqueId;
    @Getter
    private final String name;
    @Getter
    private final Location entranceLocation;
    @Getter
    private final ApartmentSchematic<?> schematic;
    @Getter
    private final double price;
}
