package io.github.spaicygaming.chunkminer.miner;

import io.github.spaicygaming.chunkminer.ChunkMiner;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class MinersManager {

    /**
     * Materials of blocks that mustn't be removed by miners
     */
    private Set<Material> ignoredMaterials;

    /**
     * The lowest y height the miner starts mine from (1 is the minimum allowed value)
     */
    private int minHeight;

    /**
     * The maximum amount of ChunkMiners a player can place at once
     */
    private int maxMinersAmountAtOnce;

    /**
     * Chunks currently processed associated to the players who placed the miners.
     * (scanning or mining operation in progress)
     */
    private Map<UUID, Set<Chunk>> activeOperations = new HashMap<>();

    public MinersManager(ChunkMiner main) {
        ConfigurationSection config = main.getConfig().getConfigurationSection("MainSettings");

        this.ignoredMaterials = config.getStringList("ignoreMaterials").stream().map(Material::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));

        this.minHeight = Math.max(1, config.getInt("minHeight"));
        this.maxMinersAmountAtOnce = config.getInt("maxAmount");
    }

    int getMinHeight() {
        return minHeight;
    }

    public int getMaxMinersAmountAtOnce() {
        return maxMinersAmountAtOnce;
    }

    /**
     * @return the {@link Map} containing UUIDs of players who have an active miner
     * and chunks involved in active scanning and/or mining operations
     */
    Map<UUID, Set<Chunk>> getActiveOperations() {
        return activeOperations;
    }

    /**
     * Gets the active mining operations associated with the player
     *
     * @param playerUUID player's UUID
     * @return an immutable empty set if there isn't any active operation associated with the player
     */
    public Set<Chunk> getActiveOperations(UUID playerUUID) {
        return Optional.ofNullable(activeOperations.get(playerUUID)).orElse(Collections.emptySet());
    }

    /**
     * Checks whether there is a running operation in the given chunk
     *
     * @param chunk the chunk to check
     * @return true if the chunk is involved in an operation
     */
    public boolean isCurrentlyProcessed(Chunk chunk) {
        return activeOperations.values().stream().flatMap(Set::stream).anyMatch(chunk::equals);
    }

    /**
     * Checks whether blocks with the given material must be removed by miners
     *
     * @param material the material whose presence check
     * @return true if it must't be removed
     */
    boolean isMaterialIgnored(Material material) {
        return ignoredMaterials.contains(material);
    }

}
