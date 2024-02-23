package github.saukiya.sxlevel.command.sub;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.command.SenderType;
import github.saukiya.sxlevel.command.SubCommand;
import github.saukiya.sxlevel.util.Message;
import lombok.val;
import lombok.var;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Saukiya
 */
public class SetCommand extends SubCommand {

    public SetCommand() {
        super(SXLevel.getInstance(), "set", " <player> <value>", SenderType.ALL);
    }


    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        val playerName = args[1];
        val playerLevel = SXLevel.getDataManager().getPlayerLevel(playerName);
        if (args[2].toLowerCase().contains("l")) {
            int level = Integer.parseInt(args[2].replaceAll(regex, ""));
            playerLevel.setLevel(level);
            playerLevel.setExp(0);
            sender.sendMessage(Message.getMsg(Message.ADMIN__SET_LEVEL, playerName, String.valueOf(level)));
        } else {
            int exp = Integer.parseInt(args[2].replaceAll(regex, ""));

            var maxExp = playerLevel.getMaxExpValue(playerName);

            if (exp > maxExp) {
                exp = maxExp;
            }
            playerLevel.setExp(exp);
            sender.sendMessage(Message.getMsg(Message.ADMIN__SET_EXP, playerName, String.valueOf(exp), String.valueOf(playerLevel.getExp()), String.valueOf(playerLevel.getMaxExpValue(playerName))));
        }
        playerLevel.updateDefaultExp();
        playerLevel.save();
    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
