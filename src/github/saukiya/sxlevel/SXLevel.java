package github.saukiya.sxlevel;

import github.saukiya.sxlevel.data.PlayerExpDataManager;
import github.saukiya.sxlevel.data.PlayerExpData;
import github.saukiya.sxlevel.listener.OnListener;
import github.saukiya.sxlevel.listener.OnMythicmobsDeathListener;
import github.saukiya.sxlevel.util.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class SXLevel extends JavaPlugin implements Listener {

    @Getter
    private static int[] serverSplit = null;
    @Getter
    private static JavaPlugin plugin;
    private static List<String> commandList = new ArrayList<>();


    public void onEnable() {
        plugin = this;
        Long oldTimes = System.currentTimeMillis();
        // 获取版本
        String server = Bukkit.getServer().getClass().getPackage().getName().replace(".", "-").split("-")[3];
        String[] serverStringSplit = server.replaceAll("[^0-9_]", "").split("_");
        serverSplit = new int[serverStringSplit.length];
        for (int i = 0; i < serverStringSplit.length; i++) {
            serverSplit[i] = Integer.valueOf(serverStringSplit[i]);
        }
        // 获取指令
        for (String command : this.getDescription().getCommands().keySet()) {
            commandList.add(command);
            for (String key : this.getDescription().getCommands().get(command).keySet()) {
                if (key.equals("aliases")) {
                    commandList.addAll((List<String>) this.getDescription().getCommands().get(command).get(key));
                }
            }
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new OnListener(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders(this).hook();
            Bukkit.getConsoleSender().sendMessage("[" + this.getName() + "] Find PlacholderAPI!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            Bukkit.getConsoleSender().sendMessage("[" + this.getName() + "] Find MythicMobs!");
            Bukkit.getPluginManager().registerEvents(new OnMythicmobsDeathListener(), this);
        } else {
            Bukkit.getConsoleSender().sendMessage("[" + this.getName() + "] §cNo Find MythicMobs!");
        }
        Config.loadConfig();
        Message.loadMessage();
        PlayerExpDataManager.autoSave();
        Bukkit.getConsoleSender().sendMessage("[" + this.getName() + "] 加载用时: §c" + (System.currentTimeMillis() - oldTimes) + "§7 毫秒");
        Bukkit.getConsoleSender().sendMessage("[" + this.getName() + "] §c加载成功! 插件作者: Saukiya 联系: 1940208750");
    }

    public void onDisable() {
        PlayerExpDataManager.getPlayerNameMap().values().forEach(PlayerExpData::save);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
        if (commandList.contains(label)) {
            CommandType type = CommandType.CONSOLE;
            //判断是否是玩家
            if (sender instanceof Player) {
                //判断是否有权限
                if (!sender.hasPermission(this.getName() + ".use")) {
                    sender.sendMessage(Message.getMsg(Message.ADMIN_NO_PER_CMD));
                    return true;
                }
                type = CommandType.PLAYER;
            }
            //无参数
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&0-&8 --&7 ---&c ----&4 -----&b " + this.getName() + "&4 -----&c ----&7 ---&8 --&0 - &0Author Saukiya"));
                String color = "&7";
                for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
                    if (!method.isAnnotationPresent(PlayerCommand.class)) {
                        continue;
                    }
                    PlayerCommand sub = method.getAnnotation(PlayerCommand.class);
                    if (contains(sub.type(), type) && sender.hasPermission(this.getName() + "." + sub.cmd())) {
                        color = color.equals("&7") ? "" : "&7";
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageFormat.format(color + "/{0} {1}{2}&7 -&c {3}", label, sub.cmd(), sub.arg(), Message.getMsg("Command." + sub.cmd()))));
                    }
                }
                return true;
            }
            for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(PlayerCommand.class)) {
                    continue;
                }
                PlayerCommand sub = method.getAnnotation(PlayerCommand.class);
                if (!sub.cmd().equalsIgnoreCase(args[0])) {
                    continue;
                }
                if (!contains(sub.type(), type) || !sender.hasPermission(this.getName() + "." + args[0])) {
                    sender.sendMessage(Message.getMsg(Message.ADMIN_NO_PER_CMD));
                    return true;
                }
                try {
                    method.invoke(this, sender, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return true;
            }
            sender.sendMessage(Message.getMsg(Message.ADMIN_NO_CMD, args[0]));
            return true;
        }
        return false;
    }

    private boolean contains(CommandType[] type1, CommandType type2) {
        return IntStream.range(0, type1.length).anyMatch(i -> type1[i].equals(CommandType.ALL) || type1[i].equals(type2));
    }

    @PlayerCommand(cmd = "add", arg = " <player> <value>")
    public void onAddCommand(CommandSender sender, String args[]) {
        if (args.length < 3) {
            sender.sendMessage(Message.getMsg(Message.ADMIN_NO_FORMAT));
            return;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage(Message.getMsg(Message.ADMIN_NO_ONLINE));
            return;
        }
        PlayerExpData playerData = PlayerExpDataManager.getPlayerData(player);
        int addExp = Integer.valueOf(args[2].replaceAll("[^0-9]", ""));
        playerData.addExp(addExp);
        playerData.save();
        sender.sendMessage(Message.getMsg(Message.ADMIN_ADD_EXP, player.getName(), String.valueOf(addExp), String.valueOf(playerData.getExp()), String.valueOf(playerData.getMaxExp())));
    }

    @PlayerCommand(cmd = "take", arg = " <player> <value>")
    public void onTakeCommand(CommandSender sender, String args[]) {
        if (args.length < 3) {
            sender.sendMessage(Message.getMsg(Message.ADMIN_NO_FORMAT));
            return;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage(Message.getMsg(Message.ADMIN_NO_ONLINE));
            return;
        }
        PlayerExpData playerData = PlayerExpDataManager.getPlayerData(player);
        int takeExp = Integer.valueOf(args[2].replaceAll("[^0-9]", ""));
        playerData.takeExp(takeExp);
        playerData.save();
        sender.sendMessage(Message.getMsg(Message.ADMIN_TAKE_EXP, player.getName(), String.valueOf(takeExp), String.valueOf(playerData.getExp()), String.valueOf(playerData.getMaxExp())));
    }

    @PlayerCommand(cmd = "set", arg = " <player> <value>")
    public void onSetCommand(CommandSender sender, String args[]) {
        if (args.length < 2) {
            sender.sendMessage(Message.getMsg(Message.ADMIN_NO_FORMAT));
            return;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage(Message.getMsg(Message.ADMIN_NO_ONLINE));
            return;
        }
        PlayerExpData playerData = PlayerExpDataManager.getPlayerData(player);
        if (args[2].toLowerCase().contains("l")) {
            int level = Integer.valueOf(args[2].replaceAll("[^0-9]", ""));
            playerData.setLevel(level);
            playerData.setExp(0);
            // 注入属性
            sender.sendMessage(Message.getMsg(Message.ADMIN_SET_LEVEL, player.getName(), String.valueOf(level)));
        } else {
            int exp = Integer.valueOf(args[2].replaceAll("[^0-9]", ""));
            if (exp > playerData.getMaxExp()) {
                exp = playerData.getMaxExp();
            }
            playerData.setExp(exp);
            sender.sendMessage(Message.getMsg(Message.ADMIN_SET_EXP, player.getName(), String.valueOf(exp), String.valueOf(playerData.getExp()), String.valueOf(playerData.getMaxExp())));
        }
        playerData.save();
    }

    @PlayerCommand(cmd = "reload")
    void onReloadCommand(CommandSender sender, String args[]) {
        Config.loadConfig();
        Message.loadMessage();
        sender.sendMessage(Message.getMsg(Message.ADMIN_PLUGIN_RELOAD));
    }


}
