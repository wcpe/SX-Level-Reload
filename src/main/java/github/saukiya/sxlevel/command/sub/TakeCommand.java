package github.saukiya.sxlevel.command.sub;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.command.SenderType;
import github.saukiya.sxlevel.command.SubCommand;
import github.saukiya.sxlevel.util.Message;
import lombok.val;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Saukiya
 */
public class TakeCommand extends SubCommand {

    public TakeCommand() {
        super(SXLevel.getInstance(), "take", " <player> <value>", SenderType.ALL);
    }

    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        val playerName = args[1];
        val playerLevel = SXLevel.getDataManager().getPlayerLevel(playerName);
        int takeExp = Integer.parseInt(args[2].replaceAll(regex, ""));
        playerLevel.takeExp(takeExp);
        sender.sendMessage(Message.getMsg(Message.ADMIN__TAKE_EXP, playerName, String.valueOf(takeExp), String.valueOf(playerLevel.getExp()), String.valueOf(playerLevel.getMaxExp(playerName))));
    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
