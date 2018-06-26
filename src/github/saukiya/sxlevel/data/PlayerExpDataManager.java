package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * @author Saukiya
 * @since 2018年3月28日
 */

public class PlayerExpDataManager {

    @Getter
    private static HashMap<Player, PlayerExpData> playerNameMap = new HashMap<>();

    /**
     * 定时保存玩家数据
     */
    public static void autoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                playerNameMap.values().forEach(PlayerExpData::save);
                Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] Save PlayerExpData!");
            }
        }.runTaskTimerAsynchronously(SXLevel.getPlugin(), 6000, 6000);
    }

    public static PlayerExpData getPlayerData(Player player) {
        PlayerExpData playerData = playerNameMap.get(player);
        if (playerData == null) {
            playerData = new PlayerExpData(player);
            playerNameMap.put(player, playerData);
        }
        return playerData;
    }

    public static void removePlayerData(Player player) {
        if (playerNameMap.containsKey(player)) {
            playerNameMap.remove(player).save();
        }
    }
}
