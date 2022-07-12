package github.saukiya.sxlevel;

import github.saukiya.sxlevel.api.SXLevelAPI;
import github.saukiya.sxlevel.bstats.Metrics;
import github.saukiya.sxlevel.command.MainCommand;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.data.ExpDataManager;
import github.saukiya.sxlevel.event.ChangeType;
import github.saukiya.sxlevel.event.SXExpChangeEvent;
import github.saukiya.sxlevel.listener.OnMythicmobsDeathListener;
import github.saukiya.sxlevel.sql.ExpDataDao;
import github.saukiya.sxlevel.sql.MySQLConnection;
import github.saukiya.sxlevel.sql.MySQLExecutorService;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import github.saukiya.sxlevel.util.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.stream.IntStream;

public class SXLevel extends JavaPlugin implements Listener {
    // 版本号
    private static final int[] versionSplit = new int[3];
    // 插件实例
    private static SXLevel plugin;
    // Api
    private static SXLevelAPI api;
    // 主命令
    private MainCommand mainCommand;

    private ExpDataManager expDataManager;

    private ExpDataDao expDataDao;
    private MySQLConnection mysql = null;

    public static int[] getVersionsplit() {
        return versionSplit;
    }

    public static SXLevel getPlugin() {
        return plugin;
    }

    public static SXLevelAPI getApi() {
        return api;
    }

    public void log(String log) {
        getServer().getConsoleSender().sendMessage("§a[§e" + this.getName() + "§a]§r" + log);
    }

    @Override
    public void onLoad() {
        plugin = this;
        api = new SXLevelAPI(this);
        try {
            Config.loadConfig();
            Message.loadMessage();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            log(Message.getMessagePrefix() + "§cError!");
        }
        mainCommand = new MainCommand(this);
    }

    public MySQLConnection getMysql() {
        return mysql;
    }

    public void onEnable() {
        Long oldTimes = System.currentTimeMillis();
        // 获取版本
        String version = Bukkit.getBukkitVersion().split("-")[0].replace(" ", "");
        log(Message.getMessagePrefix() + "服务器版本: " + version);
        // SplitVersion
        String[] strSplit = version.split("[.]");
        IntStream.range(0, strSplit.length).forEachOrdered(i -> versionSplit[i] = Integer.valueOf(strSplit[i]));
        new Metrics(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders(this);
            log(Message.getMessagePrefix() + "§cPlaceholderAPI 不存在!");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            log(Message.getMessagePrefix() + "§aMythicMobs 成功加载!");
            Bukkit.getPluginManager().registerEvents(new OnMythicmobsDeathListener(this), this);
        } else {
            log(Message.getMessagePrefix() + "§cMythicMobs 不存在!");
        }
        MySQLExecutorService.setupExecutorService();
        if (Config.isSql()) {
            MySQLExecutorService.getThread().execute(() -> {
                mysql = new MySQLConnection();
                if (!mysql.isConnection()) {
                    Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§c未成功连接数据库 插件关闭!");
                    this.setEnabled(false);
                    return;
                }
                expDataDao = new ExpDataDao(mysql);
            });
        }
        expDataManager = new ExpDataManager(this);

        Bukkit.getPluginManager().registerEvents(this, this);
        mainCommand.setUp("sxLevel");
        log(Message.getMessagePrefix() + "加载时间: §c" + (System.currentTimeMillis() - oldTimes) + "§7 ms");
        log(Message.getMessagePrefix() + "§c作者: Saukiya QQ: 1940208750");
        log(Message.getMessagePrefix() + "§c重置作者: WCPE QQ: 1837019522");
    }

    // XXX

    // XXX Listener
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        expDataManager.getMap().put(player.getName(), new ExpData(player));
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        getExpDataManager().saveData(event.getPlayer());
    }

    @EventHandler
    void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
        int addExp = (int) (event.getAmount() * Config.getConfig().getDouble(Config.DEFAULT_EXP_VALUE));
        if (Config.getDisabledDefaultExpChange()) {
            event.setAmount(0);
        }
        if (Config.getConfig().getBoolean(Config.DEFAULT_EXP_ENABLED)) {
            ExpData playerData = plugin.getExpDataManager().getPlayerData(event.getPlayer());
            SXExpChangeEvent sxExpChangeEvent = new SXExpChangeEvent(event.getPlayer(), playerData, addExp,
                    ChangeType.ADD);
            Bukkit.getPluginManager().callEvent(sxExpChangeEvent);
            if (!sxExpChangeEvent.isCancelled()) {
                playerData.addExp(sxExpChangeEvent.getAmount());
            }
        }
    }

    public void onDisable() {
        expDataManager.saveAllData();
        if (mysql != null) {
            mysql.closeConnection();
        }
    }

    public MainCommand getMainCommand() {
        return mainCommand;
    }

    public ExpDataManager getExpDataManager() {
        return expDataManager;
    }

    public ExpDataDao getExpDataDao() {
        return expDataDao;
    }

}
