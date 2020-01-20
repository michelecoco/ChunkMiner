package io.github.spaicygaming.chunkminer.hooks;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WorldGuardIntegration extends PluginIntegration { // todo support latest worldguard versions

    private WorldGuardPlugin worldGuardPlugin;
    private WorldGuardPlatform worldGuard;
    private boolean pre7;

    public WorldGuardIntegration(boolean integrationEnabledInConfig) {
        super("WorldGuard", integrationEnabledInConfig);

        try {
            worldGuardPlugin = (WorldGuardPlugin) super.getPlugin();
        } catch (ClassCastException e) {
            throw new AssertionError("The plugin WorldGuard isn't instance of com.sk89q.worldguard.bukkit.WorldGuardPlugin");
        }

        if (isInstalled()) {
            try {
                worldGuard = WorldGuard.getInstance().getPlatform();
            } catch (NoClassDefFoundError err) {
                // This means the spigot server is running a WorldGuard version previous to 7.0
                pre7 = true;
            }
        }
    }

    /**
     * Gets the given world's region manager
     *
     * @param world the Bukkit world
     * @return world's region manager
     */
    @SuppressWarnings("unused") // will be used in a future version
    public RegionManager getRegionManager(org.bukkit.World world) {
        if (pre7) {
            return worldGuardPlugin.getRegionManager(world);
        } else {
            return worldGuard.getRegionContainer().get(worldGuard.getWorldByName(world.getName()));
        }
    }

    /**
     * {@inheritDoc} WorldGuard
     */
    @Override
    public boolean canBuildHere(Player player, Location location) {
        if (pre7) {
            return worldGuardPlugin.canBuild(player, location);
        } else {
            @SuppressWarnings("ConstantConditions") Extent worldEditWorld = worldGuard.getWorldByName(location.getWorld().getName());
            com.sk89q.worldedit.util.Location worldEditLocation;

            try {
                Vector position;
                try {
                    position = BukkitUtil.toVector(location);
                } catch (NoClassDefFoundError | NoSuchMethodError err) {
                    position = new Vector(location.getX(), location.getY(), location.getZ());
                }

                worldEditLocation = new com.sk89q.worldedit.util.Location(worldEditWorld, position);
            }
            // Support WorldEdit 7.0.0-beta-02+ versions
            catch (NoClassDefFoundError post7beta02Version) {
                Class<com.sk89q.worldedit.util.Location> weLocationClass = com.sk89q.worldedit.util.Location.class;
                try {
                    //noinspection JavaReflectionMemberAccess
                    Constructor<com.sk89q.worldedit.util.Location> constructor = weLocationClass.getConstructor(Extent.class, Vector3.class);

                    worldEditLocation = constructor.newInstance(worldEditWorld, Vector3.at(location.getX(), location.getY(), location.getZ()));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return worldGuard.getRegionContainer().createQuery().testBuild(worldEditLocation, worldGuardPlugin.wrapPlayer(player));
        }
    }

}
