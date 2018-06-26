package github.saukiya.sxlevel.api;

import github.saukiya.sxlevel.data.PlayerExpDataManager;
import github.saukiya.sxlevel.data.PlayerExpData;
import org.bukkit.entity.Player;

public class SXLevelAPI {
    /**
     * 获取玩家的经验数据
     *
     * @param player 玩家
     * @return PlayerExpData 数据
     */
    public static PlayerExpData getPlayerData(Player player) {
        return PlayerExpDataManager.getPlayerData(player);
    }
}
