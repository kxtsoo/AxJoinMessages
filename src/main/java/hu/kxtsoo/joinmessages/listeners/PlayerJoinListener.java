package hu.kxtsoo.joinmessages.listeners;

import de.myzelyam.api.vanish.VanishAPI;
import hu.kxtsoo.joinmessages.utils.ConfigUtil;
import hu.kxtsoo.joinmessages.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private final ConfigUtil configUtil;

    public PlayerJoinListener(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        var section = configUtil.getConfig().getSection("join-messages.messages");

        boolean usePremiumVanish = configUtil.getConfig().getString("options.hooks.vanish-plugin", "")
                .equalsIgnoreCase("PremiumVanish");

        if (usePremiumVanish && VanishAPI.isInvisible(player)) {
            event.setJoinMessage(null);
            return;
        }

        boolean disableVanillaMessage = configUtil.getConfig().getBoolean("options.vanilla-join-message", true);
        if (disableVanillaMessage) {
            event.setJoinMessage(null);
        }

        if (section == null) {
            return;
        }

        boolean usePriorities = configUtil.getConfig().getBoolean("options.use-priorities", true);
        String selectedMessage = null;

        if (usePriorities) {
            int highestPriority = Integer.MIN_VALUE;

            for (String key : section.getRoutesAsStrings(false)) {
                boolean enabled = configUtil.getConfig().getBoolean("join-messages.messages." + key + ".enabled", false);
                String permission = configUtil.getConfig().getString("join-messages.messages." + key + ".permission", "");
                int priority = configUtil.getConfig().getInt("join-messages.messages." + key + ".priority", 0);
                List<String> messages = configUtil.getConfig().getStringList("join-messages.messages." + key + ".message");

                if (enabled && player.hasPermission(permission) && priority > highestPriority) {
                    highestPriority = priority;
                    selectedMessage = String.join("\n", messages).replace("%player%", player.getName());
                }
            }
        } else {
            for (String key : section.getRoutesAsStrings(false)) {
                boolean enabled = configUtil.getConfig().getBoolean("join-messages.messages." + key + ".enabled", false);
                String permission = configUtil.getConfig().getString("join-messages.messages." + key + ".permission", "");
                List<String> messages = configUtil.getConfig().getStringList("join-messages.messages." + key + ".message");

                if (enabled && player.hasPermission(permission)) {
                    selectedMessage = String.join("\n", messages).replace("%player%", player.getName());
                    break;
                }
            }
        }

        if (selectedMessage != null) {
            Bukkit.broadcastMessage(ChatUtil.colorizeHex(selectedMessage));
        }
    }
}