package com.jamie.traffic;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class TrafficPanel extends JPanel implements MouseListener {

	private BufferedImage roadPiece;
	private BufferedImage intersection;
	private NodeNetwork myNodeNet;

	public TrafficPanel(int w, int h, NodeNetwork n) throws IOException {
		this.setPreferredSize(new Dimension(w, h));
		this.setSize(w, h);
		roadPiece = ImageIO.read(new File("images/roadpiece.png"));
		intersection = ImageIO.read(new File("images/intersection.png"));
		myNodeNet = n;
		this.addMouseListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		myNodeNet.paint(g2);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		myNodeNet.update();
		this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		myNodeNet.update();
		this.repaint();
	}
}
