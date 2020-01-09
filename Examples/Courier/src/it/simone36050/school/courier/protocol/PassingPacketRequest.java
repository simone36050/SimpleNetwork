package it.simone36050.school.courier.protocol;

import it.simone36050.network.ByteBuf;
import it.simone36050.network.DefinedPacket;

public class PassingPacketRequest extends DefinedPacket<CourierHandler> {
	
	private long id;
	private int center;
	private boolean last;
	
	public PassingPacketRequest() {
	}
	
	public PassingPacketRequest(long id, int center) {
		this(id, center, false);
	}
	
	public PassingPacketRequest(long id, int center, boolean last) {
		this.id = id;
		this.center = center;
		this.last = last;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCenter() {
		return center;
	}
	
	public void setCenter(int center) {
		this.center = center;
	}
	
	public boolean isLast() {
		return last;
	}
	
	public void setLast(boolean last) {
		this.last = last;
	}

	@Override
	public void read(ByteBuf buf) {
		id = buf.readLong();
		center = buf.readInt();
		last = buf.readBool();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeLong(id);
		buf.writeInt(center);
		buf.writeBool(last);
	}

	@Override
	public void handle0(CourierHandler handler) {
		handler.handlePassingPacketRequest(this);
	}

}
