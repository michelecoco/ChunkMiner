package io.github.spaicygaming.chunkminer.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import io.github.spaicygaming.chunkminer.ChunkMiner;

public class Const {
	
	private static ChunkMiner main = ChunkMiner.getInstance();
	
	// The min height the miner starts mine from
	public static int MIN_HEIGHT = main.getConfig().getInt("MainSettings.minHeight");
	
	// The miner ignores blocks of one of these materials
	public static List<Material> IGNORED_MATERIALS = new ArrayList<>();
	
	// Users Permissions
	public final static String PERM_PLACE = "chunkminer.place";
	
	// Staff Permissions
	public final static String PERM_NOTIFY = "chunkminer.notify";
	public final static String PERM_GET = "chunkminer.get";
	public final static String PERM_GIVE = "chunkminer.give";
	
}
