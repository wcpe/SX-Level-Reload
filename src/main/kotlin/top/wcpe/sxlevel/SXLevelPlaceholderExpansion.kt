package top.wcpe.sxlevel

import github.saukiya.sxlevel.SXLevel
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import java.text.DecimalFormat

/**
 * 由 WCPE 在 2022/5/26 13:42 创建
 *
 * Created by WCPE on 2022/5/26 13:42
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v
 */
class SXLevelPlaceholderExpansion : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "SX-Level"
    }

    override fun getRequiredPlugin(): String {
        return "SX-Level"
    }

    override fun getAuthor(): String {
        return "WCPE"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onPlaceholderRequest(player: Player, identifier: String): String {
        val df = DecimalFormat("#.##")
        var d = 0.0
        val playerLevel = SXLevel.dataManager.getPlayerLevel(player.name)
        if (identifier.equals("exp", ignoreCase = true)) {
            d = playerLevel.exp.toDouble()
        } else if (identifier.equals("expPercentage", ignoreCase = true)) {
            val maxExp = playerLevel.getMaxExp()
            if (maxExp != 0) {
                d = playerLevel.exp.toDouble() / maxExp
            }
        } else if (identifier.equals("maxExp", ignoreCase = true)) {
            d = playerLevel.getMaxExp().toDouble()
        } else if (identifier.equals("level", ignoreCase = true)) {
            d = playerLevel.level.toDouble()
        } else if (identifier.equals("maxLevel", ignoreCase = true)) {
            d = playerLevel.getMaxLevel().toDouble()
        } else {
            return "§c变量填写错误"
        }
        return df.format(d)
    }
}