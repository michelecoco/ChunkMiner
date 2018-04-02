package io.github.spaicygaming.chunkminer.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.spaicygaming.chunkminer.ChunkMiner;
import io.github.spaicygaming.chunkminer.util.ChatUtil;
import io.github.spaicygaming.chunkminer.util.Const;

public class CMCommands implements CommandExecutor {

	private ChunkMiner main = ChunkMiner.getInstance();
	
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		// Length 2
		if (args.length == 2) {
			// Get
			if (args[0].equalsIgnoreCase("get")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatUtil.c("onlyInGame"));
					return false;
				}
				Player player = (Player) sender;
				
				if (!player.hasPermission(Const.PERM_GET)) {
					player.sendMessage(ChatUtil.c("noCmdPerms"));
					return false;
				}
				
				// Controlla che l'amount sia un numero valido
				if (!validInt(args[1])) {
					player.sendMessage(ChatUtil.c("invalidAmount").replace("{input}", args[1]));
					return false;
				}
				int amount = Integer.valueOf(args[1]);
				
				// Dà il chunk miner item
				main.getMinerItem().give(player, amount);
				
				// Message
				player.sendMessage(ChatUtil.c("itemReceived").replace("{amount}", String.valueOf(amount)));
				
				return true;
			}
			// Wrong args
			else {
				printHelpMenu(sender);
			}
			
			return false;
		}
		
		// Length 3
		else if (args.length == 3) {
			// Give
			if (args[0].equalsIgnoreCase("give")) {
				// Check permission
				if (!sender.hasPermission(Const.PERM_GIVE)) {
					sender.sendMessage(ChatUtil.c("noCmdPerms"));
					return false;
				}
			
				String targetName = args[1];
				// Controlla se il target è offline
				Player target = main.getServer().getPlayer(targetName);
				if (target == null) {
					sender.sendMessage(ChatUtil.c("targetOffline").replace("{target}", targetName));
					return false;
				}
				
				// Controlla che l'amount sia un numero valido
				if (!validInt(args[2])) {
					sender.sendMessage(ChatUtil.c("invalidAmount").replace("{input}", args[2]));
					return false;
				}
				int amount = Integer.valueOf(args[2]);
				
				// Controlla se c'è spazio nell'inventario
				if (target.getInventory().firstEmpty() == -1) {
					sender.sendMessage(ChatUtil.c("targetFullInventory").replace("{target}", targetName));
					return false;
				}
				
				// Dà il chnk miner
				main.getMinerItem().give(target, amount);
				
				// Giver Message
				sender.sendMessage(ChatUtil.c("itemGived").replace("{target}", targetName)
						.replace("{amount}", String.valueOf(amount)));
				
				// Sender Name
				String senderName = sender.getName();
				if (sender instanceof ConsoleCommandSender)
					senderName = Const.CONSOLE_NAME;
				
				// Target message
				target.sendMessage(ChatUtil.c("itemReceivedOther").replace("{giver}", senderName)
						.replace("{amount}", String.valueOf(amount)));
				
				return true;
			} else {
				printHelpMenu(sender);
			}
			
		}
		
		// Invalid Length
		else {
			printHelpMenu(sender);
		}
		
		return false;
	}
	
	/**
	 * Send the commands help menu
	 * @param sender The CommandSender who send to the menu
	 */
	private void printHelpMenu(CommandSender sender) {
		ChatUtil.color(main.getConfig().getStringList("Messages.HelpMenu")).forEach(sender::sendMessage);
	}
	
	/**
	 * Check whether the string is an integer greater than 0 
	 * @param userInput The String to check
	 * @return true if it is
	 */
	private boolean validInt(String userInput) {
		int input;
		try {
			input = Integer.valueOf(userInput);
		} catch (NumberFormatException e) {
			return false;
		}
		return input > 0;
	}
	
	
}
