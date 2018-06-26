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
    final public static String DEFAULT_EXP_ENABLED = "DefaultExp.Enabled";
    final public static String DEFAULT_EXP_VALUE = "DefaultExp.Value";
    final public static String EXP_LIST = "ExpList";

    final private static File FILE = new File("plugins" + File.separator + SXLevel.getPlugin().getName() + File.separator + "Config.yml");
    @Getter
    private static YamlConfiguration config;

    private static void createConfig() {
        Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] §cCreate Config.yml");
        config = new YamlConfiguration();
        config.set(CONFIG_VERSION, SXLevel.getPlugin().getDescription().getVersion());
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
    }
}
