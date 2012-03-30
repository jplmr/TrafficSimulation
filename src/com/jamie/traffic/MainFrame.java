package com.jamie.traffic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class MainFrame extends JFrame {

	private TrafficPanel myTrafficPanel;

	public MainFrame(NodeNetwork nn) {
		super("Traffic Simulator");
		try {

			JPanel main = new JPanel();
			main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

			myTrafficPanel = new TrafficPanel(500, 500, nn);
			main.add(myTrafficPanel);

			JSlider slider = new JSlider(0, 100);
			slider.setValue(1);
			nn.mySpinner = slider;
			main.add(slider);

			this.getContentPane().add(main);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.pack();
			this.setSize(500, 500);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		NodeNetwork n = new NodeNetwork();
		MainFrame main = new MainFrame(n);
		main.setVisible(true);

		while (true) {
			n.update();
			main.repaint();
			Thread.sleep((long) n.mySpinner.getValue());
		}
	}
}
