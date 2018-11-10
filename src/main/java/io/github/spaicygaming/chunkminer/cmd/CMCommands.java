package io.github.spaicygaming.chunkminer.cmd;

import io.github.spaicygaming.chunkminer.ChunkMiner;
import io.github.spaicygaming.chunkminer.Permission;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CMCommands implements CommandExecutor {

    /**
     * Main class instance
     */
    private ChunkMiner main;
    private List<String> helpMenu;

    public CMCommands(ChunkMiner main) {
        this.main = main;
        this.helpMenu = ChatUtil.color(main.getConfig().getStringList("Messages.HelpMenu"));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        // Args Length 2
        if (args.length == 2) {
            // Get command
            if (args[0].equalsIgnoreCase("get")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatUtil.c("onlyInGame"));
                    return false;
                }
                Player player = (Player) sender;

                if (!Permission.CMD_GET.has(player)) {
                    player.sendMessage(ChatUtil.c("noCmdPerms"));
                    return false;
                }

                // Check the amount is a valid number
                if (invalidInt(args[1])) {
                    player.sendMessage(ChatUtil.c("invalidAmount").replace("{input}", args[1]));
                    return false;
                }
                int amount = Integer.valueOf(args[1]);

                // Give chunk miner item(s)
                main.getMinerItem().give(player, amount);

                // Send message
                player.sendMessage(ChatUtil.c("itemReceived").replace("{amount}", String.valueOf(amount)));
            }
            // Wrong args
            else {
                printHelpMenu(sender);
            }
        }

        // Args Length 3
        else if (args.length == 3) {
            // Give command
            if (args[0].equalsIgnoreCase("give")) {
                // Check permission
                if (!Permission.CMD_GIVE.has(sender)) {
                    sender.sendMessage(ChatUtil.c("noCmdPerms"));
                    return false;
                }

                // Target player data
                String targetName = args[1];
                Player target = main.getServer().getPlayer(targetName);

                // Return if the target player isn't online
                if (target == null) {
                    sender.sendMessage(ChatUtil.c("targetOffline").replace("{target}", targetName));
                    return false;
                }

                // Check the amount is a valid number
                if (invalidInt(args[2])) {
                    sender.sendMessage(ChatUtil.c("invalidAmount").replace("{input}", args[2]));
                    return false;
                }
                int amount = Integer.valueOf(args[2]);

                // Return if target's inventory is full
                if (target.getInventory().firstEmpty() == -1) {
                    sender.sendMessage(ChatUtil.c("targetFullInventory").replace("{target}", targetName));
                    return false;
                }

                // Give chunk miner item(s)
                main.getMinerItem().give(target, amount);

                // Send message to command executor (giver)
                sender.sendMessage(ChatUtil.c("itemGived").replace("{target}", targetName)
                        .replace("{amount}", String.valueOf(amount)));

                // How to name the sender
                String senderName = sender instanceof ConsoleCommandSender ? ChatUtil.getConsoleName() : sender.getName();

                // Send message to the target
                target.sendMessage(ChatUtil.c("itemReceivedOther").replace("{giver}", senderName)
                        .replace("{amount}", String.valueOf(amount)));
            }
            // Wrong arg
            else {
                printHelpMenu(sender);
            }
        }

        // Invalid args length
        else {
            printHelpMenu(sender);
        }

        return true;
    }

    /**
     * Send the commands help menu
     *
     * @param sender The CommandSender who send the menu to
     */
    private void printHelpMenu(CommandSender sender) {
        helpMenu.forEach(sender::sendMessage);
    }

    /**
     * Check whether the string is an integer greater than 0
     *
     * @param userInput The String to check
     * @return true if it isn't
     */
    private boolean invalidInt(String userInput) {
        int input;
        try {
            input = Integer.valueOf(userInput);
        } catch (NumberFormatException e) {
            return true;
        }
        return input <= 0;
    }

}
