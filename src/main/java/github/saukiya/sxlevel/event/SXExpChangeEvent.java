package github.saukiya.sxlevel.event;

import github.saukiya.sxlevel.data.ExpData;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saukiya
 * @since ${date}
 */
public class SXExpChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final ExpData expData;
    private final ChangeType type;
    private final List<String> tipAdditionMessageList = new ArrayList<>();
    private int amount;
    private boolean Cancelled = false;

    private SXExpChangeEvent(Player player, ExpData expData, int amount, ChangeType type) {
        this.player = player;
        this.expData = expData;
        this.amount = amount;
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static SXExpChangeEvent callEvent(Player player, ExpData expData, int amount, ChangeType type) {
        val sxExpChangeEvent = new SXExpChangeEvent(player, expData, amount, type);
        Bukkit.getPluginManager().callEvent(sxExpChangeEvent);
        return sxExpChangeEvent;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ChangeType getType() {
        return type;
    }

    public List<String> getTipAdditionMessageList() {
        return tipAdditionMessageList;
    }

    public void addAdditionMessage(String message) {
        tipAdditionMessageList.add(message);
    }

    public ExpData getExpData() {
        return expData;
    }
}
