package me.joeleoli.nucleus.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;

public class Packet extends PacketContainer {

	public Packet(PacketType packetType) {
		super(packetType);
	}

	public void send(Player player) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, this);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
