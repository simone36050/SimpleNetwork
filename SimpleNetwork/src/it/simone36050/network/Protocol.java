package it.simone36050.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Protocol {
	
	private Map<Short, ProtocolEntry> fromId = new HashMap<>();
	private Map<Class<? extends Packet>, ProtocolEntry> fromClass = new HashMap<>();
	
	public Protocol() {
		_addEntry(new ProtocolEntry() {
			@Override
			public Class<? extends Packet> getPacketClass() {
				return KeepAlive.class;
			}
			@Override
			public short getId() {
				return (short)0x00;
			}
			@Override
			public Supplier<? extends Packet> getGenerator() {
				return () -> KeepAlive.INSTANCE;
			}
		});
	}
	
	private void _addEntry(ProtocolEntry entry) {
		fromId.put(entry.getId(), entry);
		fromClass.put(entry.getPacketClass(), entry);
	}
	
	public void addEntry(ProtocolEntry entry) {
		if(entry.getId() == 0x00)
			throw new ProtocolException("Register 0x00 packet's id is not allowed");
		_addEntry(entry);
	}
	
	public Packet dataToPacket(byte[] data) {
		// create packet class
		short id = NetworkUtils.getShort(data, 0);
		ProtocolEntry entry = fromId.get(id);
		if(entry == null)
			throw new UnrecognizedPacket("Packet id " + id + " do not exist");
		Packet packet = (Packet)entry.getGenerator().get();
		
		// read data from ByteBuf
		ByteBuf buf = ByteBuf.fromByteArray(data, 2);
		packet.read(buf);
		
		return packet;
	}
	
	public byte[] packetToData(Packet packet) {
		byte[] data;
		
		// write data into ByteBuf
		ByteBuf buf = new ByteBuf();
		packet.write(buf);
		
		// create byte array
		data = new byte[buf.size() + 2];
		
		// get id of the packet
		ProtocolEntry entry = fromClass.get(packet.getClass());
		NetworkUtils.setShort(entry.getId(), data, 0);
		
		// copy data from ByteBuf to byte array
		buf.toByteArray(data, 2);
		
		return data;
	}
	
	public static interface ProtocolEntry {
		public short getId();
		public Class<? extends Packet> getPacketClass();
		public Supplier<? extends Packet> getGenerator();
	}
	
	public class StandardEntry implements ProtocolEntry {

		public final short id;
		public final Class<? extends Packet> packet;
		public final Supplier<? extends Packet> generator;
		
		public StandardEntry(int id, Class<? extends Packet> packet, Supplier<? extends Packet> generator) {
			this.id = (short)id;
			this.packet = packet;
			this.generator = generator;
			addEntry(this);
		}
		
		@Override
		public short getId() {
			return id;
		}

		@Override
		public Class<? extends Packet> getPacketClass() {
			return packet;
		}

		@Override
		public Supplier<? extends Packet> getGenerator() {
			return generator;
		}
		
	}
	
	public static class ProtocolException extends NetworkException {

		private static final long serialVersionUID = -5958104669664755649L;
		public ProtocolException() { super(); }
		public ProtocolException(String message) { super(message); }
		public ProtocolException(String message, Throwable cause) { super(message, cause); }
		public ProtocolException(Throwable cause) { super(cause); }
		
	}
	
	public static class UnrecognizedPacket extends ProtocolException {

		private static final long serialVersionUID = 4385056072652599838L;
		public UnrecognizedPacket() { super(); }
		public UnrecognizedPacket(String message) { super(message); }
		public UnrecognizedPacket(String message, Throwable cause) { super(message, cause); }
		public UnrecognizedPacket(Throwable cause) { super(cause); }
		
	}
	
}
