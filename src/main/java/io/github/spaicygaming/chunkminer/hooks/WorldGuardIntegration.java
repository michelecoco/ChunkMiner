package io.github.spaicygaming.chunkminer.hooks;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WorldGuardIntegration extends PluginIntegration {

    private WorldGuardPlugin worldGuardPlugin;
    private WorldGuardPlatform worldGuard;

    /**
     * Whether the running WorldGuard version is previous to 7.0
     */
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
     * {@inheritDoc} WorldGuard
     */
    @Override
    public boolean canBuildHere(Player player, Location location) {
        if (pre7) {
            try {
                @SuppressWarnings("JavaReflectionMemberAccess") // backward compatibility
                        Method canBuildMethod = WorldGuardPlugin.class.getMethod("canBuild", Player.class, Location.class);
                return (boolean) canBuildMethod.invoke(worldGuardPlugin, player, location);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // should never happen
                e.printStackTrace();
                return false; // todo remove the player from the set
            }
        } else {
            @SuppressWarnings("ConstantConditions")
            Extent worldEditWorld = getWorldEditWorldByName(location.getWorld().getName());
            com.sk89q.worldedit.util.Location worldEditLocation
                    = new com.sk89q.worldedit.util.Location(worldEditWorld, location.getX(), location.getY(), location.getZ());
            return worldGuard.getRegionContainer().createQuery().testBuild(worldEditLocation, worldGuardPlugin.wrapPlayer(player));
        }
    }

    /**
     * Gets WorldEdit World from its name
     *
     * @param worldName the name of the world
     * @return a new {@link BukkitWorld} instance
     */
    private com.sk89q.worldedit.world.World getWorldEditWorldByName(String worldName) {
        org.bukkit.World bukkitWorld = Bukkit.getServer().getWorld(worldName);
        Preconditions.checkNotNull(bukkitWorld);
        return new BukkitWorld(bukkitWorld);
    }
}
