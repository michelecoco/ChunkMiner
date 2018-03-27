package io.github.spaicygaming.chunkminer.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import io.github.spaicygaming.chunkminer.ChunkMiner;

public class ChatUtil {

	private static ChunkMiner main = ChunkMiner.getInstance();

	private static String prefix = color("Messages.prefix") + ChatColor.RESET + " ";
	
	/**
	 * Color the String
	 * @param string
	 * @return the colored string
	 */
	public static String colorString(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String c(String configMessageKey) {
		return prefix + color("Messages." + configMessageKey);
	}
	
	public static String color(String configPath) {
		return colorString(main.getConfig().getString(configPath));
	}
	
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
