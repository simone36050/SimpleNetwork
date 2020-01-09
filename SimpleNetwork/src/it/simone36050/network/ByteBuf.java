package it.simone36050.network;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class ByteBuf {
	
	private LinkedList<Byte> buf = new LinkedList<Byte>();
	
	public byte readByte() {
		try {
			return buf.removeFirst();
		} catch (NoSuchElementException e) {
			throw new ByteBuffNoElementException(e);
		}
	}
	
	public short readShort() {
		try {
			return (short)((buf.removeFirst() & 0xFF) << 8 | buf.removeFirst() & 0xFF);
		} catch (NoSuchElementException e) {
			throw new ByteBuffNoElementException(e);
		}
	}
	
	public int readInt() {
		try {
			return 	(buf.removeFirst() & 0xFF) << 24 |
					(buf.removeFirst() & 0xFF) << 16 |
					(buf.removeFirst() & 0xFF) << 8  |
					(buf.removeFirst() & 0xFF);
		} catch (NoSuchElementException e) {
			throw new ByteBuffNoElementException(e);
		}
	}
	
	public long readLong() {
		try {
			return 	((long) buf.removeFirst() & 0xFF) << 56 |
					((long) buf.removeFirst() & 0xFF) << 48 |
					((long) buf.removeFirst() & 0xFF) << 40 |
					((long) buf.removeFirst() & 0xFF) << 32 |
					((long) buf.removeFirst() & 0xFF) << 24 |
					((long) buf.removeFirst() & 0xFF) << 16 |
					((long) buf.removeFirst() & 0xFF) << 8  |
					((long) buf.removeFirst() & 0xFF);
		} catch (NoSuchElementException e) {
			throw new ByteBuffNoElementException(e);
		}
	}
	
	public ByteBuf writeByte(byte b) {
		buf.addLast(b);
		return this;
	}
	
	public ByteBuf writeShort(short s) {
		buf.addLast((byte)(s >>> 8));
		buf.addLast((byte)s);
		return this;
	}
	
	public ByteBuf writeInt(int i) {
		buf.addLast((byte)(i >>> 24));
		buf.addLast((byte)(i >>> 16));
		buf.addLast((byte)(i >>> 8));
		buf.addLast((byte)i);
		return this;
	}
	
	public ByteBuf writeLong(long l) {
		buf.addLast((byte)(l >>> 56));
		buf.addLast((byte)(l >>> 48));
		buf.addLast((byte)(l >>> 40));
		buf.addLast((byte)(l >>> 32));
		buf.addLast((byte)(l >>> 24));
		buf.addLast((byte)(l >>> 16));
		buf.addLast((byte)(l >>> 8));
		buf.addLast((byte)l);
		return this;
	}
	
	public boolean readBool() {
		return readByte() == 0 ? false : true;
	}
	
	public ByteBuf writeBool(boolean b) {
		writeByte((byte)(b ? 1 : 0));
		return this;
	}
	
	public int readVarInt() {
		int result = 0, count = 0;
		byte read;
		
		do {
			read = readByte();
			int value = (read & 0b0111_1111);
			result |= (value << (7 * count));
			
			count++;
			if(count > 5)
				throw new VarVariableTooLongException("VarInt is too big");
		} while((read & 0b1000_0000) != 0);
		
		return result;
	}
	
	public ByteBuf writeVarInt(int i) {
		do {
			byte tmp = (byte)(i & 0b0111_1111);
			i >>>= 7;
			if(i != 0)
				tmp |= 0b1000_0000;
			
			writeByte(tmp);
		} while(i != 0);
		return this;
	}
	
	public String readString() {
		int length = readVarInt();
		
		if(length > Short.MAX_VALUE)
			throw new WrongStringLengthException("Length " + length + " is bigger than " + Short.MAX_VALUE);
		
		byte[] data = new byte[length];
		for(int i = 0; i < length; i++)
			data[i] = readByte();
		
		return new String(data, StandardCharsets.UTF_8);
	}
	
	public ByteBuf writeString(String s) {
		int length = s.length();
		
		if(length > Short.MAX_VALUE)
			throw new WrongStringLengthException("Length " + length + " is bigger than " + Short.MAX_VALUE);
		
		byte[] data = s.getBytes(StandardCharsets.UTF_8);
		writeVarInt(length);
		
		for(int i = 0; i < length; i++)
			writeByte(data[i]);
		
		return this;
	}
	
	public ByteBuf writeEnum(Enum<?> e) {
		writeVarInt(e.ordinal());
		return this;
	}
	
	public <T extends Enum<?>> T readEnum(T[] values) {
		int i = readVarInt();
		for(T v : values)
			if(i == v.ordinal())
				return v;
		return null;
	}
	
	public void toByteArray(byte[] array, int pos) {
		try {
			for(int i = 0; i < buf.size(); i++)
				array[pos + i] = buf.get(i);
		} catch (IndexOutOfBoundsException e) {
			throw new ByteBuffException("Out of bound", e);
		}
	}
	
	public int size() {
		return buf.size();
	}
	
	public static ByteBuf fromByteArray(byte[] array, int pos) {
		try {
			ByteBuf b = new ByteBuf();
			for(int i = pos; i < array.length; i++)
				b.buf.addLast(array[i]);
			return b;
		} catch (IndexOutOfBoundsException e) {
			throw new ByteBuffException("Out of bound", e);
		}
	}
	
	public static class ByteBuffException extends NetworkException {
		
		private static final long serialVersionUID = -5481373423059491293L;
		public ByteBuffException() { super(); }
		public ByteBuffException(String message) { super(message); }
		public ByteBuffException(String message, Throwable cause) { super(message, cause); }
		public ByteBuffException(Throwable cause) { super(cause); }
		
	}
	
	public static class VarVariableTooLongException extends ByteBuffException {
		
		private static final long serialVersionUID = -7873205877487052203L;
		public VarVariableTooLongException() { super(); }
		public VarVariableTooLongException(String message) { super(message); }
		public VarVariableTooLongException(String message, Throwable cause) { super(message, cause); }
		public VarVariableTooLongException(Throwable cause) { super(cause); }
		
	}
	
	public static class ByteBuffNoElementException extends ByteBuffException {
		
		private static final long serialVersionUID = 5624985618515792118L;
		public ByteBuffNoElementException() { super(); }
		public ByteBuffNoElementException(String message) { super(message); }
		public ByteBuffNoElementException(String message, Throwable cause) { super(message, cause); }
		public ByteBuffNoElementException(Throwable cause) { super(cause); }
		
	}
	
	public static class WrongStringLengthException extends ByteBuffException {
		
		private static final long serialVersionUID = -1684594645275371812L;
		public WrongStringLengthException() { super(); }
		public WrongStringLengthException(String message) { super(message); }
		public WrongStringLengthException(String message, Throwable cause) { super(message, cause); }
		public WrongStringLengthException(Throwable cause) { super(cause); }
		
	}
	
}