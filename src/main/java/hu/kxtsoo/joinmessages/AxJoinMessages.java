package hu.kxtsoo.joinmessages;

import hu.kxtsoo.joinmessages.manager.CommandManager;
import hu.kxtsoo.joinmessages.utils.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxJoinMessages extends JavaPlugin {

    private static AxJoinMessages instance;
    private ConfigUtil configUtil;

    @Override
    public void onEnable() {
        instance = this;

        configUtil = new ConfigUtil(this);
        configUtil.setupConfig();


        CommandManager commandManager = new CommandManager(this);
        commandManager.registerSuggestions();
        commandManager.registerCommands();
    }

    @Override
    public void onDisable() {}

    public static AxJoinMessages getInstance() {
        return instance;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
}
