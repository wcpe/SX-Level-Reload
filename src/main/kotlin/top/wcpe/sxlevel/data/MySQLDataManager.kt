package top.wcpe.sxlevel.data

import github.saukiya.sxlevel.SXLevel
import redis.clients.jedis.Jedis
import top.wcpe.sxlevel.entity.PlayerLevel
import top.wcpe.sxlevel.mapper.PlayerLevelMapper
import top.wcpe.wcpelib.bukkit.WcpeLib

/**
 * 由 WCPE 在 2022/7/9 19:50 创建
 *
 * Created by WCPE on 2022/7/9 19:50
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.1.0-alpha-dev-1
 */
class MySQLDataManager : IDataManager {
    init {
        WcpeLib.getMybatis().sqlSessionFactory.openSession().use {
            val playerDataMapper = it.getMapper(PlayerLevelMapper::class.java)
            if (playerDataMapper.existTable(WcpeLib.getMybatis().databaseName) == 0) {
                playerDataMapper.createTable()
            }
            it.commit()
        }
    }

    companion object {
        private const val REDIS_KEY = "SX-Level"
    }

    private fun getResource(): Jedis {
        val resource = WcpeLib.getRedis().getResource()
        resource.select(SXLevel.instance.config.getInt("redis.index"))
        return resource
    }

    private fun setValue(resource: Jedis, redisKey: String, value: String) {
        resource[redisKey] = value
        resource.expire(redisKey, SXLevel.instance.config.getLong("redis.expire"))
    }

    override fun getPlayerLevel(playerName: String): PlayerLevel {
        val redisKey = "$REDIS_KEY:$playerName"
        getResource().use { resource ->

            val value = resource.get(redisKey)

            if (value != null) {
                return PlayerLevel.stringToThis(playerName, value)
            }
            WcpeLib.getMybatis().sqlSessionFactory.openSession().use { sqlSession ->
                val playerLevelMapper = sqlSession.getMapper(PlayerLevelMapper::class.java)
                return playerLevelMapper.getPlayerLevel(playerName) ?: PlayerLevel(playerName)
                    .also {
                        playerLevelMapper.insertPlayerLevel(it)
                        sqlSession.commit()
                        setValue(resource, redisKey, it.saveToString())
                    }
            }
        }
    }

    override fun savePlayerLevel(playerLevel: PlayerLevel): Boolean {
        val redisKey = "$REDIS_KEY:${playerLevel.playerName}"
        getResource().use { resource ->
            WcpeLib.getMybatis().sqlSessionFactory.openSession().use { sqlSession ->
                val mapper = sqlSession.getMapper(PlayerLevelMapper::class.java)

                return try {
                    mapper.updatePlayerLevel(playerLevel)
                    sqlSession.commit()
                    setValue(resource, redisKey, playerLevel.saveToString())
                    true
                } catch (e: Exception) {
                    sqlSession.rollback()
                    false
                }
            }
        }
    }
}