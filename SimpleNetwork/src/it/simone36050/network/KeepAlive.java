package it.simone36050.network;

public class KeepAlive implements Packet {
	
	public static final KeepAlive INSTANCE = new KeepAlive();

	@Override
	public void read(ByteBuf buf) {
		// nothing
	}

	@Override
	public void write(ByteBuf buf) {
		// nothing
	}

	@Override
	public void handle(Handler handler) {
		// nothing
	}

}
