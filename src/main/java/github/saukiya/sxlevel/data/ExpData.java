package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import lombok.Data;
import org.bukkit.entity.Player;
import top.wcpe.sxlevel.entity.PlayerLevel;

/**
 * @author Saukiya
 * @since 2018年5月7日
 */

@Data
public class ExpData {
    private PlayerLevel playerLevel;

    public ExpData(Player player) {
        this.playerLevel = SXLevel.getDataManager().getPlayerLevel(player.getName());
    }

    public ExpData(PlayerLevel playerLevel) {
        this.playerLevel = playerLevel;
    }

    public int getLevel() {
        return playerLevel.getLevel();
    }

    public void setLevel(int level) {
        playerLevel.setLevel(level);
    }

    public int getExp() {
        return playerLevel.getExp();
    }

    public void setExp(int exp) {
        playerLevel.setExp(exp);
    }

    public Boolean hasExp(int hasExp) {
        return playerLevel.hasExp(hasExp);
    }

    public void takeExp(int takeExp) {
        playerLevel.takeExp(takeExp);
    }

    public void addExp(int addExp) {
        playerLevel.addExp(addExp);
    }

    public void updateDefaultExp() {
        playerLevel.updateDefaultExp();
    }

    @Deprecated
    public int getMaxExp() {
        return getMaxExp(playerLevel.getPlayerName());
    }

    @Deprecated
    public int getMaxExp(String playerName) {
        return playerLevel.getMaxExpValue(playerName);
    }

    public int getMaxLevel() {
        return SXLevel.getInstance().getConfiguration().getMaxLevel();
    }

    public void save() {
        playerLevel.save();
    }
}
