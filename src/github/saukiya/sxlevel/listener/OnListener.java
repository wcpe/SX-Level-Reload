package github.saukiya.sxlevel.listener;

import github.saukiya.sxlevel.data.PlayerExpDataManager;
import github.saukiya.sxlevel.data.PlayerExpData;
import github.saukiya.sxlevel.util.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnListener implements Listener {

    @EventHandler
    void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
        if (Config.getConfig().getBoolean(Config.DEFAULT_EXP_ENABLED)) {
            PlayerExpData playerData = PlayerExpDataManager.getPlayerData(event.getPlayer());
            int addExp = (int) (event.getAmount() * Config.getConfig().getDouble(Config.DEFAULT_EXP_VALUE));
            playerData.addExp(addExp);

        }
    }

    @EventHandler
    void onPlayerQuitEvent(PlayerQuitEvent event) {
        PlayerExpDataManager.removePlayerData(event.getPlayer());
    }

}
