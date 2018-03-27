package io.github.spaicygaming.chunkminer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import io.github.spaicygaming.chunkminer.util.Const;

public class Miner {
	
	/**
	 * The chunk the player placed the miner
	 */
	private Chunk chunk;
	
	public Miner(Chunk chunk) {
		this.chunk = chunk;
	}
	
	/**
	 * The blocks inside the chunk to replace with AIR.
	 */
	private List<Block> blocksToRemove = new ArrayList<Block>();
	
	/**
	 * Scan the blocks inside the chunk and add
	 * them in {@link #blocksToRemove}
	 * @return (this will be used later)
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
					
					// Se il blocco è aria non lo aggiunge nella lista dei blocchi da rimuovere
					// Skip blocks made of ignored materials and Air
					if (ignoreMaterial(currBlock.getType()) || currBlock.getType() == Material.AIR)
						continue;
						
					/*
					 * TODO: controllare se il blocco è in una region protetta
					 * e in caso return false
					 */
					
					// Add the blocks to the list of blocks to remove on #mine()
					blocksToRemove.add(currBlock);
				}
		
//		System.out.println("Scan process took " + (System.currentTimeMillis() - actionStart) + "ms");
		return true;
	}
	
	/**
	 * Check whether blocks with that material should not be removed
	 * @param material
	 * @return
	 */
	private boolean ignoreMaterial(Material material) {
		return Const.IGNORED_MATERIALS.contains(material);
	}
	
	/**
	 * Returns the amount of blocks in the Chunk that can be removed.
	 * {@link #chunk} must be called before.
	 * @return
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
