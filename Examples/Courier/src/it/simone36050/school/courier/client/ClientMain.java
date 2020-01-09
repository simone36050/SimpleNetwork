package it.simone36050.school.courier.client;

import it.simone36050.network.Bootstrap;
import it.simone36050.network.Connection;
import it.simone36050.school.courier.protocol.CourierProtocol;
import it.simone36050.school.courier.protocol.NewPacketRequest;
import it.simone36050.school.courier.protocol.PacketInfoRequest;
import it.simone36050.school.courier.protocol.PassingPacketRequest;

public class ClientMain {

	public static void main(String[] args) throws InterruptedException {
		Object lock = new Object();
		ClientHandler handler = new ClientHandler(lock);
		long id;
		
		// connect to server
		Connection conn = new Bootstrap()
				.host("127.0.0.1")
				.port(36050)
				.protocol(CourierProtocol.PROTOCOL)
				.handler(handler)
				.connect();
		
		// send packet creation
		// create packet
		conn.sendAndFlush(new NewPacketRequest(36100));
		wait(lock);
		
		// add all nodes
		id = handler.packet;
		
		int[] nodes = new int[] {36051, 36075, 36071};
		for(int n : nodes) {
			// send node
			handler.lastNode = n;
			conn.sendAndFlush(new PassingPacketRequest(id, n, n == nodes[nodes.length - 1]));
			wait(lock);
			
			// show info
			conn.sendAndFlush(new PacketInfoRequest(id));
			wait(lock);
		}
		
		conn.shutdown();
		System.out.println("All ended!");
	}
	
	public static void wait(Object lock) throws InterruptedException {
		synchronized (lock) {
			lock.wait();
		}
	}
	
}
