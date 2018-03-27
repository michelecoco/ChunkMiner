package io.github.spaicygaming.chunkminer.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.spaicygaming.chunkminer.ChunkMiner;
import io.github.spaicygaming.chunkminer.Miner;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import io.github.spaicygaming.chunkminer.util.Const;
import io.github.spaicygaming.chunkminer.util.MinerItem;

public class InteractListener implements Listener {

	private ChunkMiner main;
	private MinerItem minerItem;

	public InteractListener(ChunkMiner main) {
		this.main = main;
		minerItem = main.getMinerItem();
	}

	@EventHandler
	public void onBlockPlace(PlayerInteractEvent event) {
		// Check the Action and the permissions
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().hasPermission(Const.PERM_PLACE))
			return;

		// Return if the item is not a ChunkMiner
		if (!minerItem.isSimilar(event.getItem())) return;
		
		/*
		 * TODO: add a confirm gui
		 */
		
		event.setCancelled(true);
		Player player = event.getPlayer();
		
		// The chunk the player is in
		Chunk chunk = event.getClickedBlock().getLocation().getChunk();
		
		// Scan and mine the chunk
		Miner miner = new Miner(chunk, player, main.getWorldGuard());
		// If the player is not allowed to build in this region...
		if (!miner.scan()) {
			player.sendMessage(ChatUtil.c("notAllowedHere"));
			return;
		}
		
		// Remove the ChunkMiner from player's hand
		player.getInventory().setItemInHand(removeOneItem(player.getInventory().getItemInHand()));
		player.updateInventory();
		
		// Action start message
		player.sendMessage(ChatUtil.c("minerPlaced"));
		
		miner.mine();
		
		// Action finished Message
		player.sendMessage(ChatUtil.c("minerSuccess"));
		
		// Notify staffers
		notifyStaffers(player.getName(), chunk);
	}

	/**
	 * Decrease by one the amount of items in the ItemStack
	 * @param item The ItemStack
	 * @return the ItemStack with one item less
	 */
	private ItemStack removeOneItem(ItemStack item) {
		ItemStack tempItem = item;
		int amount = tempItem.getAmount();

		if (amount == 1) {
			tempItem = null;
		} else {
			tempItem.setAmount(amount - 1);
		}
		return tempItem;
	}
	
	/**
	 * Notify all staffers that the player used a ChunkMiner
	 * @param playerName The name of the player who placed the ChunkMiner
	 * @param chunk The chunk mined
	 */
	private void notifyStaffers(String playerName, Chunk chunk) {
		for (Player staffer : main.getServer().getOnlinePlayers()) {
			if (!staffer.hasPermission(Const.PERM_NOTIFY))
				continue;
			staffer.sendMessage(ChatUtil.c("minerNotifyStaff").replace("{playerName}", playerName)
					.replace("{world}", chunk.getWorld().getName())
					.replace("{x}", String.valueOf(chunk.getX()))
					.replace("{z}", String.valueOf(chunk.getZ())));
		}
	}
	
}
