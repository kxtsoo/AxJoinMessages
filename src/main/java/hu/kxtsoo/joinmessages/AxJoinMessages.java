package hu.kxtsoo.joinmessages;

import hu.kxtsoo.joinmessages.listeners.PlayerJoinListener;
import hu.kxtsoo.joinmessages.listeners.PlayerQuitListener;
import hu.kxtsoo.joinmessages.manager.CommandManager;
import hu.kxtsoo.joinmessages.utils.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxJoinMessages extends JavaPlugin {

    private static AxJoinMessages instance;
    private ConfigUtil configUtil;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;

        configUtil = new ConfigUtil(this);
        configUtil.setupConfig();

        commandManager = new CommandManager(this);
        commandManager.registerSuggestions();
        commandManager.registerCommands();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(configUtil), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(configUtil), this);
    }

    @Override
    public void onDisable() {
        if(commandManager != null) {
            commandManager.shutdown();
        }
    }

    public static AxJoinMessages getInstance() {
        return instance;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
}
