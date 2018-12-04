package io.github.spaicygaming.chunkminer.hooks;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WorldGuardHook {

    private WorldGuardPlugin worldGuardPlugin;
    private WorldGuardPlatform worldGuard;
    private boolean integrationEnabled, installed, pre7;

    public WorldGuardHook(boolean integrationEnabled) {
        this.integrationEnabled = integrationEnabled;

        worldGuardPlugin = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        this.installed = worldGuardPlugin != null;

        if (installed) {
            try {
                worldGuard = WorldGuard.getInstance().getPlatform();
            } catch (NoClassDefFoundError err) {
                // This means the spigot server is running a WorldGuard version previous to 7.0
                pre7 = true;
            }
        }

        if (integrationEnabled)
            if (installed)
                Bukkit.getServer().getLogger().info("Hooked into WorldGard");
            else
                Bukkit.getServer().getLogger().info("Can't hook into WorldGuard, plugin not found");
    }

    /**
     * Check whether to perform operations involving WorldGuard
     *
     * @return true if WG integration is enabled and WG is running
     */
    public boolean performChecks() {
        return integrationEnabled && installed;
    }

    /**
     * @param world The Bukkit world
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
     * Check whether a player can build at a specific location according to WorldGuard flags
     *
     * @param player   The player
     * @param location The location
     * @return true if he can build
     */
    public boolean canBuild(Player player, Location location) {
        if (pre7) {
            return worldGuardPlugin.canBuild(player, location);
        } else {
            Extent worldEditWorld = worldGuard.getWorldByName(location.getWorld().getName());
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
