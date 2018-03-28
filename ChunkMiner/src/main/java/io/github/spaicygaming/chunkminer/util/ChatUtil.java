package io.github.spaicygaming.chunkminer.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import io.github.spaicygaming.chunkminer.ChunkMiner;

public class ChatUtil {

	/**
	 * Main class instance
	 */
	private static ChunkMiner main = ChunkMiner.getInstance();

	/**
	 * Chat prefix
	 */
	private static String prefix = color("Messages.prefix") + ChatColor.RESET + " ";
	
	/**
	 * Color the String translating it to ChatColor using the character '&'
	 * @param string
	 * @return the colored string
	 */
	public static String colorString(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	/**
	 * Color the string using {@link #color(String)} and append it to the prefix
	 * @param configMessageKey The subkey of config.yml's ConfigurationSection "Messages"
	 * @return the prefix + colored message 
	 */
	public static String c(String configMessageKey) {
		StringBuilder stringBuilder = new StringBuilder(prefix);
		stringBuilder.append(color("Messages." + configMessageKey));
		return stringBuilder.toString();
	}
	
	/**
	 * Retrive the string from the config.yml and color it using {@link #colorString(String)}
	 * @param configPath The path to the string in the config.yml
	 * @return the colored string
	 */
	public static String color(String configPath) {
		return colorString(main.getConfig().getString(configPath));
	}
	
	/**
	 * Color each string of the list using {@link #colorString(String)}
	 * @param lores The list of strings to color
	 * @return the colored list
	 */
	public static List<String> color(List<String> lores) {
		List<String> coloredStings = new ArrayList<>();
		lores.forEach(str -> coloredStings.add(colorString(str)));
		return coloredStings;
	}

	public static String getSeparators(char character, int amount) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i <= amount; i++) {
			stringBuilder.append(character);
		}
		return stringBuilder.toString();
	}

	/**
	 * Send an alert message to the ConsoleSender
	 * @param message The message to display
	 */
	public static void alert(String message) {
		main.getServer().getConsoleSender().sendMessage("[ChunkMiner] " + ChatColor.RED + message);
	}
	
}
