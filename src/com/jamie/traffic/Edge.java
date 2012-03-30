package com.jamie.traffic;

import java.util.ArrayList;

public class Edge {

	private Node node1;
	private Node node2;
	private double distance;
	public ArrayList<Car> carsToward1;
	public ArrayList<Car> carsToward1old;
	public ArrayList<Car> carsToward2;
	public ArrayList<Car> carsToward2old;

	public Edge(Node n1, Node n2) {
		node1 = n1;
		node2 = n2;
		distance = Math.sqrt(Math.pow(node1.getX() - node2.getX(), 2) + Math.pow(node1.getY() - node2.getY(), 2));
		n1.addEdge(this);
		n2.addEdge(this);
		carsToward1 = new ArrayList<Car>();
		carsToward2 = new ArrayList<Car>();
		carsToward1old = new ArrayList<Car>();
		carsToward2old = new ArrayList<Car>();
	}

	public Node getNode1() {
		return node1;
	}

	public Node getNode2() {
		return node2;
	}

	public Car getNextCar(Car current) {
		if (carsToward1.contains(current) && current.imDest == node1 && (carsToward1.indexOf(current) + 1 >= carsToward1.size() - 1)) {
			return carsToward1.get(carsToward1.indexOf(current) + 1);
		} else if (carsToward2.contains(current) && current.imDest == node2 && (carsToward2.indexOf(current) + 1 >= carsToward2.size() - 1)) {
			return carsToward2.get(carsToward2.indexOf(current) + 1);
		}
		return null;
	}

	public Car getPrevCar(Car current) {
		if (current.imDest == node1 && (carsToward1.indexOf(current) - 1 >= 0)) {
			return carsToward1.get(carsToward1.indexOf(current) - 1);
		} else if (current.imDest == node2 && (carsToward2.indexOf(current) - 1 >= 0)) {
			return carsToward1.get(carsToward2.indexOf(current) - 1);
		}
		return null;
	}

	public Node getNeighbor(Node n) {
		if (n == node1)
			return node2;

		if (n == node2)
			return node1;

		return null;
	}

	public double getDistance() {
		return distance;
	}
}
