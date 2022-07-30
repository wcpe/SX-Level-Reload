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
public class AddCommand extends SubCommand {

    public AddCommand() {
        super(SXLevel.getInstance(), "add", " <player> <value>", SenderType.ALL);
    }

    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        final val playerLevel = SXLevel.getDataManager().getPlayerLevel(args[1]);
        int addExp = Integer.parseInt(args[2].replaceAll(regex, ""));
        playerLevel.addExp(addExp);
        sender.sendMessage(Message.getMsg(Message.ADMIN__ADD_EXP, args[1], String.valueOf(addExp), String.valueOf(playerLevel.getExp()), String.valueOf(playerLevel.getMaxExp())));
    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
