package com.jamie.traffic;

import java.util.Comparator;

public class CarPctComparator implements Comparator<Car> {

	@Override
	public int compare(Car c1, Car c2) {
		return (int) Math.signum(c1.percent - c2.percent);
	}

}
