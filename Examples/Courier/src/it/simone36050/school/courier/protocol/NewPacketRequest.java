package it.simone36050.school.courier.protocol;

import it.simone36050.network.ByteBuf;
import it.simone36050.network.DefinedPacket;

public class NewPacketRequest extends DefinedPacket<CourierHandler> {

	private int center;
	
	public NewPacketRequest() {
	}
	
	public NewPacketRequest(int center) {
		this.center = center;
	}
	
	public int getCenter() {
		return center;
	}
	
	public void setCenter(int center) {
		this.center = center;
	}
	
	@Override
	public void read(ByteBuf buf) {
		center = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(center);
	}

	@Override
	public void handle0(CourierHandler handler) {
		handler.handleNewPacketRequest(this);
	}

}
