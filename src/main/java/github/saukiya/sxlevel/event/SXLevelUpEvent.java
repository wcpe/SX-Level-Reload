package github.saukiya.sxlevel.event;

import github.saukiya.sxlevel.data.ExpData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Saukiya
 * @since ${date}
 */
public class SXLevelUpEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final ExpData expData;

    private boolean Cancelled = false;

    public SXLevelUpEvent(Player player, ExpData expData) {
        this.player = player;
        this.expData = expData;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return Cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        Cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public ExpData getExpData() {
        return expData;
    }
}
