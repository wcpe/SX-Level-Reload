package github.saukiya.sxlevel.util;

import github.saukiya.sxlevel.SXLevel;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public class Message {
    final public static String MESSAGE_VERSION = "MessageVersion";
    final public static String PLAYER_EXP = "Player.Exp";
    final public static String PLAYER_LEVEL_UP = "Player.LevelUp";
    final public static String PLAYER_MAX_LEVEL = "Player.MaxLevel";

    final public static String ADMIN_ADD_EXP = "Admin.AddExp";
    final public static String ADMIN_TAKE_EXP = "Admin.TakeExp";
    final public static String ADMIN_SET_EXP = "Admin.SetExp";
    final public static String ADMIN_SET_LEVEL = "Admin.SetLevel";
    final public static String ADMIN_NO_PER_CMD = "Admin.NoPermissionCommand";
    final public static String ADMIN_NO_CMD = "Admin.NoCommand";
    final public static String ADMIN_NO_FORMAT = "Admin.NoFormat";
    final public static String ADMIN_NO_ONLINE = "Admin.NoOnline";
    final public static String ADMIN_NO_CONSOLE = "Admin.NoConsole";
    final public static String ADMIN_PLUGIN_RELOAD = "Admin.PluginReload";

    final public static String COMMAND_ADD = "Command.add";
    final public static String COMMAND_TAKE = "Command.take";
    final public static String COMMAND_SET = "Command.set";
    final public static String COMMAND_UPDATE = "Command.updateLocalDataToSql";
    final public static String COMMAND_RELOAD = "Command.reload";
    final private static File FILE = new File("plugins" + File.separator + SXLevel.getPlugin().getName() + File.separator + "Message.yml");
    @Getter
    private static YamlConfiguration messages;

    public static void createMessage() {
        Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] §cCreate Message.yml");
        messages = new YamlConfiguration();
        messages.set(MESSAGE_VERSION, SXLevel.getPlugin().getDescription().getVersion());
        messages.set(PLAYER_EXP, "[ACTIONBAR]&e&lLv.{0}&8 - &7&l[&a&l{1}&7&l/&6&l{2}&7&l] &7&l[{3}&7&l]");
        messages.set(PLAYER_LEVEL_UP, "[TITLE]&e&l&n Level   Up! &r\n&e&lLv.{0} &8&l- &e&lMaxExp:{1}");
        messages.set(PLAYER_MAX_LEVEL, "[ACTIONBAR]&a&l你已经满级了!");
        messages.set(ADMIN_ADD_EXP, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c增长 &6{0}&c 玩家 &6{1} &c经验 &7[&6{2}&7/&6{3}&7]");
        messages.set(ADMIN_TAKE_EXP, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c减少 &6{0}&c 玩家 &6{1} &c经验 &7[&6{2}&7/&6{3}&7]");
        messages.set(ADMIN_SET_EXP, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c设置 &6{0}&c 玩家经验为: &6{1} &7[&6{2}&7/&6{3}&7]");
        messages.set(ADMIN_SET_LEVEL, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c设置 &6{0}&c 玩家等级为: &6{1}&c 级");
        messages.set(ADMIN_NO_PER_CMD, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c你没有权限执行此指令");
        messages.set(ADMIN_NO_CMD, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c未找到此子指令:{0}");
        messages.set(ADMIN_NO_FORMAT, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c格式错误!");
        messages.set(ADMIN_NO_ONLINE, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c玩家不在线或玩家不存在!");
        messages.set(ADMIN_NO_CONSOLE, "&8[&d" + SXLevel.getPlugin().getName() + "&8] &c控制台不允许执行此指令!");
        messages.set(ADMIN_PLUGIN_RELOAD, "&8[&d" + SXLevel.getPlugin().getName() + "&8] §c插件已重载");

        messages.set(COMMAND_ADD, "增长玩家的经验");
        messages.set(COMMAND_TAKE, "减少玩家的经验");
        messages.set(COMMAND_SET, "设置玩家的等级/经验");
        messages.set(COMMAND_UPDATE, "将本地经验数据上传到SQL");
        messages.set(COMMAND_RELOAD, "重新加载这个插件的配置");
        try {
            messages.save(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMessage() {
        if (!FILE.exists()) {
            createMessage();
        } else {
            Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] Find Message.yml");
        }
        messages = new YamlConfiguration();
        try {
            messages.load(FILE);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§8[§6" + SXLevel.getPlugin().getName() + "§8] §c读取message时发生错误");
        }
    }


    public static String getMsg(String loc, Object... args) {
        String message = ChatColor.translateAlternateColorCodes('&', messages.getString(loc, "Null Message: " + loc));
        return MessageFormat.format(message, args);
    }

    public static List<String> getList(String loc, Object... args) {
        List<String> list = messages.getStringList(loc);
        if (list == null || list.isEmpty()) {
            list.add("Null Message: " + loc);
            return list;
        }
        //循环lore
        for (int e = 0; e < list.size(); e++) {
            String lore = list.get(e).replace("&", "§");
            for (int i = 0; i < args.length; i++) {
                lore = lore.replace("{" + i + "}", args[i] == null ? "null" : args[i].toString());
            }
            list.set(e, lore);
        }
        return list;
    }

    public static void sendCommandMessage(Player player, List<String> loreList, String command, String loc, boolean locBoolean, Object... args) {
        TextComponent message = new TextComponent(locBoolean ? getMsg(loc, args) : loc);
        if (loreList.size() > 0) {
            ComponentBuilder bc = new ComponentBuilder(loreList.get(0).replace("&", "§"));
            for (int i = 1; i < loreList.size(); i++) {
                bc.append("\n" + loreList.get(i).replace("&", "§"));
            }
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, bc.create()));
        }
        if (command != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        }
        player.spigot().sendMessage(message);
    }


    @SuppressWarnings("deprecation")
    public static void send(LivingEntity entity, String loc, Object... args) {
        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;
        String message = getMsg(loc, args);
        if (message.equals("Null Message: " + loc)) return;
        if (message.contains("[ACTIONBAR]")) {
            message = message.replace("[ACTIONBAR]", "");
            if (SXLevel.getServerSplit()[1] > 9 || (SXLevel.getServerSplit()[1] == 9 && SXLevel.getServerSplit()[2] >= 4)) {
                //1.9.4方法
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            } else {
                player.sendMessage(message);
            }
        } else if (message.contains("[TITLE]")) {
            message = message.replace("[TITLE]", "");
            String title = message;
            String subTitle = null;
            if (message.contains("\n")) {
                title = message.split("\n")[0];
                subTitle = message.split("\n")[1];
            }
            //1.11.2方法
            if (SXLevel.getServerSplit()[1] >= 11) {
                player.sendTitle(title, subTitle, 5, 20, 3);
            } else if (SXLevel.getServerSplit()[1] >= 9) {
                player.sendTitle(title, subTitle);
            } else {
                player.sendMessage(message);
            }
        } else {
            player.sendMessage(message);
        }
    }
}
