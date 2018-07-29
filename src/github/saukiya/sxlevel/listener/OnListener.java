package github.saukiya.sxlevel.listener;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.data.ExpDataManager;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.event.ChangeType;
import github.saukiya.sxlevel.event.SXExpChangeEvent;
import github.saukiya.sxlevel.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OnListener implements Listener {

    @EventHandler
    void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
        if (Config.getConfig().getBoolean(Config.DEFAULT_EXP_ENABLED)) {
            ExpData playerData = ExpDataManager.getPlayerData(event.getPlayer());
            int addExp = (int) (event.getAmount() * Config.getConfig().getDouble(Config.DEFAULT_EXP_VALUE));
            SXExpChangeEvent sxExpChangeEvent = new SXExpChangeEvent(event.getPlayer(), playerData, addExp,ChangeType.ADD);
            Bukkit.getPluginManager().callEvent(sxExpChangeEvent);
            if(!sxExpChangeEvent.isCancelled()){
                playerData.addExp(sxExpChangeEvent.getAmount());
            }
        }
        if(Config.disabledDefaultExpChange){
            event.setAmount(0);
        }
    }

    @EventHandler
    void onPlayerJoinEvent(PlayerQuitEvent event) {
        new BukkitRunnable(){
            @Override
            public void run() {
                if(event.getPlayer().isOnline()){
                    ExpDataManager.getPlayerData(event.getPlayer());
                }
            }
        }.runTaskLaterAsynchronously(SXLevel.getPlugin(),(Config.getConfig().getBoolean(Config.SQL_ENABLED) && SXLevel.getMysql() == null) ? 120 : 40);
    }

    @EventHandler
    void onPlayerQuitEvent(PlayerQuitEvent event) {
        ExpDataManager.removePlayerData(event.getPlayer());
    }

}
