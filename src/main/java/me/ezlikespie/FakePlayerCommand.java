package me.ezlikespie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class FakePlayerCommand implements CommandExecutor {
	private FakePlayer fakePlayer;
	private JavaPlugin plugin = Main.getInstance();
	
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(command.getName().equalsIgnoreCase("fakeplayer")) {
				p.sendMessage(ChatColor.YELLOW+"[NMSTests] "+ChatColor.GREEN+"Spawned NPC");
				fakePlayer = new FakePlayer("ezlikespie", p.getLocation());
				fakePlayer.spawnNPC();
				new BukkitRunnable() {
					private int count = 0;
					public void run() {
						if(count==0) {
							fakePlayer.animation(0);
							count=1;
						}
						else {
							fakePlayer.animation(3);
							count=0;
						}
					}
				}.runTaskTimer(plugin, 20, 10);
			}
		}
		return true;
	}
	
}
