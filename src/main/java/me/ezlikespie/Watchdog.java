package me.ezlikespie;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

public class Watchdog {

	private int id;
	private GameProfile gameProfile;
	private EntityPlayer entity;
	private Player entityPlayer;
	private Player player;
	private PlayerConnection connection;
	private double rotation;
	private final double FLY_RADIUS = 3;
	private final double SPINS_PER_SECOND = 1.5;
	
	public Watchdog(Player p) {
		player = p;
		Location pLoc = p.getLocation();
		rotation = yawToRad(pLoc.getYaw());
		connection = ((CraftPlayer)p).getHandle().playerConnection;
		id = (int)(Math.random()*Math.pow(2, 32));
		gameProfile = new GameProfile(p.getUniqueId(), ChatColor.RED+UUID.randomUUID().toString());
		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) pLoc.getWorld()).getHandle();
		entity = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
		entityPlayer = entity.getBukkitEntity().getPlayer();
		Location wLoc = findLocation();
		float rot = getRotation();
		entity.setPositionRotation(wLoc.getX(), wLoc.getY(), wLoc.getZ(), rot, 0);
		entity.setHeadRotation(rot);
		entity.setNoGravity(true);
		entityPlayer.setCustomName("");
		entityPlayer.setCustomNameVisible(true);
		entityPlayer.setAI(false);
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entity));
		connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, (byte)(rot*256F/360F)));
		new BukkitRunnable() {
			public void run() {
				if(connection.isDisconnected()) {
					cancel();
					return;
				}
				Location newLoc = findLocation();
				float rot = getRotation();
				entity.setPositionRotation(newLoc.getX(), newLoc.getY(), newLoc.getZ(), rot, 0);
				entity.setHeadRotation(rot);
				connection.sendPacket(new PacketPlayOutEntityTeleport(entity));
				connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, (byte)(rot*256F/360F)));
			}
		}.runTaskTimer(Main.getInstance(), 10, 1);
	}
	
	private float getRotation() {
		return (radToYaw(rotation)+90)%360-180;
	}
	
	private Location findLocation() {
		Location pLoc = player.getLocation();
		Location wLoc = new Location(pLoc.getWorld(), FLY_RADIUS*Math.cos(rotation)+pLoc.getX(), pLoc.getY(), FLY_RADIUS*Math.sin(rotation)+pLoc.getZ());
		rotation += SPINS_PER_SECOND*18*Math.PI/180;
		if(rotation>=2*Math.PI) {
			rotation%=Math.PI*2;
		}
		return wLoc;
	}
	
	private double yawToRad(float yaw) {
		return (yaw+180)*Math.PI/180;
	}
	
	private float radToYaw(double rad) {
		return (float)(rad%(Math.PI*2)*180/Math.PI-180);
	}
	
}
