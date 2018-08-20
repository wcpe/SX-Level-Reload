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
    public static final String CONFIG_VERSION = "ConfigVersion";
    public static final String SQL_ENABLED = "SQL.Enabled";
    public static final String SQL_DATABASE_NAME = "SQL.DataBaseName";
    public static final String SQL_HOST = "SQL.Host";
    public static final String SQL_PORT = "SQL.Port";
    public static final String SQL_USER = "SQL.User";
    public static final String SQL_PASSWORD = "SQL.Password";
    public static final String AUTO_SAVE_TICK = "AutoSaveTick";
    public static final String DATA_USE_UUID_SAVE = "DataUseUUIDSave";
    public static final String SX_LEVEL_SET_DEFAULT_EXP = "SXLevelSetDefaultExp";
    public static final String DISABLED_DEFAULT_EXP_CHANGE = "DisabledDefaultExpChange";
    public static final String DEFAULT_EXP_ENABLED = "DefaultExp.Enabled";
    public static final String DEFAULT_EXP_VALUE = "DefaultExp.Value";
    public static final String EXP_LIST = "ExpList";
    private static final File FILE = new File(SXLevel.getPlugin().getDataFolder(), "Config.yml");
    @Getter
    private static boolean sql = false;
    @Getter
    private static Boolean dataUseUuidSave = false;
    @Getter
    private static Boolean sxLevelSetDefaultExp = false;
    @Getter
    private static Boolean disabledDefaultExpChange = false;
    @Getter
    private static YamlConfiguration config;

    private static void createDefaultConfig() {
        config.set(CONFIG_VERSION, SXLevel.getPlugin().getDescription().getVersion());
        config.set(SQL_ENABLED, false);
        config.set(SQL_DATABASE_NAME, "null");
        config.set(SQL_HOST, "127.0.0.1");
        config.set(SQL_PORT, 3306);
        config.set(SQL_USER, "root");
        config.set(SQL_PASSWORD, "password");
        config.set(AUTO_SAVE_TICK, 6000);
        config.set(DATA_USE_UUID_SAVE, false);
        config.set(SX_LEVEL_SET_DEFAULT_EXP, false);
        config.set(DISABLED_DEFAULT_EXP_CHANGE, false);
        config.set(DEFAULT_EXP_ENABLED, true);
        config.set(DEFAULT_EXP_VALUE, 0.7);
        config.set(EXP_LIST, Arrays.asList("5:500", "10:1000", "20:2000 permission.abc", "25:3000"));
    }

    /**
     * 检查版本更新
     *
     * @return boolean
     * @throws IOException IOException
     */
    private static boolean detectionVersion() throws IOException {
        if (!config.getString(CONFIG_VERSION, "").equals(SXLevel.getPlugin().getDescription().getVersion())) {
            config.save(new File(FILE.toString().replace(".yml", "_" + config.getString(CONFIG_VERSION) + ".yml")));
            config = new YamlConfiguration();
            createDefaultConfig();
            return true;
        }
        return false;
    }

    public static void loadConfig() throws IOException, InvalidConfigurationException {
        config = new YamlConfiguration();
        if (!FILE.exists()) {
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§cCreate Config.yml");
            createDefaultConfig();
            config.save(FILE);
        } else {
            config.load(FILE);
            if (detectionVersion()) {
                config.save(FILE);
                Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§eUpdate Config.yml");
            } else {
                Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "Find Config.yml");
            }
        }
        sql = config.getBoolean(SQL_ENABLED);
        dataUseUuidSave = config.getBoolean(DATA_USE_UUID_SAVE);
        sxLevelSetDefaultExp = config.getBoolean(SX_LEVEL_SET_DEFAULT_EXP);
        disabledDefaultExpChange = config.getBoolean(DISABLED_DEFAULT_EXP_CHANGE);
    }
}
