package se.xfunserver.xfunapartments.apartments;

import lombok.Getter;
import org.bukkit.entity.Player;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.world.ApartmentWorldManager;
import se.xfunserver.xfunapartments.xFunApartments;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ApartmentManager {

    private final xFunApartments plugin;
    @Getter
    private final List<Apartment> activeApartments;

    private final ApartmentWorldManager apartmentWorldManager;

    public ApartmentManager(xFunApartments plugin) {
        this.plugin = plugin;
        this.activeApartments = new ArrayList<>();
        this.apartmentWorldManager = new ApartmentWorldManager(plugin);
    }

    public LinkedList<Apartment> getApartmentsByOwner(Player player) {
        return activeApartments.stream()
                .filter(apartment -> apartment.getOnlineOwner() != null
                        && apartment.getOnlineOwner().getUniqueId().equals(player.getUniqueId()))
                .sorted(Comparator.comparingInt(o -> o.getExpansions().size()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public void getOrCreate(@Nonnull Player player, ApartmentType type, boolean isNew) {
        Apartment apartment = getApartmentsByOwner(player)
                .stream()
                .filter(apt -> apt.getType().equals(type))
                .findAny().orElse(null);

        if (apartment != null) {
            return;
        }

        ApartmentSchematic<?> schematic = type.getSchematic();
        if (schematic == null) {
            throw new IllegalStateException("Ingen schematic hittades.");
        }

        apartment = plugin.getApartmentFactory().create(player, type, schematic, apartmentWorldManager.getNextFreeLocation());
        plugin.getStorageManager().storeApartment(apartment, true);
        activeApartments.add(apartment);
    }
}
