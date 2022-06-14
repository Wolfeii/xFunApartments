package se.xfunserver.xfunapartments.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.api.model.Setting;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.xFunApartments;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerMovementHandler implements Listener {

    private final xFunApartments plugin;

    public PlayerMovementHandler(xFunApartments plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event != null && !event.isCancelled() && event.getTo() != null) {
            int enterDistance = Integer.parseInt(plugin.getStringSetting(Setting.APARTMENT_ENTER_DISTANCE));

            List<ApartmentType> nearbyApartments = plugin.getStorageManager().getCachedTypes()
                    .stream()
                    .filter(type -> type.getEntranceLocation().distance(
                            event.getTo()) <= enterDistance)
                    .collect(Collectors.toList());

            if (nearbyApartments.size() == 0)
                return;
        }
    }
}
