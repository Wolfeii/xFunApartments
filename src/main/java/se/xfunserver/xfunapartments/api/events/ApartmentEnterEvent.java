package se.xfunserver.xfunapartments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import se.xfunserver.xfunapartments.model.apartment.IApartment;

public class ApartmentEnterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private IApartment apartment;
    private boolean cancelled;

    public ApartmentEnterEvent(Player player,
                               IApartment apartment) {
        this.player = player;
        this.apartment = apartment;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public IApartment getApartment() {
        return apartment;
    }
}
