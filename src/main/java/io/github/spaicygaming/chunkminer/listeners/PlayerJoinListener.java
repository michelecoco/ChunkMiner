package io.github.spaicygaming.chunkminer.listeners;

import io.github.spaicygaming.chunkminer.Permission;
import io.github.spaicygaming.chunkminer.UpdateChecker;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Notify players about plugin updates
 */
public class PlayerJoinListener implements Listener {

    private UpdateChecker updateChecker;

    public PlayerJoinListener(UpdateChecker updateChecker) {
        this.updateChecker = updateChecker;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // If this code run means that the listener is registered and update-checking is enabled in the config.yml

        Player player = event.getPlayer();
        if (updateChecker.availableUpdate() && Permission.NOTIFY_UPDATES.has(player)) {
            player.sendMessage(ChatColor.GOLD + ChatUtil.getSeparators('=', 45));
            player.sendMessage(ChatUtil.getPrefix() + ChatColor.GREEN + "There is a new update available!");
            player.sendMessage(ChatColor.AQUA + "Your Version: " + ChatColor.YELLOW + updateChecker.getCurrentVersion());
            player.sendMessage(ChatColor.AQUA + "Latest Version: " + ChatColor.YELLOW + updateChecker.getLatestVersion());
            player.sendMessage(ChatColor.GRAY + "Download From: https://www.spigotmc.org/resources/54969/");
            player.sendMessage(ChatColor.GOLD + ChatUtil.getSeparators('=', 45));
        }
    }

}
