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
     * Returns the colored chat prefix specified in the configuration file
     *
     * @return the prefix
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * Colors the String by translating it to ChatColor using the character '&'
     *
     * @param string the String to color
     * @return the colored string
     */
    private static String colorString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Colors the string and append it to the prefix
     *
     * @param configMessageKey the subkey of config.yml's ConfigurationSection "Messages"
     * @return the chat prefix  + colored message
     */
    public static String c(String configMessageKey) {
        return prefix + color("Messages." + configMessageKey);
    }

    /**
     * Retrieves the string from the config.yml and colors it using
     *
     * @param configPath The path to the string in the config.yml
     * @return the colored string
     */
    static String color(String configPath) {
        return colorString(main.getConfig().getString(configPath));
    }

    /**
     * Colors each string of the list
     *
     * @param stringList the list of strings to color
     * @return a new {@link ArrayList} instance which contains the colored strings
     */
    public static List<String> color(List<String> stringList) {
        List<String> coloredStings = new ArrayList<>();
        stringList.forEach(str -> coloredStings.add(colorString(str)));
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
     * Capitalizes the first letter of the given String.
     * The string must be at least one character long.
     *
     * @param str the string to capitalize
     * @return the capitalized String
     */
    public static String capitalizeFirstChar(String str) {
        if (str.length() < 1)
            throw new IllegalArgumentException("The string must contain at least one character");

        String lowerCaseStr = str.toLowerCase();
        return lowerCaseStr.substring(0, 1).toUpperCase() + lowerCaseStr.substring(1);
    }

    /**
     * Sends an alert message to the ConsoleSender
     *
     * @param message the message to display
     */
    public static void alert(String message) {
        main.getServer().getConsoleSender().sendMessage("[ChunkMiner] " + ChatColor.RED + message);
    }

    /**
     * @return how to name console in messages according to the configuration file (config.yml)
     */
    public static String getConsoleName() {
        return main.getConfig().getString("MainSettings.consoleName");
    }

}
