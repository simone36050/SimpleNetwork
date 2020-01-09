package it.simone36050.school.courier.client;

import java.util.Iterator;

import it.simone36050.school.courier.CourierPacket;
import it.simone36050.school.courier.protocol.CourierHandler;
import it.simone36050.school.courier.protocol.NewPacketResponse;
import it.simone36050.school.courier.protocol.PacketInfoResponse;
import it.simone36050.school.courier.protocol.PassingPacketResponse;

public class ClientHandler extends CourierHandler {

	private Object lock;
	public long packet = -1;
	public int lastNode = -1;
	
	public ClientHandler(Object lock) {
		this.lock = lock;
	}
	
	@Override
	public void handleNewPacketResponse(NewPacketResponse packet) {
		System.out.println("Packet created: " + packet.getId());
		this.packet = packet.getId();
		
		notify(lock);
	}
	
	@Override
	public void handlePassingPacketResponse(PassingPacketResponse packet) {
		System.out.println("Confirmed passed node: " + lastNode);
		
		notify(lock);
	}
	
	@Override
	public void handlePacketInfoResponse(PacketInfoResponse packet) {
		CourierPacket pack = packet.getPacket();
		
		System.out.println("=========== REPORT ===========");
		if(packet.isFounded()) {
			System.out.println("ID: " + pack.getId());
			StringBuilder b = new StringBuilder();
			Iterator<Integer> iter = pack.getNodes().iterator();
			while(iter.hasNext()) {
				b.append(iter.next());
				if(iter.hasNext())
					b.append(" -> ");
			}
			System.out.println("Nodes: " + b);
			System.out.println("Delivered: " + (pack.isEnded() ? "yes" : "no"));
		} else
			System.out.println("Packet not found");
		System.out.println("==============================");
		
		notify(lock);
	}
	
	public static void notify(Object lock) {
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
}
