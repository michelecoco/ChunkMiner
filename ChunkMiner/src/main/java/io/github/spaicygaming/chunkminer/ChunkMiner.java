package io.github.spaicygaming.chunkminer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import io.github.spaicygaming.chunkminer.cmd.CMCommands;
import io.github.spaicygaming.chunkminer.listeners.InteractListener;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import io.github.spaicygaming.chunkminer.util.Const;
import io.github.spaicygaming.chunkminer.util.MinerItem;

public class ChunkMiner extends JavaPlugin {

	private static ChunkMiner instance;
	private MinerItem minerItem;
	
	private double configVersion = 1.2;
	
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
		
		getLogger().info("ChunkMiner has been enabled!");
	}
	
	public static ChunkMiner getInstance() {
		return instance;
	}
	
	/**
	 * Return the instance of MinerItem
	 * @return
	 */
	public MinerItem getMinerItem() {
		return minerItem;
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
	 * Disable the plugin and send fancy messages to the console
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
	 * @return null if WorldGuard is not installed
	 */
	public WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	    
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null;
	    }

	    return (WorldGuardPlugin) plugin;
	}
	
	/**
	 * Check whether Factions plugin is installed
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
