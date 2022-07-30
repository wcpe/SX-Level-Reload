package top.wcpe.sxlevel.data

import github.saukiya.sxlevel.SXLevel
import org.bukkit.configuration.file.YamlConfiguration
import top.wcpe.sxlevel.entity.PlayerLevel
import java.nio.file.Files
import java.nio.file.Path

/**
 * 由 WCPE 在 2022/7/9 19:49 创建
 *
 * Created by WCPE on 2022/7/9 19:49
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v
 */
class FileDataManager : IDataManager {

    private val playerLevelMap: MutableMap<String, PlayerLevel> = mutableMapOf()

    private fun getPlayerLevelDirPath(): Path {
        val playerDataDirPath = SXLevel.instance.dataFolder.toPath().resolve("PlayerData")
        if (Files.notExists(playerDataDirPath)) {
            Files.createDirectories(playerDataDirPath)
        }
        return playerDataDirPath
    }

    private fun getPlayerLevelFilePath(playerName: String): Path {
        val playerDataFilePath = getPlayerLevelDirPath().resolve("$playerName.yml")
        if (Files.notExists(playerDataFilePath)) {
            Files.createFile(playerDataFilePath)
        }
        return playerDataFilePath
    }

    override fun getPlayerLevel(playerName: String): PlayerLevel {
        return playerLevelMap.computeIfAbsent(playerName) {
            PlayerLevel(playerName, YamlConfiguration.loadConfiguration(getPlayerLevelFilePath(playerName).toFile()))
        }
    }

    override fun savePlayerLevel(playerLevel: PlayerLevel): Boolean {
        return try {
            playerLevel.serialize().save(getPlayerLevelFilePath(playerLevel.playerName).toFile())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}