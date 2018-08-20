package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.sql.MySQLExecutorService;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
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
    private final HashMap<Player, ExpData> map = new HashMap<>();

    private final SXLevel plugin;

    public ExpDataManager(SXLevel plugin) {
        this.plugin = plugin;
        autoSave();
    }

    /**
     * 定时保存玩家数据
     */
    public void autoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MySQLExecutorService.getThread().execute(() -> {
                    map.values().forEach(ExpData::save);
                    Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "Save ExpData!");
                });
            }
        }.runTaskTimerAsynchronously(SXLevel.getPlugin(), 6000, Config.getConfig().getInt(Config.AUTO_SAVE_TICK, 6000));
    }

    public ExpData getPlayerData(Player player) {
        ExpData playerData = map.get(player);
        if (playerData == null) {
            playerData = new ExpData(plugin, player);
            map.put(player, playerData);
        }
        return playerData;
    }

    public void removePlayerData(Player player) {
        if (map.containsKey(player)) {
            MySQLExecutorService.getThread().execute(() -> map.remove(player).save());
        }
    }
}
