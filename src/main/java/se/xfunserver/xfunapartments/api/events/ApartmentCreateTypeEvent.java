package se.xfunserver.xfunapartments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;

public class ApartmentCreateTypeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final ApartmentType apartmentType;
    private boolean cancelled;

    public ApartmentCreateTypeEvent(Player player,
                                    ApartmentType apartmentType) {
        this.player = player;
        this.apartmentType = apartmentType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public ApartmentType getApartmentType() {
        return apartmentType;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
