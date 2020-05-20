package me.ezlikespie;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FakePlayer extends Reflections{

	public FakePlayer(String name,Location location){
		entityID = (int)Math.ceil(Math.random() * 1000) + 2000;
		gameprofile = new GameProfile(UUID.randomUUID(), name);
		this.location = location.clone();
		nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
		npc = new EntityPlayer(nmsServer, nmsWorld, gameprofile, new PlayerInteractManager(nmsWorld));
		npcPlayer = npc.getBukkitEntity().getPlayer();
	}

	private int entityID;
	private Location location;
	private GameProfile gameprofile;
	private MinecraftServer nmsServer;
	private WorldServer nmsWorld;
	private EntityPlayer npc;
	private Player npcPlayer;

	public void spawnNPC(Location l) {
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();

		setValue(packet, "a", entityID);
		setValue(packet, "b", gameprofile.getId());
		setValue(packet, "c", getFixLocation(location.getX()));
		setValue(packet, "d", getFixLocation(location.getY()));
		setValue(packet, "e", getFixLocation(location.getZ()));
		setValue(packet, "f", getFixRotation(location.getYaw()));
		setValue(packet, "g", getFixRotation(location.getPitch()));
		addToTablist();
		sendPacket(packet);
		headRotation(location.getYaw(), location.getPitch());
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

	public void headRotation(float yaw,float pitch){
		PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw),getFixRotation(pitch) , true);
		PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
		setValue(packetHead, "a", entityID);
		setValue(packetHead, "b", getFixRotation(yaw));


		sendPacket(packet);
		sendPacket(packetHead);
	}

	public int getFixLocation(double pos){
		return (int)MathHelper.floor(pos * 32.0D);
	}

	public byte getFixRotation(float yawpitch){
		return (byte) ((int) (yawpitch * 256.0F / 360.0F));
	}
}
