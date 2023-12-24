package top.wcpe.sxlevel

import github.saukiya.sxlevel.SXLevel
import github.saukiya.sxlevel.event.ChangeType
import github.saukiya.sxlevel.event.SXExpChangeEvent
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
 * @since  : v1.1.0-alpha-dev-1
 */
class SXLevelListener : Listener {
    @EventHandler
    fun listenerPlayerJoinEvent(e: PlayerJoinEvent) {
        SXLevel.dataManager.getPlayerLevel(e.player.name).updateDefaultExp()
    }

    @EventHandler
    fun onPlayerExpChangeEvent(e: PlayerExpChangeEvent) {
        val addExp = (e.amount * SXLevel.instance.configuration.defaultExpValue).toInt()
        if (SXLevel.instance.configuration.disabledDefaultExpChange) {
            e.amount = 0
        }
        if (!SXLevel.instance.configuration.defaultExpEnable) {
            return
        }
        val playerLevel = SXLevel.dataManager.getPlayerLevel(e.player.name)

        val sxExpChangeEvent = SXExpChangeEvent.callEvent(e.player, playerLevel.toExpData(), addExp, ChangeType.ADD)
        if (!sxExpChangeEvent.isCancelled) {
            playerLevel.addExp(sxExpChangeEvent.amount, *sxExpChangeEvent.tipAdditionMessageList.toTypedArray())
        }

    }

}