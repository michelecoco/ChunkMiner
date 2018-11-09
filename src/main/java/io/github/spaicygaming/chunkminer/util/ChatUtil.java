package io.github.spaicygaming.chunkminer.util;

import io.github.spaicygaming.chunkminer.ChunkMiner;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class ChatUtil {

    /**
     * Prevent utility class initialization
     */
    private ChatUtil() {
    }

    /**
     * Main class instance
     */
    private static ChunkMiner main = ChunkMiner.getInstance();

    /**
     * Chat prefix
     */
    private static String prefix = color("Messages.prefix") + ChatColor.RESET + " ";

    /**
     * Return the colored chat prefix specified in the configuration file
     *
     * @return the prefix
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * Color the String translating it to ChatColor using the character '&'
     *
     * @param string the String to color
     * @return the colored string
     */
    private static String colorString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Color the string using {@link #color(String)} and append it to the prefix
     *
     * @param configMessageKey The subkey of config.yml's ConfigurationSection "Messages"
     * @return the prefix + colored message
     */
    public static String c(String configMessageKey) {
        return prefix + color("Messages." + configMessageKey);
    }

    /**
     * Retrieve the string from the config.yml and color it using {@link #colorString(String)}
     *
     * @param configPath The path to the string in the config.yml
     * @return the colored string
     */
    static String color(String configPath) {
        return colorString(main.getConfig().getString(configPath));
    }

    /**
     * Color each string of the list using {@link #colorString(String)}
     *
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
     * Capitalize the first letter.
     * The string must be at least one character long.
     *
     * @param str the string to capitalize
     * @return the capitalized String
     */
    public static String capitalizeFirstChar(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Send an alert message to the ConsoleSender
     *
     * @param message The message to display
     */
    public static void alert(String message) {
        main.getServer().getConsoleSender().sendMessage("[ChunkMiner] " + ChatColor.RED + message);
    }

    /**
     * @return how to name console in messages. It's specified in the configuration file (config.yml)
     */
    public static String getConsoleName() {
        return main.getConfig().getString("MainSettings.consoleName");
    }


}
