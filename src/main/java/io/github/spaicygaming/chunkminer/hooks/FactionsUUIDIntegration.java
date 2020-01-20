package io.github.spaicygaming.chunkminer.hooks;

import com.massivecraft.factions.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionsUUIDIntegration extends PluginIntegration {

    private List<String> allowedRoles;

    public FactionsUUIDIntegration(boolean integrationEnabledInConfig, List<String> allowedRoles) {
        super("Factions", integrationEnabledInConfig);
        this.allowedRoles = allowedRoles;
    }

    /**
     * {@inheritDoc} FactionsUUID
     */
    @Override
    public boolean canBuildHere(Player player, Location location) {
        // The FPlayer who placed the miner
        FPlayer factionPlayer = FPlayers.getInstance().getByPlayer(player);

        // Player can bypass factions restrictions
        if (Conf.playersWhoBypassAllProtection.contains(factionPlayer.getName()) || factionPlayer.isAdminBypassing())
            return true;

        // The Faction that owns the chunk at miner location
        Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(location));

        // Return true if it's wilderness
        if (otherFaction.isWilderness() && (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())))
            return true;

        // Own claim
        if (factionPlayer.getFactionId().equals(otherFaction.getId())) {
            return allowedRoles.contains(factionPlayer.getRole().name());
        }

        // Not own claim
        return false;
    }


}
