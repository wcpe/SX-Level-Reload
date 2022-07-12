package github.saukiya.sxlevel.command.sub;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.command.SubCommand;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.sql.MySQLExecutorService;
import github.saukiya.sxlevel.util.Config;
import github.saukiya.sxlevel.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author Saukiya
 */
public class UpdateLocalDataToSqlCommand extends SubCommand {

    public UpdateLocalDataToSqlCommand() {
        super(SXLevel.getPlugin(), "updateLocalDataToSql");
    }

    @Override
    public void onCommand(SXLevel plugin, CommandSender sender, String[] args) {
        if (Config.isSql() && plugin.getMysql() != null) {
            File files = new File(SXLevel.getPlugin().getDataFolder(), "PlayerData");
            if (files.isDirectory()) {
                for (File file : Objects.requireNonNull(files.listFiles())) {
                    ExpData expData = new ExpData(file);
                    MySQLExecutorService.getThread().execute(expData::save);
                }
            }
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§c上传了 §4" + Objects.requireNonNull(files.listFiles()).length + " §c份本地数据到SQL");
        } else {
            Bukkit.getConsoleSender().sendMessage(Message.getMessagePrefix() + "§c你没有开启或连接SQL");
        }
    }

    @Override
    public List<String> onTabComplete(SXLevel plugin, CommandSender sender, String[] args) {
        return null;
    }
}
