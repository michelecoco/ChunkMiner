package io.github.spaicygaming.chunkminer.miner;

import io.github.spaicygaming.chunkminer.hooks.WorldGuardIntegration;
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

    private final MinersManager minersManager;

    /**
     * The chunk the player placed the miner
     */
    private final Chunk chunk;

    /**
     * The player who placed the miner
     */
    private final Player player;

    /**
     * Player's UUID
     */
    private final UUID playerUniqueId;

    /**
     * WorldGuardHook instance
     */
    private final WorldGuardIntegration worldGuardIntegration;

    /**
     * Set the given Block's material to AIR
     */
    private final static Consumer<Block> setAir = block -> block.setType(Material.AIR);

    public Miner(Chunk chunk, Player player, MinersManager minersManager, WorldGuardIntegration worldGuardIntegration) {
        this.chunk = chunk;
        this.player = player;
        this.playerUniqueId = player.getUniqueId();
        this.worldGuardIntegration = worldGuardIntegration;
        this.minersManager = minersManager;
    }

    /**
     * A list containing the blocks inside the chunk that must be replaced with AIR.
     */
    private List<Block> blocksToRemove;

    /**
     * Scans the blocks inside the chunk and add them in the collection {@link #blocksToRemove}.
     * <p>
     * While the operation is in progress the chunk should be in the collection containing
     * every currently processed chunk: {@link MinersManager#getActiveOperations()}
     *
     * @return false if the player is not allowed to build at any location of the chunk
     */
    public boolean scan() {
        World world = chunk.getWorld();

        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;

        blocksToRemove = IntStream.range(x, x + 16)
//                .parallel()
                .mapToObj(pX -> IntStream.range(z, z + 16)
                        .mapToObj(pZ -> IntStream.rangeClosed(minersManager.getMinHeight(), world.getMaxHeight())
                                .mapToObj(pY -> world.getBlockAt(pX, pY, pZ))))
                .flatMap(Function.identity())
                .flatMap(Function.identity())
                .filter(block -> !block.isEmpty())
                .filter(block -> !minersManager.isMaterialIgnored(block.getType()))
                .collect(Collectors.toList());

        if (worldGuardIntegration.shouldPerformChecks()) {
            //noinspection SimplifyStreamApiCallChains for better performance
            return !blocksToRemove.stream().map(Block::getLocation).anyMatch(processedBlock -> !worldGuardIntegration.canBuildHere(player, processedBlock));
        }

        return true;
    }

    /**
     * Replaces the blocks with AIR.
     */
    public void mine() {
        // Remove all blocks by iterating the list
        blocksToRemove.forEach(setAir);
    }

    /**
     * Gets the amount of blocks in the Chunk that can be removed.
     * <p>
     * NOTE: {@link #mine()} must have been called before, otherwise a NullPointerException is thrown
     *
     * @return the amount of blocks
     */
    public int getBlocksAmount() {
        return blocksToRemove.size();
    }

    /**
     * Adds the {@link #chunk} in the Set containing all the chunks in which there is an active operation
     * started by the player
     */
    public void operationStarted() {
        Map<UUID, Set<Chunk>> operations = minersManager.getActiveOperations();

        // The scan/mine operation is so fast (and sequential) that players won't probably never have another running miner
        if (operations.containsKey(playerUniqueId)) {
            operations.get(playerUniqueId).add(chunk);
        } else {
            operations.put(playerUniqueId, Collections.singleton(chunk));
        }
    }

    /**
     * Removes the {@link #chunk} from the Set containing the chunks in which there is an active operation
     */
    public void operationFinished() {
        Map<UUID, Set<Chunk>> operations = minersManager.getActiveOperations();

        if (operations.get(playerUniqueId).size() > 1) {
            operations.get(playerUniqueId).remove(chunk);
        } else {
            operations.remove(playerUniqueId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Miner miner = (Miner) o;
        return Objects.equals(chunk, miner.chunk) &&
                Objects.equals(playerUniqueId, miner.playerUniqueId) &&
                Objects.equals(blocksToRemove, miner.blocksToRemove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk, playerUniqueId, blocksToRemove);
    }
}
