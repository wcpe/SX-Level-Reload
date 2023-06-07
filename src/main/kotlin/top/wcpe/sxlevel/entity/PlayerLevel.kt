package top.wcpe.sxlevel.entity

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import github.saukiya.sxlevel.SXLevel
import github.saukiya.sxlevel.data.ExpData
import github.saukiya.sxlevel.event.SXLevelUpEvent
import github.saukiya.sxlevel.util.Message
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import top.wcpe.wcpelib.bukkit.utils.StringActionUtil
import java.util.function.Consumer

/**
 * 由 WCPE 在 2022/7/23 4:39 创建
 *
 * Created by WCPE on 2022/7/23 4:39
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v1.1.0-alpha-dev-1
 */
data class PlayerLevel(
    val playerName: String, var exp: Int = 0, var level: Int = 0
) {

    private fun runPlayer(callBack: Consumer<Player>) {
        val playerExact = Bukkit.getPlayerExact(playerName)
        if (playerExact == null || !playerExact.isOnline) {
            return
        }
        callBack.accept(playerExact)
    }

    constructor(playerName: String, yamlConfiguration: YamlConfiguration) : this(
        playerName, yamlConfiguration.getInt("Exp"), yamlConfiguration.getInt("Level")
    ) {
        updateDefaultExp()
    }

    fun serialize(): YamlConfiguration {
        return YamlConfiguration().apply {
            this["Exp"] = exp
            this["Level"] = level
        }
    }

    companion object {

        @JvmStatic
        val playerLevelAdapter: JsonAdapter<PlayerLevel> =
            Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(PlayerLevel::class.java)

        fun stringToThis(playerName: String, data: String): PlayerLevel {
            return playerLevelAdapter.fromJson(data) ?: PlayerLevel(playerName)
        }
    }


    fun saveToString(): String {
        return playerLevelAdapter.toJson(this)
    }

    fun toExpData(): ExpData {
        return ExpData(this)
    }

    fun hasExp(hasExp: Int): Boolean {
        var tempHasExp = hasExp
        var tempLevel = level
        var tempExp = exp
        while (tempHasExp > 0) {
            if (tempLevel <= 0 && tempHasExp > tempExp) {
                return false
            }
            if (tempHasExp > tempExp) {
                tempHasExp -= tempExp
                tempLevel--
                tempExp = getMaxExp()
            } else {
                return true
            }
        }
        return true
    }

    fun takeExp(take: Int) {
        takeExp(take, "")
    }

    fun takeExp(take: Int, vararg tipAdditionMessage: String) {
        var takeExp = take
        val change: Int = takeExp
        while (takeExp > 0) {
            if (level <= 0 && takeExp > exp) {
                exp = 0
                break
            }
            if (takeExp > exp) {
                takeExp -= exp
                level--
                exp = getMaxExp()
            } else {
                exp -= takeExp
                break
            }
        }
        save()
        updateDefaultExp()
        runPlayer { player ->
            Message.send(
                player, Message.getMsg(
                    Message.PLAYER__EXP, this.level, this.exp, getMaxExp(), "§c§l-$change", *tipAdditionMessage
                )
            )
        }

    }

    fun addExp(add: Int) {
        addExp(add, "")
    }

    fun addExp(add: Int, vararg tipAdditionMessage: String) {
        if (getMaxLevel() <= level) {
            runPlayer {
                Message.send(it, Message.getMsg(Message.PLAYER__MAX_LEVEL))
            }
            return
        }

        var tempAddExp = add
        var levelUp = false
        while (tempAddExp > 0) {
            val maxExp = getMaxExp()
            if (maxExp == 0) {
                break
            }
            // 升级
            val totalExp = exp + tempAddExp
            if (totalExp >= maxExp) {
                tempAddExp = totalExp - maxExp
                exp = 0
                level += 1
                runPlayer {
                    StringActionUtil.executionCommands(
                        SXLevel.instance.config.getStringList("level-up-string-action.$level"), false, it
                    )
                }
                levelUp = true
            } else {
                exp += tempAddExp
                break
            }
        }
        save()
        updateDefaultExp()
        runPlayer {
            Message.send(
                it, Message.getMsg(
                    Message.PLAYER__EXP, level, exp, getMaxExp(), "§e§l+$add", *tipAdditionMessage
                )
            )
        }
        if (!levelUp) {
            runPlayer {
                it.world.playSound(
                    it.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, add / 50f, add / 20f
                )
            }

        } else {
            runPlayer { player ->
                Bukkit.getPluginManager().callEvent(SXLevelUpEvent(player, ExpData(this)))
                Message.send(
                    player, Message.getMsg(
                        Message.PLAYER__LEVEL_UP, level, getMaxExp()
                    )
                )
                player.world.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, add / 20f, add / 20f)
            }

        }
    }


    fun updateDefaultExp() {
        if (SXLevel.instance.config.getBoolean("update-default-exp")) {
            runPlayer { player ->
                player.level = this.level

                val maxExp = getMaxExp()
                if (maxExp != 0) {
                    if (maxExp < exp) {
                        player.exp = 1.0f
                    } else {
                        player.exp = exp / maxExp.toFloat()
                    }
                } else {
                    player.exp = 0.0f
                }
            }
        }
    }

    fun getMaxExp(): Int {
        if (getMaxLevel() <= level) {
            return 0
        }

        val levelSureExp = SXLevel.instance.config.getStringList("level-sure-exp")

        var maxExp = 0
        var tempLevel = -1
        for (string in levelSureExp) {
            val stringSplit = string.split(":")
            if (stringSplit.size == 2) {
                tempLevel = stringSplit[0].toInt()
                maxExp = stringSplit[1].toInt()
            } else {
                tempLevel++
                maxExp = string.toInt()
            }
            if (this.level <= tempLevel) {
                break
            }
        }

        return maxExp
    }

    fun getMaxLevel(): Int {
        val levelSureExp = SXLevel.instance.config.getStringList("level-sure-exp")
        return levelSureExp[levelSureExp.size - 1].split(":")[0].toInt()
    }

    fun save() {
        SXLevel.dataManager.savePlayerLevel(this)
    }
}