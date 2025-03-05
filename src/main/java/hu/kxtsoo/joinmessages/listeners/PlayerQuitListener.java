package hu.kxtsoo.joinmessages.listeners;

import de.myzelyam.api.vanish.VanishAPI;
import hu.kxtsoo.joinmessages.utils.ConfigUtil;
import hu.kxtsoo.joinmessages.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerQuitListener implements Listener {

    private final ConfigUtil configUtil;

    public PlayerQuitListener(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        var section = configUtil.getConfig().getSection("leave-messages.messages");

        boolean usePremiumVanish = configUtil.getConfig().getString("options.hooks.vanish-plugin", "")
                .equalsIgnoreCase("PremiumVanish");

        if (usePremiumVanish && VanishAPI.isInvisible(player)) {
            event.setQuitMessage(null);
            return;
        }

        boolean disableVanillaMessage = configUtil.getConfig().getBoolean("options.vanilla-join-message", true);
        if (disableVanillaMessage) {
            event.setQuitMessage(null);
        }

        if (section == null) {
            return;
        }

        boolean usePriorities = configUtil.getConfig().getBoolean("options.use-priorities", true);
        String selectedMessage = null;

        if (usePriorities) {
            int highestPriority = Integer.MIN_VALUE;

            for (String key : section.getRoutesAsStrings(false)) {
                boolean enabled = configUtil.getConfig().getBoolean("leave-messages.messages." + key + ".enabled", false);
                String permission = configUtil.getConfig().getString("leave-messages.messages." + key + ".permission", "");
                int priority = configUtil.getConfig().getInt("leave-messages.messages." + key + ".priority", 0);
                List<String> messages = configUtil.getConfig().getStringList("leave-messages.messages." + key + ".message");

                if (enabled && player.hasPermission(permission) && priority > highestPriority) {
                    highestPriority = priority;
                    selectedMessage = String.join("\n", messages).replace("%player%", player.getName());
                }
            }
        } else {
            for (String key : section.getRoutesAsStrings(false)) {
                boolean enabled = configUtil.getConfig().getBoolean("leave-messages.messages." + key + ".enabled", false);
                String permission = configUtil.getConfig().getString("leave-messages.messages." + key + ".permission", "");
                List<String> messages = configUtil.getConfig().getStringList("leave-messages.messages." + key + ".message");

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
