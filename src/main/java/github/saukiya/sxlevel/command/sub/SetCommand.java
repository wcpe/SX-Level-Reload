package github.saukiya.sxlevel.command.sub;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.command.SenderType;
import github.saukiya.sxlevel.command.SubCommand;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Saukiya
 */
public class SetCommand extends SubCommand {

    public SetCommand() {
        super(SXLevel.getPlugin(), "set", " <player> <value>", SenderType.ALL);
    }

    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_ONLINE));
            return;
        }
        ExpData playerData = plugin.getExpDataManager().getPlayerData(player);
        if (args[2].toLowerCase().contains("l")) {
            int level = Integer.valueOf(args[2].replaceAll("[^0-9]", ""));
            playerData.setLevel(level);
            playerData.setExp(0);
            // 注入属性
            sender.sendMessage(Message.getMsg(Message.ADMIN__SET_LEVEL, player.getName(), String.valueOf(level)));
        } else {
            int exp = Integer.valueOf(args[2].replaceAll("[^0-9]", ""));
            if (exp > playerData.getMaxExp()) {
                exp = playerData.getMaxExp();
            }
            playerData.setExp(exp);
            sender.sendMessage(Message.getMsg(Message.ADMIN__SET_EXP, player.getName(), String.valueOf(exp), String.valueOf(playerData.getExp()), String.valueOf(playerData.getMaxExp())));
        }
        playerData.updateDefaultExp();
        playerData.save();
    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
