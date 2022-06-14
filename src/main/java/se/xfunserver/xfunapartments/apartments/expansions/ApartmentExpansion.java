package se.xfunserver.xfunapartments.apartments.expansions;

import lombok.Builder;
import lombok.Getter;
import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;

import javax.annotation.Nonnull;
import java.util.List;

@Builder
public class ApartmentExpansion {

    @Getter
    private final boolean enabled;

    @Getter
    private final ApartmentType type;

    @Getter
    private double price;

    @Getter
    private final List<ApartmentSchematic<?>> schematics;

    @Getter
    private final List<ExpansionCommand> commands;
}
