package github.saukiya.sxlevel

import github.saukiya.sxlevel.api.SXLevelAPI
import github.saukiya.sxlevel.command.MainCommand
import github.saukiya.sxlevel.listener.OnMythicmobsDeathListener
import github.saukiya.sxlevel.util.Message
import github.saukiya.sxlevel.util.Placeholders
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.plugin.java.JavaPlugin
import top.wcpe.sxlevel.AttributePlusListener
import top.wcpe.sxlevel.Configuration
import top.wcpe.sxlevel.SXLevelListener
import top.wcpe.sxlevel.SXLevelPlaceholderExpansion
import top.wcpe.sxlevel.data.FileDataManager
import top.wcpe.sxlevel.data.IDataManager
import top.wcpe.sxlevel.data.MySQLDataManager
import top.wcpe.wcpelib.common.WcpeLibCommon
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
 * @since  : v1.1.0-alpha-dev-1
 */
class SXLevel : JavaPlugin() {

    companion object {
        @JvmStatic
        private val versionSplit = IntArray(3)

        @JvmStatic
        lateinit var instance: SXLevel
            private set

        @JvmStatic
        lateinit var api: SXLevelAPI
            private set

        @JvmStatic
        lateinit var dataManager: IDataManager
            private set


    }

    var configuration: Configuration = Configuration(config)
        private set

    @Deprecated("please use getInstance()", ReplaceWith("instance", "github.saukiya.sxlevel.SXLevel.instance"))
    fun getPlugin(): SXLevel {
        return instance
    }

    fun reloadOtherConfig() {
        reloadConfig()
        configuration = Configuration(config)
        Message.loadMessage()
    }

    override fun onLoad() {
        instance = this
        saveDefaultConfig()
        api = SXLevelAPI()

    }

    override fun onEnable() {
        val oldTimes = System.currentTimeMillis()
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
        if (configuration.mysql) {
            val mybatis = WcpeLibCommon.mybatis
            val redis = WcpeLibCommon.redis
            if (mybatis == null || redis == null) {
                logger.info("WcpeLib 未开启链接数据库 关闭服务器!")
                server.shutdown()
            } else {
                dataManager = MySQLDataManager(mybatis, redis)
            }
        }
        MainCommand(this).setUp("sxLevel")
        server.pluginManager.registerEvents(SXLevelListener(), this)

        if (server.pluginManager.getPlugin("AttributePlus") != null) {
            server.pluginManager.registerEvents(AttributePlusListener(), this)
        }

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