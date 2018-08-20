package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.event.SXLevelUpEvent;
import github.saukiya.sxlevel.sql.MySQLExecutorService;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Saukiya
 * @since 2018年5月7日
 */

public class ExpData {

    @Getter
    private Player player;
    @Getter
    @Setter
    private int level;
    @Getter
    @Setter
    private int exp;

    private SXLevel plugin;

    /**
     * 建立玩家数据并自行读取
     *
     * @param player
     */
    public ExpData(SXLevel plugin, Player player) {
        this.player = player;
        this.plugin = plugin;
        YamlConfiguration yaml = new YamlConfiguration();
        String saveName = Config.getDataUseUuidSave() ? this.player.getUniqueId().toString() : this.player.getName();
        if (plugin.getMysql() != null) {
            if (plugin.getMysql().isExists(plugin.getSqlName(), "name", saveName)) {
                Object object = plugin.getMysql().getValue(plugin.getSqlName(), "name", saveName, "date");
                try {
                    yaml.loadFromString(object.toString());
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        } else {
            File file = new File(SXLevel.getPlugin().getDataFolder(), "PlayerData" + File.separator + saveName + ".yml");
            if (file.exists()) {
                try {
                    yaml.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }
        this.level = yaml.getInt("Level");
        this.exp = yaml.getInt("Exp");
        if (Config.getSxLevelSetDefaultExp()) {
            player.setLevel(this.getLevel());
            if (this.getMaxExp() != 0) {
                player.setExp(this.getExp() / (float) this.getMaxExp());
            } else {
                player.setExp(0);
            }
        }
    }

    public ExpData(File file) {
        YamlConfiguration yaml = new YamlConfiguration();
        if (file.exists()) {
            try {
                yaml.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        this.level = yaml.getInt("Level");
        this.exp = yaml.getInt("Exp");
    }

    public Boolean hasExp(int hasExp) {
        int level = this.level;
        int exp = this.exp;
        while (hasExp > 0) {
            if (level <= 0 && hasExp > exp) {
                return false;
            }
            if (hasExp > exp) {
                hasExp -= exp;
                level--;
                exp = getMaxExp();
            } else {
                return true;
            }
        }
        return true;
    }

    public void takeExp(int takeExp) {
        int change = takeExp;
        while (takeExp > 0) {
            if (this.level <= 0 && takeExp > this.exp) {
                this.exp = 0;
                break;
            }
            if (takeExp > this.exp) {
                takeExp -= exp;
                this.level--;
                this.exp = getMaxExp();
            } else {
                this.exp -= takeExp;
                break;
            }
        }
        updateDefaultExp();
        Message.send(player, Message.getMsg(Message.PLAYER__EXP, this.level, this.getExp(), this.getMaxExp(), "§c§l-" + change));
    }

    public void addExp(int addExp) {
        if (this.getMaxLevel() <= this.level) {
            Message.send(player, Message.getMsg(Message.PLAYER__MAX_LEVEL));
            return;
        }
        int change = addExp;
        boolean levelUp = false;
        while (addExp > 0) {
            if (this.getMaxExp() == 0) {
                break;
            }
            // 升级
            if (this.getExp() + addExp >= this.getMaxExp()) {
                addExp = (this.getExp() + addExp) - this.getMaxExp();
                this.setExp(0);
                this.setLevel(this.getLevel() + 1);
                levelUp = true;
            } else {
                this.setExp(this.getExp() + addExp);
                break;
            }
        }
        updateDefaultExp();
        Message.send(player, Message.getMsg(Message.PLAYER__EXP, this.level, this.getExp(), this.getMaxExp(), "§e§l+" + change));
        if (!levelUp) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, change / 50f, change / 20f);
        } else {
            Bukkit.getPluginManager().callEvent(new SXLevelUpEvent(player, this));
            MySQLExecutorService.getThread().execute(this::save);
            Message.send(player, Message.getMsg(Message.PLAYER__LEVEL_UP, this.level, this.getMaxExp()));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, change / 20f, change / 20f);
        }
    }

    public void updateDefaultExp() {
        if (Config.getSxLevelSetDefaultExp()) {
            player.setLevel(this.getLevel());
            if (this.getMaxExp() != 0) {
                player.setExp(this.getExp() / (float) this.getMaxExp());
            } else {
                player.setExp(0);
            }
        }
    }

    public int getMaxExp() {
        List<String> expList = Config.getConfig().getStringList(Config.EXP_LIST);
        if (this.getMaxLevel() <= this.level) {
            return 0;
        }
        int maxExp = 0, level = -1;
        for (String str : expList) {
            if (str.contains(":") && str.split(":").length > 1) {
                level = Integer.valueOf(str.split(":")[0].replaceAll("[^0-9]", ""));
                maxExp = Integer.valueOf(str.split(":")[1].replaceAll("[^0-9]", ""));
            } else {
                level++;
                maxExp = Integer.valueOf(str.replaceAll("[^0-9]", ""));
            }
            if (str.contains(" ") && str.split(" ").length > 1) {
                if (!player.hasPermission(str.split(" ")[1])) {
                    break;
                }
            }
            if (this.level <= level) {
                break;
            }
        }
        return maxExp;
    }

    public int getMaxLevel() {
        List<String> expList = Config.getConfig().getStringList(Config.EXP_LIST);
        int maxLevel = -1;
        for (String str : expList) {
            if (str.contains(":") && str.split(":").length > 1) {
                maxLevel = Integer.valueOf(str.split(":")[0].replaceAll("[^0-9]", ""));
            } else {
                maxLevel++;
            }
            if (str.contains(" ") && str.split(" ").length > 1) {
                if (!player.hasPermission(str.split(" ")[1])) {
                    break;
                }
            }
        }
        return maxLevel;
    }

    /**
     * 保存数据
     *
     * @return playerData 返回自己
     */
    public ExpData save() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("Name", this.player.getName());
        yaml.set("Exp", this.exp);
        yaml.set("Level", this.level);
        String saveName = Config.getDataUseUuidSave() ? this.player.getUniqueId().toString() : this.player.getName();
        if (plugin.getMysql() != null) {
            if (!plugin.getMysql().isExists(plugin.getSqlName(), "name", saveName)) {
                plugin.getMysql().intoValue(plugin.getSqlName(), saveName, yaml.saveToString());
            } else {
                plugin.getMysql().setValue(plugin.getSqlName(), "name", saveName, "date", yaml.saveToString());
            }
        } else {
            File file = new File(SXLevel.getPlugin().getDataFolder(), "PlayerData" + File.separator + saveName + ".yml");
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
