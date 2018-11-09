package io.github.spaicygaming.chunkminer.listeners;

import com.massivecraft.factions.*;
import io.github.spaicygaming.chunkminer.ChunkMiner;
import io.github.spaicygaming.chunkminer.miner.Miner;
import io.github.spaicygaming.chunkminer.miner.MinersManager;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import io.github.spaicygaming.chunkminer.util.Const;
import io.github.spaicygaming.chunkminer.util.MinerItem;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private ChunkMiner main;
    private MinerItem minerItem;
    private MinersManager minersManager;

    public InteractListener(ChunkMiner main, MinersManager minersManager) {
        this.main = main;
        this.minerItem = main.getMinerItem();
        this.minersManager = minersManager;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onMinerPlace(PlayerInteractEvent event) {
        // Check the Action
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        // Return if the ItemStack in hand isn't a ChunkMiner item
        if (!minerItem.isSimilar(event.getItem()))
            return;

        // Cancel the event
        event.setCancelled(true);

        // Update the inventory to prevent glitchy items
        Player player = event.getPlayer();
        player.updateInventory();

        // Return is the player does not have permission
        if (!player.hasPermission(Const.PERM_PLACE)) {
            player.sendMessage(ChatUtil.c("noPlacePerms").replace("{perm}", Const.PERM_PLACE));
            return;
        }

        // Return if the player tried to place the miner in a blacklisted world and he doesn't have a bypass permission
        String worldName = player.getWorld().getName();
        if (!player.hasPermission("chunkminer.bypass.worlds")
                && main.getConfig().getStringList("MainSettings.blacklistedWorlds").contains(worldName)) {
            player.sendMessage(ChatUtil.c("blacklistedWorld").replace("{world}", worldName));
            return;
        }

        // Return if the player is in a not allowed gamemode (specified in the configuration file) and he doesn't have a bypass permission
        if (!player.hasPermission("chunkminer.bypass.gamemodes") && !isInAllowedGamemode(player)) {
            player.sendMessage(ChatUtil.c("notAllowedGamemode")
                    .replace("{gamemode}", ChatUtil.capitalizeFirstChar(player.getGameMode().toString())));
            return;
        }

        // Return if the player has reached the max amount of miners he can place at once
        if (minersManager.getActiveOperations(player.getUniqueId()).size() >= minersManager.getMaxMinersAmountAtOnce()) {
            player.sendMessage(ChatUtil.c("maxAmountReached")
                    .replace("{max_amount}", Integer.toString(minersManager.getMaxMinersAmountAtOnce())));
            return;
        }

        // FactionsUUID checks (check if the player is allowed to place the miner in this claim)
        if (Const.FACTIONS_HOOK && main.isFactionsInstalled())
            if (!canBuildHereFactions(player, event.getClickedBlock().getLocation())) {
                player.sendMessage(ChatUtil.c("notAllowedHereFactions"));
                return;
            }

        /*
         * TODO: add a confirm gui
         */

        // The chunk the player placed the miner in
        Chunk chunk = event.getClickedBlock().getLocation().getChunk();

        // Return if there is currently an active process in the chunk
        if (minersManager.isCurrentlyProcessed(chunk)) {
            player.sendMessage(ChatUtil.c("currentlyProcessed"));
            return;
        }

        // Initialize the Miner
        Miner miner = new Miner(chunk, player, main.getWorldGuard(), minersManager);

        // Action start message
        player.sendMessage(ChatUtil.c("minerPlaced"));

        // Perform miner-started actions
        miner.operationStarted();

        // Notify the player if he isn't allowed to build at at least one block location in a WorldGuard region
        // contained in the chunk
        if (!miner.scan()) {
            player.sendMessage(ChatUtil.c("notAllowedHere"));
        } else {
            // Notify the player if there are no blocks to remove (chunk has probably already been mined)
            if (miner.getBlocksAmount() == 0) {
                player.sendMessage(ChatUtil.c("chunkAlreadyMined"));
            } else {
                // Mine the chunk
                miner.mine();

                // Remove the ChunkMiner item from player's hand
                player.getInventory().setItemInHand(removeOneItem(player.getInventory().getItemInHand()));
                player.updateInventory(); // To prevent glitchy items

                // Action finished Message
                player.sendMessage(ChatUtil.c("minerSuccess"));

                // Notify staff members that someone placed a ChunkMiner
                notifyStaffMembers(player.getName(), chunk);
            }
        }
        // Perform miner-finished actions
        miner.operationFinished();
    }

    /**
     * Check whether the player is in a allowed gamemode
     * to place ChunkMiners
     *
     * @param player The player whose gamemode check
     * @return true if he is
     */
    private boolean isInAllowedGamemode(Player player) {
        return !main.getConfig().getStringList("MainSettings.blockedGamemodes").contains(player.getGameMode().toString());
    }

    /**
     * Check whether the player is allowed by FactionsUUID
     * to build at that location
     *
     * @param player   The player who placed the miner
     * @param location The location of the block he interacted with
     * @return true if he is
     */
    private boolean canBuildHereFactions(Player player, Location location) {
        final String configSectionName = "MainSettings.hooks.FactionsUUID.allow";

        // The FPlayer who placed the miner
        FPlayer factionPlayer = FPlayers.getInstance().getByPlayer(player);

        // Player can bypass factions restrictions
        if (Conf.playersWhoBypassAllProtection.contains(factionPlayer.getName()) || factionPlayer.isAdminBypassing())
            return true;

        // The Faction that owns the chunk at miner location
        Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(location));

        // Return true if it's wilderness
        if (otherFaction.isWilderness())
            return true;

        // Own claim
        if (factionPlayer.getFactionId().equals(otherFaction.getId())) {
            return main.getConfig().getStringList(configSectionName + ".roles")
                    .contains(factionPlayer.getRole().toString().toUpperCase());
        }

        // Not own claim
        return false;
    }

    /**
     * Decrease by one the amount of items in the ItemStack
     *
     * @param item The ItemStack
     * @return the ItemStack with one item less
     */
    private ItemStack removeOneItem(ItemStack item) {
        ItemStack itemResult = item;
        int amount = itemResult.getAmount();

        if (amount == 1) {
            itemResult = null;
        } else {
            itemResult.setAmount(amount - 1);
        }
        return itemResult;
    }

    /**
     * Notify all staff members that someone used a ChunkMiner
     *
     * @param playerName The name of the player who placed the ChunkMiner
     * @param chunk      The chunk mined
     */
    private void notifyStaffMembers(String playerName, Chunk chunk) {
        for (Player staffer : main.getServer().getOnlinePlayers()) {
            if (!staffer.hasPermission(Const.PERM_NOTIFY_ON_USE) || staffer.getName().equals(playerName))
                continue;

            staffer.sendMessage(ChatUtil.c("minerNotifyStaff").replace("{playerName}", playerName)
                    .replace("{world}", chunk.getWorld().getName())
                    .replace("{x}", String.valueOf(chunk.getX()))
                    .replace("{z}", String.valueOf(chunk.getZ())));
        }
    }


}