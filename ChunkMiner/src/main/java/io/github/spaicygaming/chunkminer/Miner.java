package io.github.spaicygaming.chunkminer;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.github.spaicygaming.chunkminer.util.Const;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Miner {

    /**
     * The chunk the player placed the miner
     */
    private Chunk chunk;

    /**
     * The player who placed the miner
     */
    private Player player;

    /**
     * WorldGuard instance
     */
    private WorldGuardPlugin worldGuard;

    public Miner(Chunk chunk, Player player, WorldGuardPlugin worldGuard) {
        this.chunk = chunk;
        this.player = player;
        this.worldGuard = worldGuard;
    }

    /**
     * The blocks inside the chunk to replace with AIR.
     */
    private Set<Block> blocksToRemove = new HashSet<>();

    /**
     * Scan the blocks inside the chunk and add
     * them in {@link #blocksToRemove}
     *
     * @return false if the player is not allowed to build in this region
     */
    public boolean scan() {
//		long actionStart = System.currentTimeMillis();

        World world = chunk.getWorld();

        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;

        for (int xx = x; xx < x + 16; xx++)
            for (int zz = z; zz < z + 16; zz++)
                for (int y = Const.MIN_HEIGHT; y < world.getMaxHeight(); y++) {
                    Block currBlock = world.getBlockAt(xx, y, zz);

                    // Check if there is WorldGuard
                    if (worldGuard != null)
                        // Check if the block is in a WorldGuard's protected region and
                        // if the player is allowed to build in that region
                        if (Const.WORLDGUARD_HOOK && !worldGuard.canBuild(player, currBlock))
                            return false;

                    // Skip blocks made of ignored materials and Air
                    if (ignoreMaterial(currBlock.getType()) || currBlock.getType() == Material.AIR)
                        continue;

                    // Add the blocks to the set of blocks to remove on #mine()
                    blocksToRemove.add(currBlock);
                }

//		System.out.println("Scan process took " + (System.currentTimeMillis() - actionStart) + "ms");
        return true;
    }

    /**
     * Check whether blocks with that material should not be removed
     *
     * @param material The material to remove
     * @return true if ignored
     */
    private boolean ignoreMaterial(Material material) {
        return Const.IGNORED_MATERIALS.contains(material);
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
     * Replace all the blocks with AIR
     */
    public void mine() {
        blocksToRemove.forEach(block -> block.setType(Material.AIR));
    }

}
