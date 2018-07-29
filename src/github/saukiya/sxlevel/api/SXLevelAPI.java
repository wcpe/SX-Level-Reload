package github.saukiya.sxlevel.api;

import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.data.ExpDataManager;
import org.bukkit.entity.Player;

public class SXLevelAPI {
    /**
     * 获取玩家的经验数据
     *
     * @param player 玩家
     * @return ExpData 数据
     */
    public static ExpData getPlayerData(Player player) {
        return ExpDataManager.getPlayerData(player);
    }
}
