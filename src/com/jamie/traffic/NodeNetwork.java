package com.jamie.traffic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JSlider;

public class NodeNetwork {

	private ArrayList<Node> myNodes;
	private ArrayList<Edge> myEdges;
	private ArrayList<Car> myCars;
	private Random rand;
	public JSlider mySpinner;
	private int updates = 0;
	private int updateMaximum = 3000;
	private int generationSize = 20;
	private int generation = 0;

	public NodeNetwork() throws FileNotFoundException {
		myNodes = new ArrayList<Node>();
		myEdges = new ArrayList<Edge>();

		// Load a list of node coordinate from a file
		InputStream nodesList = this.getClass().getResourceAsStream("nodes.conf");
		Scanner s = new Scanner(nodesList);

		while (s.hasNextLine()) {
			String[] split = s.nextLine().split(":");
			if (split[0].equals("n")) {
				int x = Integer.parseInt(split[1].split(",")[0]);
				int y = Integer.parseInt(split[1].split(",")[1]);
				String name = split[2];
				myNodes.add(new Node(Node.Type.STOPSIGN, x, y, name));
			} else if (split[0].equals("e")) {
				int n1 = Integer.parseInt(split[1].split(",")[0]);
				int n2 = Integer.parseInt(split[1].split(",")[1]);
				myEdges.add(new Edge(myNodes.get(n1), myNodes.get(n2)));
			}
		}

		// Generate a distance map that holds the shortest distance between two nodes possible
		// Map each node to a map of all other nodes (and a double representing the distance to each other node)

		Map<Node, Map<Node, Double>> dist = new HashMap<Node, Map<Node, Double>>();

		// Start off by adding the distance from each node to all of its neighboring nodes
		for (int i = 0; i < myNodes.size(); i++) {
			Map<Node, Double> temp = new HashMap<Node, Double>();
			for (Edge ed : myNodes.get(i).getEdges()) {
				temp.put(ed.getNeighbor(myNodes.get(i)), ed.getDistance());
			}

			temp.put(myNodes.get(i), 0.0); // Distance from node to itself is zero
			dist.put(myNodes.get(i), temp); // Put the Map containing distances in the large map
		}

		// Find the shortest path from each node to each other node
		for (int k = 0; k < myNodes.size(); k++) { // loop through intermediate nodes
			for (int i = 0; i < myNodes.size(); i++) { // loop through start nodes
				Node start = myNodes.get(i);
				for (int j = 0; j < myNodes.size(); j++) { // loop through end nodes
					Node end = myNodes.get(j);
					double oldDist = dist.get(start).get(end) != null ? dist.get(start).get(end) : Double.POSITIVE_INFINITY;
					Node interm = myNodes.get(k);
					Double startInt = dist.get(start).get(interm); // distance from start node to intermediate node
					Double intStop = dist.get(interm).get(end); // distance from intermediate node to end node
					if (startInt == null) {
						startInt = Double.POSITIVE_INFINITY;
					}
					if (intStop == null) {
						intStop = Double.POSITIVE_INFINITY;
					}
					double newDist = startInt + intStop;
					if (newDist < oldDist) { // if we found a shorter distance, put the new shortest distance on the dist map
						dist.get(start).put(end, newDist);
					}
				}
			}
		}

		// Fill in each node's "next" map. This map holds each other node, and the immediate
		// edge that should be taken to get there the quickest
		for (Node start : myNodes) {
			for (Node end : myNodes) {
				double min = dist.get(start).get(end);
				for (Edge edge : start.getEdges()) {
					Node neighbor = edge.getNeighbor(start);
					if (edge.getDistance() + dist.get(neighbor).get(end) == min) {
						start.next.put(end, edge);
						break;
					}
				}
			}
		}

		rand = new Random();
		myCars = new ArrayList<Car>();

		for (int i = 0; i < generationSize; i++) {
			myCars.add(new Car(i + ""));
			place(myCars.get(i));
		}

		for (int i = 0; i < 40; i++) {
			update();
		}
		generatePoputlation();
	}

	public void advanceGeneration() {

		generation++;
		ArrayList<Car> newGeneration = new ArrayList<Car>();

		int max = 0;
		for (Car c : myCars)
			if (c.points > max)
				max = c.points;

		int total = 0;
		Car bestCar = null;
		Car bestCar2 = null;
		for (Car c : myCars) {
			c.points = max - c.points;
			if (bestCar == null || c.points > bestCar.points) {
				bestCar2 = bestCar;
				bestCar = c;
			} else if (bestCar2 == null || c.points > bestCar2.points) {
				bestCar2 = c;
			}
			total += c.points;
			// System.out.println(c.name + " | " + c.points);
		}

		bestCar.name = "0";
		bestCar2.name = "1";
		newGeneration.add(bestCar2);
		newGeneration.add(bestCar);

		for (Car c : myCars) {
			c.percent = Math.round((double) c.points / (double) total * 1000.0);
		}

		Collections.sort(myCars, new CarPctComparator());

		for (int i = 0; i < generationSize - 2; i++) {
			Car car1 = myCars.get(0);
			Car car2 = myCars.get(1);

			double r = rand.nextDouble();
			for (Car c : myCars) {
				r -= c.percent;
				if (r <= 0)
					car1 = c;
			}

			r = rand.nextDouble();
			for (Car c : myCars) {
				r -= c.percent;
				if (r <= 0)
					car2 = c;
			}

			newGeneration.add(crossover(car1, car2, i + 2));
		}

		myCars.clear();
		myCars.addAll(newGeneration);
		for (Car c : myCars) {
			place(c);
		}
		updates = 0;

	}

	public Car crossover(Car c1, Car c2, int in) {
		Car newCar = new Car("" + in);
		double[] w11 = c1.weightsIH;
		double[] w12 = c2.weightsIH;
		double[] w21 = c1.weightsHO;
		double[] w22 = c2.weightsHO;

		for (int i = 0; i < w11.length; i++) {
			newCar.weightsIH[i] = rand.nextDouble() >= 0.5 ? w11[i] : w12[i];
		}

		for (int i = 0; i < w21.length; i++) {
			newCar.weightsHO[i] = rand.nextDouble() >= 0.5 ? w21[i] : w22[i];
		}

		return newCar;
	}

	public void updateGeneration() {
		for (Car c : myCars) {
			Car ahead = c.edge.getNextCar(c);
			Car behind = c.edge.getPrevCar(c);
			c.input[4] = c.edge.getDistance() - c.distance;
			if (ahead == null) {
				c.input[0] = 500.0;
				c.input[1] = 3.0;
			} else {
				c.input[0] = ahead.distance - c.distance;
				c.input[1] = ahead.speed;
			}

			if (behind == null) {
				c.input[2] = 500.0;
				c.input[3] = 3.0;
			} else {
				c.input[2] = c.distance - behind.distance;
				c.input[3] = behind.speed;
			}
			Car.pushLayer(c.input, c.hidden, c.weightsIH);
			Car.sigmoid(c.hidden, 1.0);
			Car.pushLayer(c.hidden, c.output, c.weightsHO);
			Car.sigmoid(c.output, 3.0);
			c.targetSpeed = c.output[0];
		}
	}

	public void generatePoputlation() {
		for (Car c : myCars) {
			for (int i = 0; i < c.weightsIH.length; i++) {
				c.weightsIH[i] = rand.nextDouble() - 0.5;
			}
			for (int i = 0; i < c.weightsHO.length; i++) {
				c.weightsHO[i] = rand.nextDouble() - 0.5;
			}
		}
	}

	public void place(Car c) {
		int dest = rand.nextInt(myNodes.size());
		c.destination = myNodes.get(dest);

		int node = rand.nextInt(myNodes.size());
		while (myNodes.get(node) == c.destination)
			node = rand.nextInt(myNodes.size());

		c.edge = myNodes.get(node).next.get(c.destination);
		c.imDest = c.edge.getNeighbor(myNodes.get(node));

		c.distance = 0.0;
		c.speed = rand.nextDouble() * 1.3 + 0.1;
		c.targetSpeed = c.speed;
	}

	public void update() {

		if (updates >= updateMaximum) {
			advanceGeneration();
		}
		for (Edge e : myEdges) {
			e.carsToward1old.clear();
			e.carsToward2old.clear();
			for (Car c : e.carsToward1) {
				e.carsToward1old.add(c.clone());
			}
			for (Car c : e.carsToward2) {
				e.carsToward2old.add(c.clone());
			}
			e.carsToward1.clear();
			e.carsToward2.clear();
		}

		updateGeneration();

		for (Car car : myCars) {

			if (car.targetSpeed > car.speed) {
				car.speed += 0.01;
				if (car.speed > car.targetSpeed)
					car.speed = car.targetSpeed;
			} else if (car.targetSpeed < car.speed) {
				car.speed -= 0.01;
				if (car.speed < car.targetSpeed)
					car.speed = car.targetSpeed;
			}

			if (car.speed < 0.0) {
				car.speed = 0.0;
				System.out.println(car.targetSpeed);
			}
			car.distance += car.speed;

			if (car.distance >= car.edge.getDistance()) {
				car.distance = 0.0;

				if (car.imDest == car.destination) {
					Node random = myNodes.get(rand.nextInt(myNodes.size()));
					while (car.imDest == random) {
						random = myNodes.get(rand.nextInt(myNodes.size()));
					}
					car.destination = random;
					if (car.speed >= 1.0) {
						car.points++;
					}
					car.points -= 1;
				}
				Node temp = car.imDest;
				Edge newEdge = temp.next.get(car.destination);
				car.edge = newEdge;
				if (newEdge.getNode1() == temp) {
					car.imDest = newEdge.getNode2();
				} else {
					car.imDest = newEdge.getNode1();
				}

			}

			if (car.edge.getNode1() == car.imDest) {
				car.edge.carsToward1.add(car.clone());
			}

			if (car.edge.getNode2() == car.imDest) {
				car.edge.carsToward2.add(car.clone());
			}

		}

		for (Edge e : myEdges) {
			Collections.sort(e.carsToward1);
			Collections.sort(e.carsToward1old);
			Collections.sort(e.carsToward2);
			Collections.sort(e.carsToward2old);

			for (int i = 0; i < e.carsToward1old.size(); i++) {
				Car oldBehind = e.carsToward1old.get(i);
				for (int j = i + 1; j < e.carsToward1old.size(); j++) {
					Car oldAhead = e.carsToward1old.get(j);
					for (int k = 0; k < e.carsToward1.size(); k++) {
						Car newBehind = e.carsToward1.get(k);
						if (newBehind.name.equals(oldBehind.name)) {
							break;
						} else if (newBehind.name.equals(oldAhead.name)) {
							for (Car c : myCars) {
								if (c.name.equals(newBehind.name)) {
									c.points += 1;
								} else if (c.name.equals(oldBehind.name)) {
									c.points += 3;
								}
							}
							break;
						}
					}
				}
			}

			for (int i = 0; i < e.carsToward2old.size(); i++) {
				Car oldBehind = e.carsToward2old.get(i);
				for (int j = i + 1; j < e.carsToward2old.size(); j++) {
					Car oldAhead = e.carsToward2old.get(j);
					for (int k = 0; k < e.carsToward2.size(); k++) {
						Car newBehind = e.carsToward2.get(k);
						if (newBehind.name.equals(oldBehind.name)) {
							break;
						} else if (newBehind.name.equals(oldAhead.name)) {
							for (Car c : myCars) {
								if (c.name.equals(newBehind.name)) {
									c.points += 1;
								} else if (c.name.equals(oldBehind.name)) {
									c.points += 3;
								}
							}
							break;
						}
					}
				}
			}

		}
		updates++;

	}

	public void paint(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.fillRect(0, 0, 1000, 1000);
		g2.setColor(Color.black);
		g2.drawString("Generation: " + generation, 10, 20);
		g2.drawString("Updates Left: " + (updateMaximum - updates), 10, 35);
		g2.setStroke(new BasicStroke(2f));

		for (int i = 0; i < myEdges.size(); i++) {
			g2.drawLine((int) myEdges.get(i).getNode1().getX(), (int) myEdges.get(i).getNode1().getY(), (int) myEdges.get(i).getNode2().getX(),
					(int) myEdges.get(i).getNode2().getY());
		}

		g2.setColor(Color.blue);

		for (int i = 0; i < myNodes.size(); i++) {
			g2.fillOval((int) (myNodes.get(i).getX() - 3.0f), (int) (myNodes.get(i).getY() - 3.0f), 6, 6);
			g2.drawString(myNodes.get(i).name, myNodes.get(i).getX() + 5, myNodes.get(i).getY() - 8);
		}

		int x, y = 0;
		float dash[] = { 5.0f, 2.0f };
		g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

		int textStartY = 20;
		int textStartX = 420;
		int interval = 14;

		for (int i = 0; i < myCars.size(); i++) {
			x = getCarXY(myCars.get(i)).x;
			y = getCarXY(myCars.get(i)).y;
			g2.setColor(Color.green);

			g2.drawLine(x, y, (int) myCars.get(i).destination.getX(), (int) myCars.get(i).destination.getY());
			g2.setColor(Color.red);
			g2.fillOval(x - 2, y - 2, 4, 4);

			g2.setColor(Color.black);
			g2.setFont(new Font("Arial", 0, 12));
			g2.drawString(myCars.get(i).name + " | pts:" + myCars.get(i).points, textStartX, textStartY + i * interval);
			g2.setFont(new Font("Arial", 0, 10));
			g2.setColor(Color.blue);
			g2.drawString(myCars.get(i).name + "", (int) x, (int) y - 6);
		}

	}

	public Point getCarXY(Car c) {
		double angle = Math.atan2(c.edge.getNode1().getY() - c.edge.getNode2().getY(), c.edge.getNode1().getX() - c.edge.getNode2().getX());
		double x, y = 0.0;
		int d = 3;

		if (c.imDest.equals(c.edge.getNode1())) {
			x = (c.edge.getNode2().getX() + Math.cos(angle) * c.distance);
			y = c.edge.getNode2().getY() + Math.sin(angle) * c.distance;
			return new Point((int) (x + d * Math.cos(angle + Math.PI / 2.0)), (int) (y + d * Math.sin(angle + Math.PI / 2.0)));
		} else {
			x = (c.edge.getNode1().getX() - Math.cos(angle) * c.distance);
			y = c.edge.getNode1().getY() - Math.sin(angle) * c.distance;
			return new Point((int) (x + d * Math.cos(angle - Math.PI / 2.0)), (int) (y + d * Math.sin(angle - Math.PI / 2.0)));
		}

	}
}
