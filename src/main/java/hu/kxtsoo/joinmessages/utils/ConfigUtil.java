package hu.kxtsoo.joinmessages.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import hu.kxtsoo.joinmessages.AxJoinMessages;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigUtil {

    private final AxJoinMessages plugin;
    public static ConfigUtil configUtil;
    private YamlDocument config;
    private YamlDocument messages;
    private YamlDocument hooks;

    private final Map<Material, String> blockGroupMapping = new HashMap<>();

    public ConfigUtil(AxJoinMessages plugin) {
        this.plugin = plugin;
        setupConfig();
        setupMessages();
        loadBlockGroups();
        setupHooks();
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

    public void setupHooks() {
        try {
            File hooksFile = new File(plugin.getDataFolder(), "hooks.yml");
            if (!hooksFile.exists()) {
                plugin.saveResource("hooks.yml", false);
            }

            hooks = YamlDocument.create(hooksFile,
                    Objects.requireNonNull(plugin.getResource("hooks.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setKeepAll(true)
                            .setVersioning(new BasicVersioning("version")).build());

            hooks.update();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error loading or creating hooks.yml: " + ex.getMessage());
        }
    }

    public YamlDocument getConfig() {
        return config;
    }

    public YamlDocument getHooks() {
        return hooks;
    }

    public void reloadConfig() {
        setupConfig();
        setupMessages();
        loadBlockGroups();
    }

    private void loadBlockGroups() {
        blockGroupMapping.clear();

        var limitsSection = config.getSection("limits");
        if (limitsSection == null) {
            plugin.getLogger().warning("The 'limits' section is missing in the config.yml.");
            return;
        }

        Set<Object> limitKeys = limitsSection.getKeys();
        if (limitKeys == null || limitKeys.isEmpty()) {
            plugin.getLogger().warning("No keys found in the 'limits' section of the config.yml.");
            return;
        }

        for (Object key : limitKeys) {
            if (!(key instanceof String)) {
                plugin.getLogger().warning("Invalid key in 'limits' section: " + key);
                continue;
            }

            String groupKey = (String) key;
            String[] blocks = groupKey.split("\\+");

            for (String block : blocks) {
                Material material = Material.matchMaterial(block);
                if (material != null) {
                    blockGroupMapping.put(material, groupKey);
                } else {
                    plugin.getLogger().warning("Invalid material in config: " + block);
                }
            }
        }
    }

    public String getGroupKey(Material material) {
        return blockGroupMapping.getOrDefault(material, material.toString());
    }

    public List<Material> getGroupMaterials(String groupKey) {
        String[] blocks = groupKey.split("\\+");
        return Arrays.stream(blocks)
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Map<String, List<Material>> getConfiguredGroups() {
        Map<String, List<Material>> groups = new HashMap<>();

        var limitsSection = config.getSection("limits");
        if (limitsSection == null) {
            plugin.getLogger().warning("The 'limits' section is missing in the config.yml.");
            return groups;
        }

        Set<Object> limitKeys = limitsSection.getKeys();
        if (limitKeys == null || limitKeys.isEmpty()) {
            plugin.getLogger().warning("No keys found in the 'limits' section of the config.yml.");
            return groups;
        }

        for (Object key : limitKeys) {
            if (!(key instanceof String)) {
                plugin.getLogger().warning("Invalid key in 'limits' section: " + key);
                continue;
            }

            String groupKey = (String) key;
            List<Material> groupMaterials = getGroupMaterials(groupKey);
            if (!groupMaterials.isEmpty()) {
                groups.put(groupKey, groupMaterials);
            } else {
                plugin.getLogger().warning("No valid materials found for group: " + groupKey);
            }
        }

        return groups;
    }

}