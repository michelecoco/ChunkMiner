package io.github.spaicygaming.chunkminer.hooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

abstract class PluginIntegration {

    private final Plugin plugin;
    private final String pluginName;
    private final boolean integrationEnabledInConfig, installed; // TODO allow to disable integrations by reloading the configuration files

    public PluginIntegration(String pluginName, boolean integrationEnabledInConfig) {
        this.pluginName = pluginName;
        this.integrationEnabledInConfig = integrationEnabledInConfig;
        this.plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        this.installed = plugin != null && plugin.isEnabled();

        sendIntegrationResultMessages();
    }

    /**
     * Checks whether the player is allowed to build at the given location according to
     *
     * @param player   the player who placed the miner
     * @param location the location of the block he interacted with
     * @return true if the player is allowed
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // inverted to increase clarity
    public abstract boolean canBuildHere(Player player, Location location);

    /**
     * Logs whether the hook was successful
     */
    private void sendIntegrationResultMessages() {
        if (integrationEnabledInConfig) {
            if (installed)
                Bukkit.getLogger().info("Hooked into " + pluginName);
            else
                Bukkit.getLogger().info("Can't hook into " + pluginName + ", plugin not found");
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    boolean isInstalled() {
        return installed;
    }

    /**
     * Checks whether to perform operations involving the integrated plugin
     *
     * @return true if the plugin integration is enabled and the plugin is running
     * @implSpec This implementation returns {@code this.integrationEnabledInConfig && this.installed}
     */
    public boolean shouldPerformChecks() {
        return this.integrationEnabledInConfig && this.installed;
    }

}
