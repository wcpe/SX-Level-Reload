package github.saukiya.sxlevel.command.sub;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.command.SubCommand;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;

/**
 * @author Saukiya
 */
public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super(SXLevel.getPlugin(), "reload");
    }

    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        Long oldTimes = System.currentTimeMillis();
        try {
            Config.loadConfig();
            Message.loadMessage();
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "Reloading Time: §c" + (System.currentTimeMillis() - oldTimes) + "§7 ms");
            sender.sendMessage(Message.getMsg(Message.ADMIN__PLUGIN_RELOAD));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
