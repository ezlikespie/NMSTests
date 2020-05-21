package me.ezlikespie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class WatchdogCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(command.getName().equalsIgnoreCase("watchdog")) {
				p.sendMessage(ChatColor.YELLOW+"[NMSTests] "+ChatColor.GREEN+"Spawned Watchdog");
				new Watchdog(p);
			}
		}
		return true;
	}
	
}
