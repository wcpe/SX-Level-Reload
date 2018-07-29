package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.sql.MySQLExecutorService;
import github.saukiya.sxlevel.util.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * @author Saukiya
 * @since 2018年3月28日
 */

public class ExpDataManager {

    @Getter
    private static HashMap<Player, ExpData> playerNameMap = new HashMap<>();

    /**
     * 定时保存玩家数据
     */
    public static void autoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MySQLExecutorService.getThread().execute(()->{
                    playerNameMap.values().forEach(ExpData::save);
                    Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] Save ExpData!");
                });
            }
        }.runTaskTimerAsynchronously(SXLevel.getPlugin(), 6000, Config.getConfig().getInt(Config.AUTO_SAVE_TICK,6000));
    }

    public static ExpData getPlayerData(Player player) {
        ExpData playerData = playerNameMap.get(player);
        if (playerData == null) {
            playerData = new ExpData(player);
            playerNameMap.put(player, playerData);
        }
        return playerData;
    }

    public static void removePlayerData(Player player) {
        if (playerNameMap.containsKey(player)) {
            MySQLExecutorService.getThread().execute(() -> playerNameMap.remove(player).save());
        }
    }
}
