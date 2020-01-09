package it.simone36050.school.courier.protocol;

import it.simone36050.network.Protocol;

public class CourierProtocol {

	public static final Protocol PROTOCOL;
	
	static {
		PROTOCOL = new Protocol();
		
		// client -> server
		PROTOCOL.new StandardEntry(0x01, NewPacketRequest.class, () -> new NewPacketRequest());
		PROTOCOL.new StandardEntry(0x02, PassingPacketRequest.class, () -> new PassingPacketRequest());
		PROTOCOL.new StandardEntry(0x03, PacketInfoRequest.class, () -> new PacketInfoRequest());
		
		// server -> client
		PROTOCOL.new StandardEntry(0x11, NewPacketResponse.class, () -> new NewPacketResponse());
		PROTOCOL.new StandardEntry(0x12, PassingPacketResponse.class, () -> new PassingPacketResponse());
		PROTOCOL.new StandardEntry(0x13, PacketInfoResponse.class, () -> new PacketInfoResponse());
	}
	
}
