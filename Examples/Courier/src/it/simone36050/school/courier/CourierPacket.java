package it.simone36050.school.courier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CourierPacket {
	
	private long id;
	private List<Integer> nodes = new ArrayList<>();
	private boolean ended;
	
	public CourierPacket() {
	}
	
	public CourierPacket(long id, List<Integer> nodes, boolean ended) {
		this.id = id;
		this.nodes = nodes;
		this.ended = ended;
	}
	
	public static CourierPacket createPacket() {
		return new CourierPacket(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE), new ArrayList<>(), false);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public List<Integer> getNodes() {
		return Collections.unmodifiableList(nodes);
	}
	
	public void addNode(int node) {
		nodes.add(node);
	}
	
	public boolean isEnded() {
		return ended;
	}
	
	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	
}
