package it.simone36050.school.courier.protocol;

import java.util.List;

import it.simone36050.network.ByteBuf;
import it.simone36050.network.DefinedPacket;
import it.simone36050.school.courier.CourierPacket;

public class PacketInfoResponse extends DefinedPacket<CourierHandler> {
	
	private boolean founded = false;
	private CourierPacket packet;
	
	public PacketInfoResponse() {
	}
	
	public PacketInfoResponse(boolean founded, CourierPacket packet) {
		this.founded = founded;
		this.packet = packet;
	}
	
	public CourierPacket getPacket() {
		return packet;
	}
	
	public void setPacket(CourierPacket packet) {
		this.packet = packet;
	}
	
	public boolean isFounded() {
		return founded;
	}
	
	public void setFounded(boolean founded) {
		this.founded = founded;
	}

	@Override
	public void read(ByteBuf buf) {
		founded = buf.readBool();
		
		if(founded) {
			packet = new CourierPacket();
			
			packet.setId(buf.readLong());
			
			int size = buf.readVarInt();
			for(int i = 0; i < size; i++)
				packet.addNode(buf.readInt());
	
			packet.setEnded(buf.readBool());
		}
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeBool(founded);
		
		if(founded) {
			buf.writeLong(packet.getId());
			
			List<Integer> nodes = packet.getNodes();
			buf.writeVarInt(nodes.size());
			nodes.forEach(buf::writeInt);
			
			buf.writeBool(packet.isEnded());
		}
	}

	@Override
	public void handle0(CourierHandler handler) {
		handler.handlePacketInfoResponse(this);
	}

}
