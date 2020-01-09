package it.simone36050.school.courier.server;

import java.util.Map;

import it.simone36050.school.courier.CourierPacket;
import it.simone36050.school.courier.protocol.CourierHandler;
import it.simone36050.school.courier.protocol.NewPacketRequest;
import it.simone36050.school.courier.protocol.NewPacketResponse;
import it.simone36050.school.courier.protocol.PacketInfoRequest;
import it.simone36050.school.courier.protocol.PacketInfoResponse;
import it.simone36050.school.courier.protocol.PassingPacketRequest;
import it.simone36050.school.courier.protocol.PassingPacketResponse;

public class ServerHandler extends CourierHandler {

	private Map<Long, CourierPacket> storage;
	
	public ServerHandler(Map<Long, CourierPacket> storage) {
		this.storage = storage;
	}
	
	@Override
	public void handleNewPacketRequest(NewPacketRequest packet) {
		// create new packet
		CourierPacket pack = CourierPacket.createPacket();
		pack.addNode(packet.getCenter());
		
		// add to storage
		storage.put(pack.getId(), pack);
		
		// reply
		connection.sendAndFlush(new NewPacketResponse(pack.getId()));
	}
	
	@Override
	public void handlePassingPacketRequest(PassingPacketRequest packet) {
		// update packet
		CourierPacket pack = storage.get(packet.getId());
		pack.addNode(packet.getCenter());
		if(packet.isLast())
			pack.setEnded(true);
		
		// reply
		connection.sendAndFlush(new PassingPacketResponse(packet.getId()));
	}
	
	@Override
	public void handlePacketInfoRequest(PacketInfoRequest packet) {
		// get packet
		CourierPacket pack = storage.get(packet.getId());
		
		// reply
		connection.sendAndFlush(new PacketInfoResponse(pack != null, pack));
	}
	
}
