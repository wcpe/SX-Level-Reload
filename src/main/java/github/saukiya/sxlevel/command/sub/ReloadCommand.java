package github.saukiya.sxlevel.command.sub;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.command.SubCommand;
import github.saukiya.sxlevel.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Saukiya
 */
public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super(SXLevel.getInstance(), "reload");
    }

    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        long oldTimes = System.currentTimeMillis();
        SXLevel.getInstance().reloadOtherConfig();
        Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "Reloading Time: §c" + (System.currentTimeMillis() - oldTimes) + "§7 ms");
        sender.sendMessage(Message.getMsg(Message.ADMIN__PLUGIN_RELOAD));
    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
