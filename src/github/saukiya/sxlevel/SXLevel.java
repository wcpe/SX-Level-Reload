package github.saukiya.sxlevel;

import github.saukiya.sxlevel.api.SXLevelAPI;
import github.saukiya.sxlevel.bstats.Metrics;
import github.saukiya.sxlevel.command.MainCommand;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.data.ExpDataManager;
import github.saukiya.sxlevel.listener.OnListener;
import github.saukiya.sxlevel.listener.OnMythicmobsDeathListener;
import github.saukiya.sxlevel.sql.MySQLConnection;
import github.saukiya.sxlevel.sql.MySQLExecutorService;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import github.saukiya.sxlevel.util.Placeholders;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.stream.IntStream;


public class SXLevel extends JavaPlugin implements Listener {

    @Getter
    private static final int[] versionSplit = new int[3];

    @Getter
    private static JavaPlugin plugin;

    @Getter
    private static SXLevelAPI api;

    @Getter
    private final String sqlName = SXLevel.class.getSimpleName().toLowerCase();

    @Getter
    private MySQLConnection mysql = null;

    @Getter
    private MainCommand mainCommand;

    @Getter
    private ExpDataManager expDataManager;

    @Override
    public void onLoad() {
        super.onLoad();
        plugin = this;
        api = new SXLevelAPI(this);
        try {
            Config.loadConfig();
            Message.loadMessage();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§cError!");
        }
        mainCommand = new MainCommand(this);
    }

    public void onEnable() {
        Long oldTimes = System.currentTimeMillis();
        // 获取版本
        String version = Bukkit.getBukkitVersion().split("-")[0].replace(" ", "");
        Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "ServerVersion: " + version);
        String[] strSplit = version.split("[.]");
        IntStream.range(0, strSplit.length).forEachOrdered(i -> versionSplit[i] = Integer.valueOf(strSplit[i]));
        new Metrics(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders(this);
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "Find PlaceholderAPI!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "Find MythicMobs!");
            Bukkit.getPluginManager().registerEvents(new OnMythicmobsDeathListener(this), this);
        } else {
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§cNo Find MythicMobs!");
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
                if (!mysql.isExists(getSqlName())) {
                    mysql.createTable(getSqlName(), "name", "date");
                }
            });
        }
        expDataManager = new ExpDataManager(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new OnListener(this), this);
        mainCommand.setUp("sxLevel");
        Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "Loading Time: §c" + (System.currentTimeMillis() - oldTimes) + "§7 ms");
        Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§cAuthor: Saukiya QQ: 1940208750");
    }

    public void onDisable() {
        expDataManager.getMap().values().forEach(ExpData::save);
        if (mysql != null) {
            mysql.closeConnection();
        }
    }
}
