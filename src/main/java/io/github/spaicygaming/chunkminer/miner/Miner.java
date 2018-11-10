package io.github.spaicygaming.chunkminer.miner;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.github.spaicygaming.chunkminer.hooks.WorldGuardHook;
import io.github.spaicygaming.chunkminer.util.Const;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Miner {

    private MinersManager minersManager;

    /**
     * The chunk the player placed the miner
     */
    private Chunk chunk;

    /**
     * The player who placed the miner
     */
    private Player player;

    /**
     * Player's UUID
     */
    private UUID playerUniqueId;

    /**
     * WorldGuardHook instance
     */
    private WorldGuardHook worldGuardHook;

    /**
     * Set block material to AIR
     */
    private final static Consumer<Block> setAir = block -> block.setType(Material.AIR);

    public Miner(Chunk chunk, Player player, MinersManager minersManager, WorldGuardHook worldGuardHook) {
        this.chunk = chunk;
        this.player = player;
        this.playerUniqueId = player.getUniqueId();
        this.worldGuardHook = worldGuardHook;
        this.minersManager = minersManager;
    }

    /**
     * List containing the non-ignored blocks inside the chunk to replace with AIR.
     */
    private List<Block> blocksToRemove;

    /**
     * Scan the blocks inside the chunk and add
     * them in {@link #blocksToRemove}.
     * <p>
     * While the operation is in progress the chunk is added
     * to the Set containing currently processed chunks.
     *
     * @return false if the player is not allowed to build in this region
     */
    public boolean scan() {
        World world = chunk.getWorld();

        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;

        blocksToRemove = IntStream.range(x, x + 16)
//                .parallel()
                .mapToObj(pX -> IntStream.range(z, z + 16)
                        .mapToObj(pZ -> IntStream.rangeClosed(minersManager.getMinHeght(), world.getMaxHeight())
                                .mapToObj(pY -> world.getBlockAt(pX, pY, pZ))))
                .flatMap(Function.identity())
                .flatMap(Function.identity())
                .filter(block -> !block.isEmpty())
                .filter(block -> !minersManager.isMaterialIgnored(block.getType()))
                .collect(Collectors.toList());

        if (worldGuardHook.performChecks()) {
            //noinspection SimplifyStreamApiCallChains for better performance
            return !blocksToRemove.stream().map(Block::getLocation).anyMatch(processedBlock -> !worldGuardHook.canBuild(player, processedBlock));
        }

        return true;
    }

    /**
     * Replace all not-ignored blocks with AIR.
     */
    public void mine() {
        // Remove all blocks by iterating the list
        blocksToRemove.forEach(setAir);
    }

    /**
     * Returns the amount of blocks in the Chunk that can be removed.
     * {@link #mine()} must be called before.
     *
     * @return the amount of blocks
     */
    public int getBlocksAmount() {
        return blocksToRemove.size();
    }

    /**
     * Add the {@link #chunk} in the Set containing all the chunks in which there is an active operation
     * started by the player
     */
    public void operationStarted() {
        Map<UUID, Set<Chunk>> operations = minersManager.getActiveOperations();

        // The scan/mine operation is so fast (and sequential) that players won't probably never have another running miner
        if (operations.containsKey(playerUniqueId)) {
            operations.get(playerUniqueId).add(chunk);
        } else {
            operations.put(playerUniqueId, new HashSet<>(Arrays.asList(chunk)));
        }
    }

    /**
     * Remove the {@link #chunk} from the Set in the main class
     * containing all the chunks in which there is an active operation
     */
    public void operationFinished() {
        Map<UUID, Set<Chunk>> operations = minersManager.getActiveOperations();

        if (operations.get(playerUniqueId).size() > 1) {
            operations.get(playerUniqueId).remove(chunk);
        } else {
            operations.remove(playerUniqueId);
        }
    }

}
