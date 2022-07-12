package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author Saukiya
 * @since 2018年3月28日
 */

public class ExpDataManager {

    private final HashMap<String, ExpData> map = new HashMap<>();
    private final SXLevel plugin;

    public ExpDataManager(SXLevel plugin) {
        this.plugin = plugin;
        autoSave();
    }

    public HashMap<String, ExpData> getMap() {
        return map;
    }

    public void saveAllData() {
        map.values().forEach(ExpData::save);
    }

    /**
     * 定时保存玩家数据
     */
    private void autoSave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SXLevel.getPlugin(), () -> {
                    saveAllData();
                    plugin.log(Message.getMessagePrefix() + "保存数据!");
                }, Config.getConfig().getInt(Config.AUTO_SAVE_TICK, 6000),
                Config.getConfig().getInt(Config.AUTO_SAVE_TICK, 6000));
    }

    public ExpData getPlayerData(Player player) {
        ExpData playerData = map.get(player.getName());
        if (playerData == null) {
            map.put(player.getName(), new ExpData(player));
        }
        return map.get(player.getName());
    }

    public ExpData getPlayerData(Player player, Integer level, Integer exp) {
        ExpData playerData = map.get(player.getName());
        if (playerData == null) {
            map.put(player.getName(), new ExpData(player, level, exp));
        }
        return map.get(player.getName());
    }

    public void saveData(Player player) {
        ExpData playerData = map.get(player.getName());
        if (playerData != null) {
            playerData.save();
            map.remove(player.getName());
        }
    }
}
