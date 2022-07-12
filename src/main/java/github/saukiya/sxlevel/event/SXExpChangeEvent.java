package github.saukiya.sxlevel.event;

import github.saukiya.sxlevel.data.ExpData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Saukiya
 * @since ${date}
 */
public class SXExpChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final ExpData expData;
    private final ChangeType type;
    private int amount;
    private boolean Cancelled = false;

    public SXExpChangeEvent(Player player, ExpData expData, int amount, ChangeType type) {
        this.player = player;
        this.expData = expData;
        this.amount = amount;
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ChangeType getType() {
        return type;
    }

    public boolean isCancelled() {
        return Cancelled;
    }

    public void setCancelled(boolean cancelled) {
        Cancelled = cancelled;
    }

    public ExpData getExpData() {
        return expData;
    }
}
