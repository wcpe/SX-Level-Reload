package top.wcpe.sxlevel

import org.bukkit.configuration.file.FileConfiguration

/**
 * 由 WCPE 在 2023/12/22 16:38 创建
 * <p>
 * Created by WCPE on 2023/12/22 16:38
 * <p>
 * <p>
 * GitHub  : <a href="https://github.com/wcpe">wcpe 's GitHub</a>
 * <p>
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.5.0-SNAPSHOT
 */
class Configuration(config: FileConfiguration) {
    val mysql: Boolean = config.getBoolean("mysql")
    val updateDefaultExp = config.getBoolean("update-default-exp")
    val disabledDefaultExpChange = config.getBoolean("disabled-default-exp-change")
    val defaultExpEnable = config.getBoolean("default-exp.enable")
    val defaultExpValue = config.getDouble("default-exp.value")

    val levelSureExpMap: Map<Int, LevelSureExp>

    val levelUpStringActionMap: Map<Int, List<String>>

    val maxLevel: Int

    init {
        val levelSureExpMap = linkedMapOf<Int, LevelSureExp>()
        val levelSureExp = config.getStringList("level-sure-exp")
        var last: LevelSureExp? = null
        for (s in levelSureExp) {
            val split = s.split(":")
            if (split.size <= 1) {
                continue
            }
            val level = split[0].toIntOrNull() ?: continue
            val exp = split[1].toIntOrNull() ?: continue
            val permission = if (split.size > 2) {
                split[2]
            } else {
                ""
            }
            last = LevelSureExp(level, exp, permission)
            levelSureExpMap[level] = last
        }
        maxLevel = last?.level ?: 1
        this.levelSureExpMap = levelSureExpMap

        val levelUpStringActionMap = linkedMapOf<Int, List<String>>()
        val levelUpStringAction = config.getConfigurationSection("level-up-string-action")
        if (levelUpStringAction != null) {

            for (key in levelUpStringAction.getKeys(false)) {
                val stringAction = levelUpStringAction.getStringList(key)
                levelUpStringActionMap[key.toIntOrNull() ?: continue] = stringAction
            }

        }
        this.levelUpStringActionMap = levelUpStringActionMap
    }

    data class LevelSureExp(
        val level: Int,
        val sureExp: Int,
        val surePermission: String
    )

}