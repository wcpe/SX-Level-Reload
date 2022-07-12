package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.event.SXLevelUpEvent;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import top.wcpe.wcpelib.bukkit.utils.StringActionUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Saukiya
 * @since 2018年5月7日
 */

public class ExpData {

    private int level = 0;
    private int exp = 0;
    private Player player;

    public ExpData(Player player) {
        this.player = player;
        if (SXLevel.getPlugin().getMysql() != null) {
            ExpData expData = SXLevel.getPlugin().getExpDataDao().get(player.getName());
            if (expData != null) {
                this.exp = expData.getExp();
                this.level = expData.getLevel();
            }
            updateDefaultExp();
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(SXLevel.getPlugin().getDataFolder(),
                "PlayerData" + File.separator + player.getName() + ".yml"));
        this.level = yaml.getInt("Level");
        this.exp = yaml.getInt("Exp");
        updateDefaultExp();
    }

    public ExpData(Player player, int exp, int level) {
        this.player = player;
        this.exp = exp;
        this.level = level;
        updateDefaultExp();
    }

    @Override
    public String toString() {
        return "ExpData [level=" + level + ", exp=" + exp + ", player=" + player + "]";
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
        Message.send(player,
                Message.getMsg(Message.PLAYER__EXP, this.level, this.getExp(), this.getMaxExp(), "§c§l-" + change));
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
                StringActionUtil.executionCommands(Config.getConfig().getStringList(Config.LEVEL_UP_STRING_ACTION + "." + getLevel()), false, player);
                levelUp = true;
            } else {
                this.setExp(this.getExp() + addExp);
                break;
            }
        }

        updateDefaultExp();
        Message.send(player,
                Message.getMsg(Message.PLAYER__EXP, this.level, this.getExp(), this.getMaxExp(), "§e§l+" + change));
        if (!levelUp) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, change / 50f,
                    change / 20f);
        } else {
            Bukkit.getPluginManager().callEvent(new SXLevelUpEvent(player, this));
            save();
            Message.send(player, Message.getMsg(Message.PLAYER__LEVEL_UP, this.level, this.getMaxExp()));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, change / 20f, change / 20f);
        }
    }

    public void updateDefaultExp() {
        if (Config.getSxLevelSetDefaultExp()) {
            player.setLevel(this.getLevel());
            if (this.getMaxExp() != 0) {
                int maxExp = this.getMaxExp();
                if (maxExp < this.getExp()) {
                    player.setExp(1.0F);
                } else {
                    player.setExp(this.getExp() / (float) this.getMaxExp());
                }
            } else {
                player.setExp(0.0F);
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

    public void save() {
        if (SXLevel.getPlugin().getMysql() != null) {
            SXLevel.getPlugin().getExpDataDao().add(this);
            updateDefaultExp();
            return;
        }
        File f = new File(SXLevel.getPlugin().getDataFolder(),
                "PlayerData" + File.separator + player.getName() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
        yaml.set("Exp", this.exp);
        yaml.set("Level", this.level);
        try {
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateDefaultExp();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
