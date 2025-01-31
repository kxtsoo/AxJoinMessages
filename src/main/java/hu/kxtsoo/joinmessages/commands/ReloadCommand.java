package hu.kxtsoo.joinmessages.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import hu.kxtsoo.joinmessages.AxJoinMessages;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

@Command("axblocklimiter")
@Permission("axblocklimiter.admin")
public class ReloadCommand extends BaseCommand {

    @SubCommand("reload")
    @Permission("axblocklimiter.admin.reload")
    public void reload(CommandSender sender) throws SQLException {
        AxJoinMessages.getInstance().getConfigUtil().reloadConfig();

        sender.sendMessage(AxJoinMessages.getInstance().getConfigUtil().getMessage("messages.reload-command-success"));
    }
}