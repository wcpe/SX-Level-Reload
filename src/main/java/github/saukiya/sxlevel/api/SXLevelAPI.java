package github.saukiya.sxlevel.api;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.data.ExpData;
import org.bukkit.entity.Player;

public class SXLevelAPI {
    /**
     * 获取玩家的经验数据
     *
     * @param player 玩家
     * @return ExpData 数据
     */
    public ExpData getPlayerData(Player player) {
        return SXLevel.dataManager.getPlayerLevel(player.getName()).toExpData();
    }
}
