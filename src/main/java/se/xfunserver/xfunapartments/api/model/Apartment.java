package se.xfunserver.xfunapartments.api.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import se.xfunserver.xfunapartments.apartments.expansions.ApartmentExpansion;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;

import java.util.List;
import java.util.UUID;

public interface Apartment {

    UUID getUniqueId();

    UUID getOwner();

    Player getOnlineOwner();

    ApartmentType getType();

    IWrappedRegion getWGRegion();

    Location getSpawnLocation();

    List<ApartmentExpansion> getExpansions();

    List<Player> getPlayersInside();

    boolean isApartmentEmpty();

    boolean contains(Player player);

    boolean hasExpansion(ApartmentExpansion expansion);

    boolean addExpansion();

    void kickAllPlayers();

    void teleport();

    void teleport(Player player);

    void delete();

    void removeRegion();

}
