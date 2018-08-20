package github.saukiya.sxlevel.listener;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.event.ChangeType;
import github.saukiya.sxlevel.event.SXExpChangeEvent;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OnListener implements Listener {

    private final SXLevel plugin;

    public OnListener(SXLevel plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
        int addExp = (int) (event.getAmount() * Config.getConfig().getDouble(Config.DEFAULT_EXP_VALUE));
        if (Config.getDisabledDefaultExpChange()) {
            event.setAmount(0);
        }
        if (Config.getConfig().getBoolean(Config.DEFAULT_EXP_ENABLED)) {
            ExpData playerData = plugin.getExpDataManager().getPlayerData(event.getPlayer());
            SXExpChangeEvent sxExpChangeEvent = new SXExpChangeEvent(event.getPlayer(), playerData, addExp, ChangeType.ADD);
            Bukkit.getPluginManager().callEvent(sxExpChangeEvent);
            if (!sxExpChangeEvent.isCancelled()) {
                playerData.addExp(sxExpChangeEvent.getAmount());
            }
        }
    }

    @EventHandler
    void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (Config.isSql() && plugin.getMysql() == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Message.getMsg(Message.PLAYER__NO_SQL_CONNECTION));
        }
    }

    @EventHandler
    void onPlayerLoginEvent(PlayerLoginEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getPlayer().isOnline()) {
                    plugin.getExpDataManager().getPlayerData(event.getPlayer());
                }
            }
        }.runTaskAsynchronously(SXLevel.getPlugin());
    }

    @EventHandler
    void onPlayerQuitEvent(PlayerQuitEvent event) {
        plugin.getExpDataManager().removePlayerData(event.getPlayer());
    }

}
