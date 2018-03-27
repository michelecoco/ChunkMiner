package io.github.spaicygaming.chunkminer.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.spaicygaming.chunkminer.ChunkMiner;

public class MinerItem {

	// Item properties
	private Material material;
	private String displayName;
	private List<String> lore;
	
	private ItemStack item;
	
	public MinerItem(ChunkMiner main) {
		main.getLogger().info("Loading ChunkMiner item properties...");
		// Initialize Item properties
		
		// Controlla se il material inserito nel config esiste
		String materialName = main.getConfig().getString("MinerItem.material");
		try {
			material = Material.valueOf(materialName);
		} catch (IllegalArgumentException e) {
			main.disable("The Material " + materialName + " does not exist.");
			return;
		}
		
		displayName = ChatUtil.color("MinerItem.displayName");
		lore = ChatUtil.color(main.getConfig().getStringList("MinerItem.lore"));
		
		init();
		
		main.getLogger().info("Item correctly loaded");
	}
	
	public void init() {
		item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.setDisplayName(displayName);
		itemMeta.setLore(lore);
		
		item.setItemMeta(itemMeta);
	}

	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * Give chunk miners to the player
	 * @param player
	 * @param amount
	 */
	public void give(Player player, int amount) {
		ItemStack tempItemStack = item;
		tempItemStack.setAmount(amount);
		player.getInventory().addItem(tempItemStack);
	}
	
	/**
	 * Check whether the ItemStack is a Mineritem.
	 * Ignores ItemStack amount.
	 * @param compareItemStack The ItemStack to compare
	 * @return
	 */
	public boolean isSimilar(ItemStack compareItemStack) {
		if (compareItemStack == null || !compareItemStack.hasItemMeta()) return false;
		
		ItemMeta ciMeta = compareItemStack.getItemMeta();
		
		return compareItemStack.getType() == material && ciMeta.getDisplayName().equals(displayName) && ciMeta.getLore().equals(lore);
	}
	
}
