package io.github.spaicygaming.chunkminer.listeners;

import io.github.spaicygaming.chunkminer.UpdateChecker;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import io.github.spaicygaming.chunkminer.util.Const;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Notify players about plugin updates
 */
public class JoinListener implements Listener {

    private UpdateChecker updateChecker;

    public JoinListener(UpdateChecker updateChecker) {
        this.updateChecker = updateChecker;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // If this code run means that the listener is registered,
        // and so update checking is enabled in the config.yml

        Player player = event.getPlayer();
        if (!player.hasPermission(Const.PERM_NOTIFY_UPDATES) || !updateChecker.availableUpdate())
            return;

        player.sendMessage(ChatColor.GOLD + ChatUtil.getSeparators('=', 45));
        player.sendMessage(ChatUtil.getPrefix() + ChatColor.GREEN + "There is a new update available!");
        player.sendMessage(ChatColor.AQUA + "Your Version: " + ChatColor.YELLOW + updateChecker.getCurrentVersion());
        player.sendMessage(ChatColor.AQUA + "Latest Version: " + ChatColor.YELLOW + updateChecker.getLatestVersion());
        player.sendMessage(ChatColor.GRAY + "Download From: https://www.spigotmc.org/resources/54969/");
        player.sendMessage(ChatColor.GOLD + ChatUtil.getSeparators('=', 45));
    }

}
