package top.wcpe.sxlevel.mapper

import org.apache.ibatis.annotations.*
import top.wcpe.sxlevel.entity.PlayerLevel

/**
 * 由 WCPE 在 2022/7/9 20:25 创建
 *
 * Created by WCPE on 2022/7/9 20:25
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v
 */
interface PlayerLevelMapper {

    @Select("select count(*) from information_schema.TABLES t where t.TABLE_SCHEMA =#{databaseName} and t.TABLE_NAME = 'sxlevel_player_level'")
    fun existTable(databaseName: String): Int

    @Update("create table `sxlevel_player_level` (`player_name` varchar(32) NOT NULL, `exp` int NOT NULL, `level` int NOT NULL, PRIMARY KEY (`player_name`));")
    fun createTable()

    @ResultMap("playerLevelMap")
    @Select("select player_name,exp,level from `sxlevel_player_level` where `player_name`= #{playerName};")
    fun getPlayerLevel(@Param("playerName") playerName: String): PlayerLevel?

    @Results(
        id = "playerLevelMap",
        value = [
            Result(column = "player_name", property = "playerName", id = true),
            Result(column = "exp", property = "exp"),
            Result(column = "level", property = "level")
        ]
    )
    @Select("select player_name,exp,level from `sxlevel_player_level`;")
    fun listPlayerLevel(): List<PlayerLevel?>

    @ResultMap("playerLevelMap")
    @Insert("insert INTO `sxlevel_player_level`(player_name, exp, level) VALUES (#{playerName},#{exp},#{level})")
    fun insertPlayerLevel(playerLevel: PlayerLevel)

    @Update("update `sxlevel_player_level` SET exp= #{exp}, level=#{level} WHERE player_name=#{playerName};")
    fun updatePlayerLevel(playerLevel: PlayerLevel)
}