package com.jamie.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node implements Comparable<Node> {

	private Type myType;
	private float x, y;
	private ArrayList<Edge> edges;
	public String name;
	public Map<Node, Edge> next;

	public enum Type {
		STOPLIGHT, STOPSIGN, ROADJOINT
	}

	public Node(Node.Type type, float x, float y, String name) {
		this.x = x;
		this.y = y;
		this.myType = type;
		this.edges = new ArrayList<Edge>();
		this.name = name;
		this.next = new HashMap<Node, Edge>();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void addEdge(Edge e) {
		edges.add(e);
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	@Override
	public int compareTo(Node o) {
		if (x == o.x && y == o.y) {
			return 0;
		}
		return 1;
	}

}
