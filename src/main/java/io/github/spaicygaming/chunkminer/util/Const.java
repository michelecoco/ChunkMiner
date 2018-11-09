package io.github.spaicygaming.chunkminer.util;

import io.github.spaicygaming.chunkminer.ChunkMiner;

public class Const {

    private static ChunkMiner main = ChunkMiner.getInstance();

    /*
    These are temporarily here
     */

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