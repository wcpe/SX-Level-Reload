package github.saukiya.sxlevel.listener;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.event.ChangeType;
import github.saukiya.sxlevel.event.SXExpChangeEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;

/**
 * @author Saukiya
 * @since 2018年5月2日
 */

public class OnMythicmobsDeathListener implements Listener {
    private final Random random = new Random();
    private final String regex_1 = "[^0-9.]";
    private final String regex_2 = "[^0-9]";

    @EventHandler
    void onMythicMobDeathEvent(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player)) {
            return;
        }
        val mobType = event.getMobType();
        for (String str : mobType.getConfig().getStringList("Drops")) {
            if (str.contains(" ")) {
                String[] args = str.split(" ");
                if (args.length > 1 && args[0].equalsIgnoreCase("sExp")) {
                    int addExp = 1;
                    if (args.length > 2 && args[2].length() > 0 && random.nextDouble() > Double.parseDouble(args[2].replaceAll(regex_1, ""))) {// 几率判断
                        continue;
                    }
                    if (args[1].length() > 0) {// 数量判断
                        if (args[1].contains("-") && args[1].split("-").length > 1) {
                            int i1 = Integer.parseInt(args[1].split("-")[0].replaceAll(regex_2, ""));
                            int i2 = Integer.parseInt(args[1].split("-")[1].replaceAll(regex_2, ""));
                            if (i1 > i2) {
                                Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getInstance().getName() + "] §c随机数大小不正确!: §4" + str);
                            } else {
                                addExp = new Random().nextInt(i2 - i1 + 1) + i1;
                            }
                        } else {
                            addExp = Integer.parseInt(args[1].replaceAll(regex_2, ""));
                        }
                    }
                    val playerLevel = SXLevel.getDataManager().getPlayerLevel(event.getKiller().getName());
                    SXExpChangeEvent sxExpChangeEvent = SXExpChangeEvent.callEvent((Player) event.getKiller(), playerLevel.toExpData(), addExp, ChangeType.ADD);
                    if (!sxExpChangeEvent.isCancelled()) {
                        playerLevel.addExp(sxExpChangeEvent.getAmount(), sxExpChangeEvent.getTipAdditionMessageList().toArray(new String[]{}));
                    }
                }
            }
        }
    }
}
