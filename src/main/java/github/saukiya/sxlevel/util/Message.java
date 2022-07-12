package github.saukiya.sxlevel.util;

import github.saukiya.sxlevel.SXLevel;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public enum Message {
    MESSAGE_VERSION, PLAYER__NO_SQL_CONNECTION, PLAYER__EXP, PLAYER__LEVEL_UP, PLAYER__MAX_LEVEL,

    ADMIN__ADD_EXP, ADMIN__TAKE_EXP, ADMIN__SET_EXP, ADMIN__SET_LEVEL, ADMIN__NO_PERMISSION_CMD, ADMIN__NO_CMD,
    ADMIN__NO_FORMAT, ADMIN__NO_ONLINE, ADMIN__NO_CONSOLE, ADMIN__PLUGIN_RELOAD,

    COMMAND__ADD, COMMAND__TAKE, COMMAND__SET, COMMAND__UPDATELOCALDATATOSQL, COMMAND__RELOAD;

    private static final File FILE = new File(SXLevel.getPlugin().getDataFolder(), "Message.yml");
    private static final String messagePrefix = "[" + SXLevel.getPlugin().getName() + "] ";
    private static YamlConfiguration messages;

    public static String getMessagePrefix() {
        return messagePrefix;
    }

    public static YamlConfiguration getMessages() {
        return messages;
    }

    private static void createDefaultMessage() {
        messages.set(MESSAGE_VERSION.toString(), SXLevel.getPlugin().getDescription().getVersion());

        messages.set(PLAYER__MAX_LEVEL.toString(), "[ACTIONBAR]&a&l你已经满级了!");
        messages.set(PLAYER__NO_SQL_CONNECTION.toString(), "&c服务器暂未准备好，请稍后连接");
        messages.set(PLAYER__EXP.toString(), "[ACTIONBAR]&e&lLv.{0}&8 - &7&l[&a&l{1}&7&l/&6&l{2}&7&l] &7&l[{3}&7&l]");
        messages.set(PLAYER__LEVEL_UP.toString(), "[TITLE]&e&l&n Level   Up! &r\n&e&lLv.{0} &8&l- &e&lMaxExp:{1}");
        messages.set(PLAYER__MAX_LEVEL.toString(), "[ACTIONBAR]&a&l你已经满级了!");
        messages.set(ADMIN__ADD_EXP.toString(), messagePrefix + "&c增长 &6{0}&c 玩家 &6{1} &c经验 &7[&6{2}&7/&6{3}&7]");
        messages.set(ADMIN__TAKE_EXP.toString(), messagePrefix + "&c减少 &6{0}&c 玩家 &6{1} &c经验 &7[&6{2}&7/&6{3}&7]");
        messages.set(ADMIN__SET_EXP.toString(), messagePrefix + "&c设置 &6{0}&c 玩家经验为: &6{1} &7[&6{2}&7/&6{3}&7]");
        messages.set(ADMIN__SET_LEVEL.toString(), messagePrefix + "&c设置 &6{0}&c 玩家等级为: &6{1}&c 级");
        messages.set(ADMIN__NO_PERMISSION_CMD.toString(), messagePrefix + "&c你没有权限执行此指令");
        messages.set(ADMIN__NO_CMD.toString(), messagePrefix + "&c未找到此子指令:{0}");
        messages.set(ADMIN__NO_FORMAT.toString(), messagePrefix + "&c格式错误!");
        messages.set(ADMIN__NO_ONLINE.toString(), messagePrefix + "&c玩家不在线或玩家不存在!");
        messages.set(ADMIN__NO_CONSOLE.toString(), messagePrefix + "&c控制台不允许执行此指令!");
        messages.set(ADMIN__PLUGIN_RELOAD.toString(), messagePrefix + "§c插件已重载");

        messages.set(COMMAND__ADD.toString(), "增长玩家的经验");
        messages.set(COMMAND__TAKE.toString(), "减少玩家的经验");
        messages.set(COMMAND__SET.toString(), "设置玩家的等级/经验");
        messages.set(COMMAND__UPDATELOCALDATATOSQL.toString(), "将本地经验数据上传到SQL");
        messages.set(COMMAND__RELOAD.toString(), "重新加载这个插件的配置");
    }

    /**
     * 检查版本更新
     *
     * @return boolean
     * @throws IOException IOException
     */
    private static boolean detectionVersion() throws IOException {
        if (!messages.getString(Message.MESSAGE_VERSION.toString(), "")
                .equals(SXLevel.getPlugin().getDescription().getVersion())) {
            messages.save(new File(FILE.toString().replace(".yml",
                    "_" + messages.getString(Message.MESSAGE_VERSION.toString()) + ".yml")));
            messages = new YamlConfiguration();
            createDefaultMessage();
            return true;
        }
        return false;
    }

    /**
     * 加载Message类
     *
     * @throws IOException                   IOException
     * @throws InvalidConfigurationException InvalidConfigurationException
     */
    public static void loadMessage() throws IOException, InvalidConfigurationException {
        messages = new YamlConfiguration();
        if (!FILE.exists()) {
            Bukkit.getConsoleSender().sendMessage(messagePrefix + "§cCreate Message.yml");
            createDefaultMessage();
            messages.save(FILE);
        } else {
            messages.load(FILE);
            if (detectionVersion()) {
                Bukkit.getConsoleSender().sendMessage(messagePrefix + "§eUpdate Message.yml");
                messages.save(FILE);
            } else {
                Bukkit.getConsoleSender().sendMessage(messagePrefix + "Find Message.yml");
            }
        }
    }

    /**
     * 获取String
     *
     * @param loc  Message
     * @param args Object...
     * @return String
     */
    public static String getMsg(Message loc, Object... args) {
        return ChatColor.translateAlternateColorCodes('&',
                MessageFormat.format(messages.getString(loc.toString(), "Null Message: " + loc), args));
    }

    /**
     * 获取List
     *
     * @param loc  Message
     * @param args Object...
     * @return List
     */
    public static List<String> getStringList(Message loc, Object... args) {
        List<String> list = messages.getStringList(loc.toString());
        if (list.size() == 0)
            return Collections.singletonList("Null Message: " + loc);
        IntStream.range(0, list.size()).forEach(
                i -> list.set(i, ChatColor.translateAlternateColorCodes('&', MessageFormat.format(list.get(i), args))));
        return list;
    }

    /**
     * 发送带指令点击消息
     *
     * @param player     Player
     * @param message    String
     * @param command    String
     * @param stringList List
     */
    public static void sendCommandToPlayer(Player player, String message, String command, List<String> stringList) {
        TextComponent tcMessage = new TextComponent(message);
        if (stringList != null && stringList.size() > 0) {
            ComponentBuilder bc = new ComponentBuilder(stringList.get(0).replace("&", "§"));
            IntStream.range(1, stringList.size()).mapToObj(i -> "\n" + stringList.get(i).replace("&", "§"))
                    .forEach(bc::append);
            tcMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, bc.create()));
        }
        if (command != null) {
            tcMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        player.spigot().sendMessage(tcMessage);
    }

    /**
     * 发送消息给玩家
     *
     * @param entity  Player
     * @param message String
     */
    public static void send(LivingEntity entity, String message) {
        if (message.contains("Null Message"))
            return;
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (message.contains("[ACTIONBAR]")) {
                message = message.replace("[ACTIONBAR]", "");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            } else if (message.contains("[TITLE]")) {
                message = message.replace("[TITLE]", "");
                if (message.contains(":")) {
                    String title = message.split(":")[0];
                    String subTitle = message.split(":")[1];
                    player.sendTitle(title, subTitle, 5, 20, 5);
                } else {
                    player.sendTitle(message, null, 5, 20, 5);
                }
            } else {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public String toString() {
        return name().replace("__", ".");
    }
}
