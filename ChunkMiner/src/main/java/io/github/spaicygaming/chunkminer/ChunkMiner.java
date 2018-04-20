package io.github.spaicygaming.chunkminer;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.github.spaicygaming.chunkminer.cmd.CMCommands;
import io.github.spaicygaming.chunkminer.listeners.InteractListener;
import io.github.spaicygaming.chunkminer.listeners.JoinListener;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import io.github.spaicygaming.chunkminer.util.Const;
import io.github.spaicygaming.chunkminer.util.MinerItem;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ChunkMiner extends JavaPlugin {

    private static ChunkMiner instance;
    private MinerItem minerItem;
    private UpdateChecker updateChecker;

    /**
     * Chunks currently processed (scanning or mining operation in progress)
     */
    private Set<Chunk> currentlyProcessedChunks = new HashSet<>();

    /**
     * Configuration file version (it's up here simply for reasons of ease)
     */
    private double configVersion = 1.4;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        checkConfigVersion();

        // ChunkMiner item
        minerItem = new MinerItem(this);

        // Initialize "ignored materials" List
        getConfig().getStringList("MainSettings.ignoreMaterials")
                .forEach(mat -> Const.IGNORED_MATERIALS.add(Material.valueOf(mat)));

        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getCommand("chunkminer").setExecutor(new CMCommands());


        // WorldGuard hook message
        hookMessage("WorldGuard", Const.WORLDGUARD_HOOK, getWorldGuard() != null);
        // Factions hook message
        hookMessage("Factions", Const.FACTIONS_HOOK, isFactionsInstalled());

        // Update Checker
        if (Const.NOTIFY_UPDATES) {
            checkForUpdates();
            getServer().getPluginManager().registerEvents(new JoinListener(getUpdateChecker()), this);
        }


        getLogger().info("ChunkMiner has been enabled!");
    }

    public static ChunkMiner getInstance() {
        return instance;
    }

    /**
     * @return the instance of MinerItem
     */
    public MinerItem getMinerItem() {
        return minerItem;
    }

    /**
     * @return the set containing the chunks in which there is an operation in progress
     */
    public Set<Chunk> getCurrentlyProcessedChunks() {
        return currentlyProcessedChunks;
    }

    /**
     * Check the configuration file version.
     * Notify the ConsoleSender if the config.yml is outdated
     */
    private void checkConfigVersion() {
        if (getConfig().getDouble("configVersion") < configVersion) {
            ChatUtil.alert("OUTDATED config.yml FILE DETECTED, PLEASE DELETE THE OLD ONE!");
            ChatUtil.alert("You can also manually update it: https://github.com/SpaicyGaming/ChunkMiner/blob/master/ChunkMiner/src/main/resources/config.yml");
        }
    }

    /**
     * Check for updates and notify the console
     */
    private void checkForUpdates() {
        getLogger().info("Checking for updates...");
        updateChecker = new UpdateChecker(Double.parseDouble(getDescription().getVersion()));

        if (updateChecker.availableUpdate()) {
            ChatUtil.alert("New version available!");
            ChatUtil.alert("Your version: " + getUpdateChecker().getCurrentVersion());
            ChatUtil.alert("Latest version: " + getUpdateChecker().getLatestVersion());
        } else
            getLogger().info("No new version available :(");
    }

    /**
     * Return the instance of UpdateChecker.
     *
     * @return null if update checking is disabled from the config.yml
     */
    private UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    /**
     * Disable the plugin and send fancy messages to the console
     *
     * @param reason The reason why the plugin has been disabled
     */
    public void disable(String reason) {
        ConsoleCommandSender cs = getServer().getConsoleSender();

        cs.sendMessage(ChatColor.RED + ChatUtil.getSeparators('=', 70));
        cs.sendMessage(ChatColor.AQUA + "[ChunkMiner] " + ChatColor.RED + "Fatal Error!");
        cs.sendMessage(ChatColor.RED + reason);
        cs.sendMessage(ChatColor.RED + "Disabling plugin...");
        cs.sendMessage(ChatColor.RED + ChatUtil.getSeparators('=', 70));

        getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Return WorldGuard plugin instance
     *
     * @return null if WorldGuard is not installed
     */
    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // May be another plugin
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    /**
     * Check whether Factions plugin is installed
     *
     * @return true if it is
     */
    public boolean isFactionsInstalled() {
        Plugin factions = getServer().getPluginManager().getPlugin("Factions");

        return factions != null && factions.isEnabled();
    }

    private void hookMessage(String pluginName, boolean configCondition, boolean pluginLoaded) {
        if (configCondition) {
            if (pluginLoaded)
                getLogger().info("Hooked into " + pluginName);
            else
                getLogger().info("Can't hook into " + pluginName + ", plugin not found");
        }
    }

}
