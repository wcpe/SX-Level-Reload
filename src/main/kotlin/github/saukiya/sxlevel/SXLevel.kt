package github.saukiya.sxlevel

import github.saukiya.sxlevel.api.SXLevelAPI
import github.saukiya.sxlevel.command.MainCommand
import github.saukiya.sxlevel.listener.OnMythicmobsDeathListener
import github.saukiya.sxlevel.util.Message
import github.saukiya.sxlevel.util.Placeholders
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.plugin.java.JavaPlugin
import top.wcpe.sxlevel.SXLevelListener
import top.wcpe.sxlevel.SXLevelPlaceholderExpansion
import top.wcpe.sxlevel.data.FileDataManager
import top.wcpe.sxlevel.data.IDataManager
import top.wcpe.sxlevel.data.MySQLDataManager
import top.wcpe.sxlevel.mapper.PlayerLevelMapper
import top.wcpe.wcpelib.bukkit.WcpeLib
import java.io.IOException
import java.util.stream.IntStream

/**
 * 由 WCPE 在 2022/7/23 5:41 创建
 *
 * Created by WCPE on 2022/7/23 5:41
 *
 * GitHub  : https://github.com/wcpe
 * QQ      : 1837019522
 * @author : WCPE
 * @since  : v
 */
class SXLevel : JavaPlugin() {

    companion object {
        @JvmStatic
        private val versionSplit = IntArray(3)

        @JvmStatic
        lateinit var instance: SXLevel

        @JvmStatic
        lateinit var api: SXLevelAPI

        @JvmStatic
        lateinit var dataManager: IDataManager

    }

    @Deprecated("please use getInstance()", ReplaceWith("instance", "github.saukiya.sxlevel.SXLevel.instance"))
    fun getPlugin(): SXLevel {
        return instance
    }

    override fun onEnable() {
        val oldTimes = System.currentTimeMillis()
        instance = this
        saveDefaultConfig()
        api = SXLevelAPI()
        try {
            Message.loadMessage()
        } catch (e: IOException) {
            e.printStackTrace()
            logger.info(Message.getMessagePrefix() + "§cError!")
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
            logger.info(Message.getMessagePrefix() + "§cError!")
        }
        dataManager = FileDataManager()
        if (config.getBoolean("mysql")) {
            if (WcpeLib.isEnableMysql()) {
                WcpeLib.getMybatis().addMapper(PlayerLevelMapper::class.java)
                dataManager = MySQLDataManager()
            } else {
                logger.info("WcpeLib 未开启连接数据库 将使用本地文件存储!")
            }
        }
        MainCommand(this).setUp("sxLevel")
        server.pluginManager.registerEvents(SXLevelListener(), this)

        val version =
            Bukkit.getBukkitVersion().split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].replace(
                " ",
                ""
            )
        logger.info(Message.getMessagePrefix() + "服务器版本: " + version)
        // SplitVersion
        val strSplit = version.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        IntStream.range(0, strSplit.size).forEachOrdered { i: Int ->
            versionSplit[i] = Integer.valueOf(strSplit[i])
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Placeholders(this)
            SXLevelPlaceholderExpansion().register()
            logger.info(Message.getMessagePrefix() + "§cPlaceholderAPI 不存在!")
        }
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            logger.info(Message.getMessagePrefix() + "§aMythicMobs 成功加载!")
            Bukkit.getPluginManager().registerEvents(OnMythicmobsDeathListener(), this)
        } else {
            logger.info(Message.getMessagePrefix() + "§cMythicMobs 不存在!")
        }
        logger.info("加载时间: §c" + (System.currentTimeMillis() - oldTimes) + "§7 ms")
        logger.info("§c作者: Saukiya QQ: 1940208750")
        logger.info("§c重置作者: WCPE QQ: 1837019522")
    }
}