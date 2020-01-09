package it.simone36050.school.courier.protocol;

import it.simone36050.network.ByteBuf;
import it.simone36050.network.DefinedPacket;

public class PacketInfoRequest extends DefinedPacket<CourierHandler> {
	
	private long id;
	
	public PacketInfoRequest() {
	}
	
	public PacketInfoRequest(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void setid(long id) {
		this.id = id;
	}

	@Override
	public void read(ByteBuf buf) {
		id = buf.readLong();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeLong(id);
	}

	@Override
	public void handle0(CourierHandler handler) {
		handler.handlePacketInfoRequest(this);
	}

}
