package me.ezlikespie;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static JavaPlugin instance;
	
	@Override
	public void onEnable() {
		instance = this;
		getCommand("fakeplayer").setExecutor(new FakePlayerCommand());
		getCommand("watchdog").setExecutor(new WatchdogCommand());
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static JavaPlugin getInstance() {
		return instance;
	}
	
}
