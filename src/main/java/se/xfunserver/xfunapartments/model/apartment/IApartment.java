package se.xfunserver.xfunapartments.model.apartment;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import se.xfunserver.xfunapartments.apartments.expansions.ApartmentExpansion;
import se.xfunserver.xfunapartments.api.model.Apartment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
public class IApartment implements Apartment {

    @Getter
    private final UUID uniqueId;
    @Getter
    private final UUID owner;
    @Getter
    private final List<ApartmentExpansion> expansions;
    @Getter
    private final ApartmentType type;

    private Location locationOne, locationTwo, spawnLocation;
    private IWrappedRegion worldGuardRegion;

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public Player getOnlineOwner() {
        return Bukkit.getPlayer(owner);
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public List<ApartmentExpansion> getExpansions() {
        return expansions;
    }

    @Override
    public List<Player> getPlayersInside() {
        List<Player> result = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (contains(player)) {
                result.add(player);
            }
        }

        return result;
    }

    @Override
    public boolean isApartmentEmpty() {
        return getPlayersInside().isEmpty();
    }

    @Override
    public boolean contains(Player player) {
        return this.worldGuardRegion.contains(player.getLocation());
    }

    @Override
    public boolean hasExpansion(ApartmentExpansion expansion) {
        return getExpansions().contains(expansion);
    }

    @Override
    public boolean addExpansion() {
        return false;
    }

    @Override
    public void kickAllPlayers() {
        this.getPlayersInside().forEach(player -> {
            player.teleport(getType().getEntranceLocation());
        });
    }

    @Override
    public void teleport() {

        Player player = Bukkit.getPlayer(getOwner());
        if (player != null) teleport(player);
    }

    @Override
    public void teleport(Player player) {
        spawnLocation = new Location(
                getSpawnLocation().getWorld(),
                getSpawnLocation().getX(),
                getSpawnLocation().getY(),
                getSpawnLocation().getZ(),
                getSpawnLocation().getYaw(),
                getSpawnLocation().getYaw());

        player.teleport(spawnLocation);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1, Integer.MAX_VALUE));
    }

    @Override
    public void delete() {

    }

    @Override
    public void removeRegion() {

    }

    @Override
    public IWrappedRegion getWGRegion() {
        return worldGuardRegion;
    }
}
