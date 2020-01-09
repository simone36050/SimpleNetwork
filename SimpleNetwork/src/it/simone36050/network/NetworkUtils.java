package it.simone36050.network;

public final class NetworkUtils {

	private NetworkUtils() {
		throw new RuntimeException("NetworkUtils  is not instantiable");
	}
	
	public static short getShort(byte[] array, int pos) {
		short n = 0;
		n |= (array[pos] & 0xFF) << 8;
		n |= (array[pos + 1] & 0xFF);
		return n;
	}
	
	public static void setShort(short value, byte[] array, int pos) {
		array[pos] = (byte)((value >>> 8) & 0xFF);
		array[pos + 1] = (byte)(value & 0xFF);
	}
	
}
