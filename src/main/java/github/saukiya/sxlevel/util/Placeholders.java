package github.saukiya.sxlevel.util;

import github.saukiya.sxlevel.SXLevel;
import lombok.val;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class Placeholders extends EZPlaceholderHook {

    private final SXLevel plugin;

    public Placeholders(SXLevel plugin) {
        super(plugin, "sl");
        this.plugin = plugin;
        this.hook();
    }

    @Override
    public String onPlaceholderRequest(Player player, String string) {
        DecimalFormat df = new DecimalFormat("#.##");
        double d = 0;
        final val playerLevel = SXLevel.getDataManager().getPlayerLevel(player.getName());
        if (string.equalsIgnoreCase("exp")) {
            d = playerLevel.getExp();
        } else if (string.equalsIgnoreCase("expPercentage")) {
            if (playerLevel.getMaxExp() != 0) {
                d = (double) playerLevel.getExp() / playerLevel.getMaxExp();
            }
        } else if (string.equalsIgnoreCase("maxExp")) {
            d = playerLevel.getMaxExp();
        } else if (string.equalsIgnoreCase("level")) {
            d = playerLevel.getLevel();
        } else if (string.equalsIgnoreCase("maxLevel")) {
            d = playerLevel.getMaxLevel();
        } else {
            return "§c变量填写错误";
        }
        return df.format(d);
    }

}
