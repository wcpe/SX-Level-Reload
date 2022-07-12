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
public class TakeCommand extends SubCommand {

    public TakeCommand() {
        super(SXLevel.getPlugin(), "take", " <player> <value>", SenderType.ALL);
    }

    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_FORMAT));
            return;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage(Message.getMsg(Message.ADMIN__NO_ONLINE));
            return;
        }
        ExpData playerData = plugin.getExpDataManager().getPlayerData(player);
        int takeExp = Integer.valueOf(args[2].replaceAll("[^0-9]", ""));
        playerData.takeExp(takeExp);
        playerData.updateDefaultExp();
        // 为了防止腐竹经常使用该指令，不在这里插入playerData.save();
        sender.sendMessage(Message.getMsg(Message.ADMIN__TAKE_EXP, player.getName(), String.valueOf(takeExp), String.valueOf(playerData.getExp()), String.valueOf(playerData.getMaxExp())));
    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
