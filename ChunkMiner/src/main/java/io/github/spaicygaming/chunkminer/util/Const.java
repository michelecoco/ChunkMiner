package io.github.spaicygaming.chunkminer.util;

import io.github.spaicygaming.chunkminer.ChunkMiner;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Const {

    private static ChunkMiner main = ChunkMiner.getInstance();

    // Check for updates?
    public static boolean NOTIFY_UPDATES = main.getConfig().getBoolean("CheckForUpdates");

    // The min y height the miner starts mine from
    public static int MIN_HEIGHT = main.getConfig().getInt("MainSettings.minHeight");

    // The miner ignores blocks of one of these materials
    public static List<Material> IGNORED_MATERIALS = new ArrayList<>();

    // Console name
    public static String CONSOLE_NAME = main.getConfig().getString("MainSettings.consoleName");

    // Hooks
    public static boolean WORLDGUARD_HOOK = main.getConfig().getBoolean("MainSettings.hooks.WorldGuard");
    public static boolean FACTIONS_HOOK = main.getConfig().getBoolean("MainSettings.hooks.FactionsUUID.enabled");

    // Users Permissions
    public static final String PERM_PLACE = "chunkminer.place";

    // Staff Permissions
    public static final String PERM_GET = "chunkminer.get";
    public static final String PERM_GIVE = "chunkminer.give";
    public static final String PERM_NOTIFY_ON_USE = "chunkminer.notify.onuse"; // get notified when a player use a ChunkMiner
    public static final String PERM_NOTIFY_UPDATES = "chunkminer.notify.updates";

}
