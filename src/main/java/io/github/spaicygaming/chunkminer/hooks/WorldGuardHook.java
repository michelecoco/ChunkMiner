package io.github.spaicygaming.chunkminer.hooks;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
            Vector position;
            try {
                position = BukkitUtil.toVector(location);
            } catch (NoClassDefFoundError | NoSuchMethodError err) {
                position = new Vector(location.getX(), location.getY(), location.getZ());
            }

            com.sk89q.worldedit.util.Location worldEditLocation = new com.sk89q.worldedit.util.Location(
                    worldGuard.getWorldByName(location.getWorld().getName()), position, location.getYaw(), location.getPitch());

            return/* worldGuard.getSessionManager().hasBypass(player, location.getWorld())
                    ||*/ worldGuard.getRegionContainer().createQuery().testBuild(worldEditLocation, worldGuardPlugin.wrapPlayer(player));
        }
    }

}
