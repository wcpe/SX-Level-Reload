package top.wcpe.sxlevel.data

import top.wcpe.sxlevel.entity.PlayerLevel
import top.wcpe.sxlevel.mapper.PlayerLevelMapper
import top.wcpe.wcpelib.common.mybatis.Mybatis
import top.wcpe.wcpelib.common.redis.Redis

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
class MySQLDataManager(private val mybatis: Mybatis, private val redis: Redis) : IDataManager {
    init {
        mybatis.addMapper(PlayerLevelMapper::class.java)
        mybatis.sqlSessionFactory.openSession(true).use {
            it.getMapper(PlayerLevelMapper::class.java).createTable()
        }
    }

    companion object {
        private const val REDIS_KEY = "SX-Level"
    }


    override fun getPlayerLevel(playerName: String): PlayerLevel {
        val redisKey = "$REDIS_KEY:$playerName"
        redis.getResourceProxy().use { resource ->

            val value = resource.get(redisKey)

            if (value != null) {
                return PlayerLevel.stringToThis(playerName, value)
            }
            mybatis.sqlSessionFactory.openSession().use { sqlSession ->
                val playerLevelMapper = sqlSession.getMapper(PlayerLevelMapper::class.java)
                return playerLevelMapper.getPlayerLevel(playerName) ?: PlayerLevel(playerName)
                    .also {
                        playerLevelMapper.insertPlayerLevel(it)
                        sqlSession.commit()
                        resource[redisKey] = it.saveToString()
                    }
            }
        }
    }

    override fun savePlayerLevel(playerLevel: PlayerLevel): Boolean {
        val redisKey = "$REDIS_KEY:${playerLevel.playerName}"
        redis.getResourceProxy().use { resource ->
            mybatis.sqlSessionFactory.openSession().use { sqlSession ->
                val mapper = sqlSession.getMapper(PlayerLevelMapper::class.java)

                return try {
                    mapper.updatePlayerLevel(playerLevel)
                    sqlSession.commit()
                    resource[redisKey] = playerLevel.saveToString()
                    true
                } catch (e: Exception) {
                    sqlSession.rollback()
                    false
                }
            }
        }
    }
}