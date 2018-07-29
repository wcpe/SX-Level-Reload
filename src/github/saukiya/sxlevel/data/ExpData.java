package github.saukiya.sxlevel.data;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.sql.MySQLExecutorService;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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

    /**
     * 建立玩家数据并自行读取
     *
     * @param player
     */
    ExpData(Player player) {
        this.player = player;
        YamlConfiguration yaml = new YamlConfiguration();
        String saveName = Config.dataUseUuidSave ? this.player.getUniqueId().toString() : this.player.getName();
        if (SXLevel.getMysql() != null) {
            if (SXLevel.getMysql().isExists(SXLevel.getPlugin().getName().toLowerCase(), "name", saveName)) {
                Object object = SXLevel.getMysql().getValue(SXLevel.getPlugin().getName().toLowerCase(), "name", saveName, "date");
                try {
                    yaml.loadFromString(object.toString());
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }else {
            File file = new File("plugins" + File.separator + SXLevel.getPlugin().getName() + File.separator + "PlayerData" + File.separator + saveName + ".yml");
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
    }

    public ExpData(File file){
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

    public Boolean hasExp(int hasExp){
        int level = this.level;
        int exp = this.exp;
        while (hasExp > 0){
            if(level <= 0 && hasExp > exp){
                return false;
            }
            if(hasExp > exp){
                hasExp -= exp;
                level--;
                exp = getMaxExp();
            }else {
                return true;
            }
        }
        return true;
    }

    public Boolean takeExp(int takeExp){
        int change = takeExp;
        while (takeExp > 0){
            if(this.level <= 0 && takeExp > this.exp){
                this.exp = 0;
                Message.send(player,Message.PLAYER_EXP,this.getExp(),this.getMaxExp(),this.level,"§c§l-"+change);
                return false;
            }
            if(takeExp > this.exp){
                takeExp -= exp;
                this.level--;
                this.exp = getMaxExp();
            }else {
                this.exp -= takeExp;
                takeExp = 0;
            }
        }
        Message.send(player,Message.PLAYER_EXP,this.level,this.getExp(),this.getMaxExp(),"§c§l-"+change);
        return true;
    }

    public void addExp(int addExp) {
        if (this.getMaxLevel() <= this.level) {
            Message.send(player,Message.PLAYER_MAX_LEVEL);
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
                addExp = 0;
            }
        }
        Message.send(player,Message.PLAYER_EXP,this.level,this.getExp(),this.getMaxExp(),"§e§l+"+change);
        if(!levelUp){
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,change/50f,change/20f);
        }else {
            MySQLExecutorService.getThread().execute(()->this.save());
            Message.send(player,Message.PLAYER_LEVEL_UP,this.level,this.getMaxExp());
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,change/20f,change/20f);
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
     * 获取当天零点
     *
     * @return long 时间
     */
    public long getStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
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
        String saveName = Config.dataUseUuidSave ? this.player.getUniqueId().toString() : this.player.getName();
        if (SXLevel.getMysql() != null) {
            if (SXLevel.getMysql().isExists(SXLevel.getPlugin().getName().toLowerCase(), "name", saveName)) {
                SXLevel.getMysql().intoValue(SXLevel.getPlugin().getName().toLowerCase(), saveName, yaml.saveToString());
            }else {
                SXLevel.getMysql().setValue(SXLevel.getPlugin().getName().toLowerCase(), "name", saveName, "date", yaml.saveToString());
            }
        } else {
            File file = new File("plugins" + File.separator + SXLevel.getPlugin().getName() + File.separator + "PlayerData" + File.separator + saveName + ".yml");
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
