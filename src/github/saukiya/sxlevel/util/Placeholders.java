package github.saukiya.sxlevel.util;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.data.ExpDataManager;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class Placeholders extends EZPlaceholderHook {

    @SuppressWarnings("unused")
    private SXLevel ourPlugin;

    public Placeholders(SXLevel ourPlugin) {
        super(ourPlugin, "sl");
        this.ourPlugin = ourPlugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String string) {
        DecimalFormat df = new DecimalFormat("#.##");
        double d = 0;
        ExpData playerData = ExpDataManager.getPlayerData(player);
        if (string.equalsIgnoreCase("exp")) {
            d = playerData.getExp();
        } else if (string.equalsIgnoreCase("expPercentage")) {
            if(playerData.getMaxExp() != 0){
                d = playerData.getExp()/playerData.getMaxExp();
            }
        } else if (string.equalsIgnoreCase("maxExp")) {
            d = playerData.getMaxExp();
        } else if (string.equalsIgnoreCase("level")) {
            d = playerData.getLevel();
        } else if (string.equalsIgnoreCase("maxLevel")) {
            d = playerData.getMaxLevel();
        } else {
            return "§c变量列表: exp/expPercentage/maxExp/level/maxLevel";
        }
        return df.format(d);
    }

}
