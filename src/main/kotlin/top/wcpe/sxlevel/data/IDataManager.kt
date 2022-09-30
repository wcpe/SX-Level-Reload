package top.wcpe.sxlevel.data

import top.wcpe.sxlevel.entity.PlayerLevel

/**
 * 由 WCPE 在 2022/7/9 19:47 创建
 *
 * Created by WCPE on 2022/7/9 19:47
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.1.0-alpha-dev-1
 */
interface IDataManager {

    fun getPlayerLevel(playerName: String): PlayerLevel

    fun savePlayerLevel(playerLevel: PlayerLevel): Boolean

}