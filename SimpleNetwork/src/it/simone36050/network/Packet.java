package it.simone36050.network;

public interface Packet {
	
	public void read(ByteBuf buf);
	public void write(ByteBuf buf);
	public void handle(Handler handler);
	
}
