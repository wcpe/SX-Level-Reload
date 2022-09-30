package top.wcpe.sxlevel

import github.saukiya.sxlevel.SXLevel
import top.wcpe.sxlevel.entity.PlayerLevel

/**
 * 由 WCPE 在 2022/7/24 5:08 创建
 *
 * Created by WCPE on 2022/7/24 5:08
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.1.0-alpha-dev-1
 */
object SXLevelAPI {
    @JvmStatic
    fun getPlayerLevel(playerName: String): PlayerLevel {
        return SXLevel.dataManager.getPlayerLevel(playerName)
    }
}