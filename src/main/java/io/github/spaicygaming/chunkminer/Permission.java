package io.github.spaicygaming.chunkminer;

import org.bukkit.permissions.Permissible;

public enum Permission {

    // Commands
    CMD_GET,
    CMD_GIVE,
    // Listener
    PLACE,
    // Bypass
    BYPASS_WORLD,
    BYPASS_GAMEMODE,
    // Update Checker
    NOTIFY_ONUSE,
    NOTIFY_UPDATES;

    private String permissionValue;

    Permission() {
        permissionValue = this.toString();
    }

    /**
     * Checks whether the user has the specified permission
     *
     * @param user The use whose permissions check
     * @return true if he has the permission
     */
    public boolean has(Permissible user) {
        return user.hasPermission(permissionValue);
    }

    /**
     * @return the permission in the form of "chunkminer.LOWERCASE_PERMISSION"
     */
    @Override
    public String toString() {
        return "chunkminer." + this.name().toLowerCase().replace("_", ".");
    }

}
