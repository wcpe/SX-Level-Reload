package github.saukiya.sxlevel.listener;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.event.ChangeType;
import github.saukiya.sxlevel.event.SXExpChangeEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;

/**
 * @author Saukiya
 * @since 2018年5月2日
 */

public class OnMythicmobsDeathListener implements Listener {

    private final SXLevel plugin;

    public OnMythicmobsDeathListener(SXLevel plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onMythicMobDeathEvent(MythicMobDeathEvent event) {
        MythicMob mm = event.getMobType();
        List<String> dropList = mm.getDrops();
        if (event.getKiller() instanceof Player) {
            for (String str : dropList) {
                if (str.contains(" ")) {
                    String[] args = str.split(" ");
                    if (args.length > 1 && args[0].equalsIgnoreCase("sExp")) {
                        int addExp = 1;
                        if (args.length > 2 && args[2].length() > 0
                                && new Random().nextDouble() > Double.valueOf(args[2].replaceAll("[^0-9.]", ""))) {// 几率判断
                            continue;
                        }
                        if (args[1].length() > 0) {// 数量判断
                            if (args[1].contains("-") && args[1].split("-").length > 1) {
                                int i1 = Integer.valueOf(args[1].split("-")[0].replaceAll("[^0-9]", ""));
                                int i2 = Integer.valueOf(args[1].split("-")[1].replaceAll("[^0-9]", ""));
                                if (i1 > i2) {
                                    Bukkit.getConsoleSender().sendMessage(
                                            "[" + SXLevel.getPlugin().getName() + "] §c随机数大小不正确!: §4" + str);
                                } else {
                                    addExp = new Random().nextInt(i2 - i1 + 1) + i1;
                                }
                            } else {
                                addExp = Integer.valueOf(args[1].replaceAll("[^0-9]", ""));
                            }
                        }
                        ExpData playerData = plugin.getExpDataManager()
                                .getPlayerData((Player) event.getKiller());
                        SXExpChangeEvent sxExpChangeEvent = new SXExpChangeEvent((Player) event.getKiller(), playerData,
                                addExp, ChangeType.ADD);
                        Bukkit.getPluginManager().callEvent(sxExpChangeEvent);
                        if (!sxExpChangeEvent.isCancelled()) {
                            playerData.addExp(sxExpChangeEvent.getAmount());
                        }
                    }
                }
            }
        }
    }
}
