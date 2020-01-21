package io.github.spaicygaming.chunkminer.hooks;

import com.massivecraft.factions.*;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Role;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FactionsUUIDIntegration extends PluginIntegration {

    private final List<String> allowedRoles;

    public FactionsUUIDIntegration(boolean integrationEnabledInConfig, List<String> allowedRoles) {
        super("Factions", integrationEnabledInConfig);
        this.allowedRoles = allowedRoles;
    }

    /**
     * {@inheritDoc} FactionsUUID
     */
    @Override
    public boolean canBuildHere(Player player, Location location) {
        // Factions configuration values
        Set<String> playersWhoBypassAllProtection, worldsNoWildernessProtection;
        boolean wildernessDenyBuild;

        // retrieve values from factions configuration according to its version
        try {
            MainConfig.Factions.Protection configFactions = FactionsPlugin.getInstance().conf().factions().protection();

            playersWhoBypassAllProtection = configFactions.getPlayersWhoBypassAllProtection();
            wildernessDenyBuild = configFactions.isWildernessDenyBuild();
            worldsNoWildernessProtection = configFactions.getWorldsNoWildernessProtection();

        } catch (NoClassDefFoundError pre05) {
            // Legacy support - pre 1.6.9.5-U0.5.0
            try {
                Class<?> conf = Class.forName("com.massivecraft.factions.Conf");

                //noinspection unchecked - safe to cast
                playersWhoBypassAllProtection = (Set<String>) conf.getField("playersWhoBypassAllProtection").get(null);
                wildernessDenyBuild = conf.getField("wildernessDenyBuild").getBoolean(null);
                //noinspection unchecked - safe to cast
                worldsNoWildernessProtection = (Set<String>) conf.getField("worldsNoWildernessProtection").get(null);

            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                notifyUnsupportedVersion(e, player);
                return false;
            }
        }

        // The FPlayer who placed the miner
        FPlayer factionPlayer = FPlayers.getInstance().getByPlayer(player);

        // The Faction that owns the chunk at miner location
        Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(location));

        // Return true if the player can bypass factions restrictions
        if (playersWhoBypassAllProtection.contains(factionPlayer.getName()) || factionPlayer.isAdminBypassing()) {
            return true;
        }

        // Return true if it's wilderness and it is allowed to build there
        if (otherFaction.isWilderness() && (!wildernessDenyBuild || worldsNoWildernessProtection.contains(Objects.requireNonNull(location.getWorld()).getName())))
            return true;

        // Own faction claim
        if (factionPlayer.getFactionId().equals(otherFaction.getId())) {
            // Player's role in his faction
            String roleName;
            try {
                Role role = factionPlayer.getRole();
                roleName = role.name();
            } catch (NoSuchMethodError error) {
                // Legacy support - in old Factions versions the class Role is in the package "com.massivecraft.factions.struct"
                try {
                    Object role = factionPlayer.getClass().getMethod("getRole").invoke(factionPlayer);
                    roleName = (String) role.getClass().getMethod("name").invoke(role);

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    notifyUnsupportedVersion(e, player);
                    return false;
                }
            }
            return allowedRoles.contains(roleName);
        }

        // Not own claim
        return false;
    }

    private void notifyUnsupportedVersion(Exception e, Player player) {
        e.printStackTrace();
        ChatUtil.alert("Unsupported Factions version, can't get values from its configuration file");
        player.sendMessage(ChatUtil.c("problemOccurred"));
    }


}
