package top.wcpe.sxlevel

import github.saukiya.sxlevel.event.ChangeType
import github.saukiya.sxlevel.event.SXExpChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.serverct.ersha.AttributePlus.attributeManager
import org.serverct.ersha.api.enums.AttributeName


/**
 * 由 WCPE 在 2023/6/7 2:21 创建
 * <p>
 * Created by WCPE on 2023/6/7 2:21
 * <p>
 * <p>
 * GitHub  : <a href="https://github.com/wcpe">wcpe 's GitHub</a>
 * <p>
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.1.2-alpha-dev-1
 */
class AttributePlusListener : Listener {
    @EventHandler
    private fun listenerSXExpChangeEvent(event: SXExpChangeEvent) {
        if (event.isCancelled || event.type != ChangeType.ADD) {
            return
        }
        val player = event.player
        val data = attributeManager.getAttributeData(player.uniqueId, player)
        val exp = event.amount * data.getRandomValue(AttributeName.EXP_ADDITION.toServerName()).toDouble() / 100
        event.amount = (event.amount + exp).toInt()
        event.addAdditionMessage("$exp")
    }
}