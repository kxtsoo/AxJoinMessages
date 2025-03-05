package hu.kxtsoo.joinmessages.manager;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import hu.kxtsoo.joinmessages.AxJoinMessages;
import hu.kxtsoo.joinmessages.commands.ReloadCommand;
import org.bukkit.command.CommandSender;

public class CommandManager {
    private final BukkitCommandManager<CommandSender> commandManager;
    private AxJoinMessages plugin;

    public CommandManager(AxJoinMessages plugin) {
        this.commandManager = BukkitCommandManager.create(plugin);
        this.plugin = plugin;
    }

    public void registerSuggestions() {}

    public void registerCommands() {
        commandManager.registerCommand(new ReloadCommand());
    }

    public void shutdown() {
        commandManager.unregisterCommands();
    }
}
