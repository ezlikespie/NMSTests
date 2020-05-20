package me.ezlikespie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FakePlayerCommand implements CommandExecutor {
	private FakePlayer fakePlayer;
	private JavaPlugin plugin = Main.getInstance();
	
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(command.getName().equalsIgnoreCase("fakeplayer")) {
				fakePlayer = new FakePlayer("Bob", p.getLocation());
				fakePlayer.spawnNPC();
				new BukkitRunnable() {
					public void run() {
					fakePlayer.animation(0);
				}
				}.runTaskLater(plugin, 20L);
			}
		}
		return true;
	}
	
}
