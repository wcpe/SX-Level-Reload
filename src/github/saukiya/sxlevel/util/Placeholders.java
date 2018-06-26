package github.saukiya.sxlevel.util;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.data.PlayerExpDataManager;
import github.saukiya.sxlevel.data.PlayerExpData;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

public class Placeholders extends EZPlaceholderHook {

    @SuppressWarnings("unused")
    private SXLevel ourPlugin;

    public Placeholders(SXLevel ourPlugin) {
        super(ourPlugin, "sl");
        this.ourPlugin = ourPlugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String string) {
        int i = -1;
        PlayerExpData playerData = PlayerExpDataManager.getPlayerData(player);
        if (string.equalsIgnoreCase("exp")) {
            i = playerData.getExp();
        } else if (string.equalsIgnoreCase("maxExp")) {
            i = playerData.getMaxExp();
        } else if (string.equalsIgnoreCase("level")) {
            i = playerData.getLevel();
        } else if (string.equalsIgnoreCase("maxLevel")) {
            i = playerData.getMaxLevel();
        }
        return String.valueOf(i);
    }

}
