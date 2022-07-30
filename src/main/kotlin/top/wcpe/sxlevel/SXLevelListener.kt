package top.wcpe.sxlevel

import github.saukiya.sxlevel.SXLevel
import github.saukiya.sxlevel.event.ChangeType
import github.saukiya.sxlevel.event.SXExpChangeEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerJoinEvent

/**
 * 由 WCPE 在 2022/7/23 4:25 创建
 *
 * Created by WCPE on 2022/7/23 4:25
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v
 */
class SXLevelListener : Listener {
    @EventHandler
    fun listenerPlayerJoinEvent(e: PlayerJoinEvent) {
        SXLevel.dataManager.getPlayerLevel(e.player.name).updateDefaultExp()
    }

    @EventHandler
    fun onPlayerExpChangeEvent(e: PlayerExpChangeEvent) {
        val addExp = (e.amount * SXLevel.instance.config.getDouble("default-exp.value")).toInt()
        if (SXLevel.instance.config.getBoolean("disabled-default-exp-change")) {
            e.amount = 0
        }
        if (SXLevel.instance.config.getBoolean("default-exp.enable")) {
            val playerLevel = SXLevel.dataManager.getPlayerLevel(e.player.name)
            val sxExpChangeEvent = SXExpChangeEvent(
                e.player, playerLevel.toExpData(), addExp,
                ChangeType.ADD
            )
            Bukkit.getPluginManager().callEvent(sxExpChangeEvent)
            if (!sxExpChangeEvent.isCancelled) {
                playerLevel.addExp(sxExpChangeEvent.amount)
            }
        }
    }

}