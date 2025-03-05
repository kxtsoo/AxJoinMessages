package hu.kxtsoo.joinmessages.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import hu.kxtsoo.joinmessages.AxJoinMessages;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigUtil {

    private final AxJoinMessages plugin;
    public static ConfigUtil configUtil;
    private YamlDocument config;
    private YamlDocument messages;

    public ConfigUtil(AxJoinMessages plugin) {
        this.plugin = plugin;
        setupConfig();
        setupMessages();
    }

    public void setupConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                plugin.saveResource("config.yml", false);
            }

            config = YamlDocument.create(configFile,
                    Objects.requireNonNull(plugin.getResource("config.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setKeepAll(true)
                            .setVersioning(new BasicVersioning("version")).build());

            config.update();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error loading or creating config.yml: " + ex.getMessage());
        }
    }

    public void setupMessages() {
        try {
            File messageFile = new File(plugin.getDataFolder(), "messages.yml");
            if (!messageFile.exists()) {
                plugin.saveResource("messages.yml", false);
            }

            messages = YamlDocument.create(messageFile,
                    Objects.requireNonNull(plugin.getResource("messages.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setKeepAll(true)
                            .setVersioning(new BasicVersioning("version")).build());

            messages.update();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error loading or creating messages.yml: " + ex.getMessage());
        }
    }

    public String getMessage(String key) {
        Object messageObj = messages.get(key, "Message not found");

        if (messageObj instanceof String) {
            String message = ChatUtil.colorizeHex((String) messageObj);
            String prefix = ChatUtil.colorizeHex(config.getString("prefix", ""));
            if (message.contains("%prefix%")) {
                return message.replace("%prefix%", prefix);
            }
            return message;
        } else if (messageObj instanceof List) {
            List<String> messageList = (List<String>) messageObj;
            String prefix = ChatUtil.colorizeHex(config.getString("prefix", ""));
            messageList = messageList.stream()
                    .map(ChatUtil::colorizeHex)
                    .map(msg -> msg.contains("%prefix%") ? msg.replace("%prefix%", prefix) : msg)
                    .toList();
            return String.join("\n", messageList);
        }

        return "Invalid message format";
    }

    public YamlDocument getConfig() {
        return config;
    }

    public void reloadConfig() {
        setupConfig();
        setupMessages();
    }
}