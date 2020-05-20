package me.ezlikespie;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EnumGamemode;
import net.minecraft.server.v1_15_R1.MathHelper;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;

public class FakePlayer extends Reflections{

	public FakePlayer(String name, Location location){
		entityID = (int)Math.ceil(Math.random() * 1000) + 2000;
		gameprofile = new GameProfile(UUID.fromString("55769414-6849-4905-9673-eaadf64fb923"), name);
		this.location = location.clone();
		nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
		npc = new EntityPlayer(nmsServer, nmsWorld, gameprofile, new PlayerInteractManager(nmsWorld));
		npcPlayer = npc.getBukkitEntity().getPlayer();
		npc.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		npc.setHeadRotation(location.getYaw());
		npc.setNoGravity(true);
		npcPlayer.setAI(false);
	}

	private int entityID;
	private Location location;
	private GameProfile gameprofile;
	private MinecraftServer nmsServer;
	private WorldServer nmsWorld;
	private EntityPlayer npc;
	private Player npcPlayer;

	public void spawnNPC() {
		
		addToTablist();
		sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
		sendPacket(new PacketPlayOutEntityHeadRotation(npc, getFixRotation(npc.yaw)));
		
	}
	
	public void teleport(Location l) {
		npc.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(npc);
		sendPacket(packet);
	}
	
	public void addToTablist(){
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
		players.add(data);

		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet);
	}

	public void animation(int animation){
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation(npc, animation);
		sendPacket(packet);
	}

	public int getFixLocation(double pos){
		return (int)MathHelper.floor(pos * 32.0D);
	}

	public byte getFixRotation(float yawpitch){
		return (byte) (yawpitch * 256.0F / 360.0F);
	}
}
