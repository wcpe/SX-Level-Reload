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
        val playerName = args[1];
        val playerLevel = SXLevel.getDataManager().getPlayerLevel(playerName);

        val inputString = args[2];
        val replaceInput = inputString.replaceAll(regex, "");
        int add;
        if (replaceInput.isEmpty()) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        add = Integer.parseInt(replaceInput);
        if (inputString.toLowerCase().contains("l")) {
            playerLevel.addLevel(add);
            sender.sendMessage(Message.getMsg(Message.ADMIN__ADD_LEVEL, playerName, String.valueOf(add), String.valueOf(playerLevel.getExp()), String.valueOf(playerLevel.getMaxExp())));
        } else if (inputString.toLowerCase().contains("%")) {
            val maxExp = playerLevel.getMaxExp();
            val exp = playerLevel.getExp();
            val b = (double) exp / maxExp * 100;
            if (b + add >= 100.0) {
                playerLevel.addLevel(1);
                sender.sendMessage(Message.getMsg(Message.ADMIN__ADD_LEVEL, playerName, String.valueOf(1), String.valueOf(playerLevel.getExp()), String.valueOf(playerLevel.getMaxExp())));
            } else {
                int i = maxExp * add / 100;
                playerLevel.addExp(i);
                sender.sendMessage(Message.getMsg(Message.ADMIN__ADD_EXP, playerName, String.valueOf(i), String.valueOf(playerLevel.getExp()), String.valueOf(playerLevel.getMaxExp())));
            }
        } else {
            playerLevel.addExp(add);
            sender.sendMessage(Message.getMsg(Message.ADMIN__ADD_EXP, playerName, String.valueOf(add), String.valueOf(playerLevel.getExp()), String.valueOf(playerLevel.getMaxExp())));
        }

    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
