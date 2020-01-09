package it.simone36050.school.courier.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import it.simone36050.network.Server;
import it.simone36050.network.ServerBoostrap;
import it.simone36050.school.courier.CourierPacket;
import it.simone36050.school.courier.protocol.CourierProtocol;

public class ServerMain {

	public static void main(String[] args) throws IOException {
		Server server;
		Map<Long, CourierPacket> storage = new HashMap<>();
		
		// bind socket
		server = new ServerBoostrap()
				.port(36050)
				.protocol(CourierProtocol.PROTOCOL)
				.handler(new ServerHandler(storage))
				.errorHandler(e -> e.printStackTrace())
				.bind();
		
		// close system
		System.out.println("Press enter button to close . . .");
		readLine();
		server.shutdown();
	}
	
	private static String readLine() throws IOException {
	    if (System.console() != null) {
	        return System.console().readLine();
	    }
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
	            System.in));
	    return reader.readLine();
	}
	
}
