package com.jamie.traffic;

import java.util.ArrayList;

public class Car implements Comparable<Car> {

	public Edge edge;
	public Node destination;
	public Node imDest;
	public double distance;
	public double speed;
	public double targetSpeed;

	// Weights arrays
	public double[] weightsIH;
	public double[] weightsHO;

	// Network layers
	public double[] input;
	public double[] hidden;
	public double[] output;

	public int points = 1;
	public static final double maxSpeed = 3.0;
	public boolean isSwitched = false;
	public String name;
	public double percent;
	
	public double lowerPercentLimit;
	public double upperPercentLimit;

	public Car(String name) {
		input = new double[5];
		hidden = new double[3];
		output = new double[1];
		weightsIH = new double[input.length * hidden.length];
		weightsHO = new double[hidden.length * output.length];
		this.name = name;
	}

	public Car clone() {
		Car newCar = new Car(name);
		newCar.distance = this.distance;
		return newCar;
	}

	public static double sigmoid(double o, double top) {
		return top / (1 + Math.exp(-o));
	}

	public static void sigmoid(double[] o, double top) {
		for (int i = 0; i < o.length; i++) {
			o[i] = sigmoid(o[i], top);
		}
	}

	public String toString() {
		return "" + percent;
	}

	@Override
	public int compareTo(Car o) {
		return (int) Math.signum(distance - o.distance);
	}

	public static void pushLayer(double a[], double b[], double w[]) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < a.length; j++) {
				b[i] += w[i * a.length + j] * a[j];
			}
		}
	}

}
