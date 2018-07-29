package github.saukiya.sxlevel.util;

import github.saukiya.sxlevel.SXLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Config {
    final public static String CONFIG_VERSION = "ConfigVersion";
    final public static String SQL_ENABLED = "SQL.Enabled";
    final public static String SQL_DATABASE_NAME = "SQL.DataBaseName";
    final public static String SQL_HOST = "SQL.Host";
    final public static String SQL_PORT = "SQL.Port";
    final public static String SQL_USER = "SQL.User";
    final public static String SQL_PASSWORD = "SQL.Password";
    final public static String AUTO_SAVE_TICK = "AutoSaveTick";
    final public static String DATA_USE_UUID_SAVE = "DataUseUUIDSave";
    final public static String DISABLED_DEFAULT_EXP_CHANGE = "DisabledDefaultExpChange";
    final public static String DEFAULT_EXP_ENABLED = "DefaultExp.Enabled";
    final public static String DEFAULT_EXP_VALUE = "DefaultExp.Value";
    final public static String EXP_LIST = "ExpList";

    public static Boolean dataUseUuidSave = false;
    public static Boolean disabledDefaultExpChange = false;

    final private static File FILE = new File("plugins" + File.separator + SXLevel.getPlugin().getName() + File.separator + "Config.yml");
    @Getter
    private static YamlConfiguration config;

    private static void createConfig() {
        Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] §cCreate Config.yml");
        config = new YamlConfiguration();
        config.set(CONFIG_VERSION, SXLevel.getPlugin().getDescription().getVersion());
        config.set(SQL_ENABLED, false);
        config.set(SQL_DATABASE_NAME, "null");
        config.set(SQL_HOST, "127.0.0.1");
        config.set(SQL_PORT, 3306);
        config.set(SQL_USER, "root");
        config.set(SQL_PASSWORD, "password");
        config.set(AUTO_SAVE_TICK, 6000);
        config.set(DATA_USE_UUID_SAVE, false);
        config.set(DISABLED_DEFAULT_EXP_CHANGE, false);
        config.set(DEFAULT_EXP_ENABLED, true);
        config.set(DEFAULT_EXP_VALUE, 0.7);
        config.set(EXP_LIST, Arrays.asList("5:500", "10:1000", "20:2000 permission.abc", "25:3000"));

        try {
            config.save(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        //检测Config.yml是否存在
        if (!FILE.exists()) {
            //创建Config.yml
            createConfig();
            return;
        } else {
            Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] Find Config.yml");
        }
        config = new YamlConfiguration();
        //读取config并存储
        try {
            config.load(FILE);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] §c读取config时发生错误");
        }
        dataUseUuidSave = config.getBoolean(Config.DATA_USE_UUID_SAVE);
        disabledDefaultExpChange = config.getBoolean(Config.DISABLED_DEFAULT_EXP_CHANGE);
    }
}
