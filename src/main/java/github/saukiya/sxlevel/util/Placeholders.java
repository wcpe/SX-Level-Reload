package github.saukiya.sxlevel.util;

import github.saukiya.sxlevel.SXLevel;
import lombok.val;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class Placeholders extends EZPlaceholderHook {

    public Placeholders(SXLevel plugin) {
        super(plugin, "sl");
        this.hook();
    }

    @Override
    public String onPlaceholderRequest(Player player, String string) {
        if (player == null) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#.##");
        double d = 0;
        val playerLevel = SXLevel.getDataManager().getPlayerLevel(player.getName());
        if (string.equalsIgnoreCase("exp")) {
            d = playerLevel.getExp();
        } else if (string.equalsIgnoreCase("expPercentage")) {
            val maxExp = playerLevel.getMaxExpValue(player.getName());
            if (maxExp != 0) {
                d = (double) playerLevel.getExp() / maxExp;
            }
        } else if (string.equalsIgnoreCase("maxExp")) {
            d = playerLevel.getMaxExpValue(player.getName());
        } else if (string.equalsIgnoreCase("level")) {
            d = playerLevel.getLevel();
        } else if (string.equalsIgnoreCase("maxLevel")) {
            d = SXLevel.getInstance().getConfiguration().getMaxLevel();
        } else {
            return "§c变量填写错误";
        }
        return df.format(d);
    }

}
