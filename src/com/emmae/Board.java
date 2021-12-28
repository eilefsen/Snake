package com.emmae;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {
	
	private final int B_WIDTH  = 200, B_HEIGHT = B_WIDTH,
					  DOT_SIZE =  10, ALL_DOTS = 900,
					  RAND_POS =  ((B_WIDTH - 10) / 10);
	
	private final int x[] = new int[ALL_DOTS];
	private final int y[] = new int[ALL_DOTS];
	
	private int dots, apple_x, apple_y;
	private byte score;
	
	private boolean leftDirection = false, rightDirection = true,
					upDirection   = false, downDirection  = false,
					inGame 		  = true,  rePlay		  = false;
	
	private Timer timer;
	private Image dot, apple, head;
	private int delay;
	
	public Board() {
		initBoard();
	}
	
	private void initBoard() {
		addKeyListener(new TAdapter());
		setBackground(Color.BLACK);
		setFocusable(true);
		
		setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
		loadImages();
		initGame();
	}
	
	private void loadImages() {
		ImageIcon iid = new ImageIcon(getClass().getResource("/res/dot.png"));
		dot = iid.getImage();
		
		ImageIcon iia = new ImageIcon(getClass().getResource("/res/apple.png"));
		apple = iia.getImage();
		
		ImageIcon iih = new ImageIcon(getClass().getResource("/res/head.png"));
		head = iih.getImage();
	}
	
	private void initGame() {
		dots = 3;
		score = 0;
		
		for (int z = 0; z < dots; z++) {
			x[z] = 50 - z * 10;
			y[z] = 50;
		}
		
		locateApple();
		
		rePlay = false;
		inGame = true;
		delay = 200;
		
		timer = new Timer(delay, this);
		timer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		doDrawing(g);
	}
	
	private void doDrawing(Graphics g) {
		if (inGame) {
			g.drawImage(apple, apple_x, apple_y, this);
			
			Font small = new Font("Helvetica", Font.BOLD, 12);
			
			g.setColor(Color.WHITE);
			g.setFont(small);
			g.drawString(String.valueOf(score), (B_WIDTH / 8) - 10, (B_HEIGHT / 8));
			
			
			
			for (int z = 0; z < dots; z++) {
				if (z == 0) {
					g.drawImage(head, x[z], y[z], this);
				} else {
					g.drawImage(dot, x[z], y[z], this);
				}
			}
			
			Toolkit.getDefaultToolkit().sync();
			
		} else {
			gameOver(g);
		}
	}
	
	private void gameOver(Graphics g) {
		String msgScore = "Your score was: " + score;
		String msg1 = "Game Over.";
		String msg2 = "Play again?";
		String msg3 = "(space)";
		Font large = new Font("Helvetica", Font.BOLD, 14);
		Font small = new Font("Helvetica", Font.BOLD, 11);
		Font smallest = new Font("Helvetica", Font.BOLD, 9);
		FontMetrics metrics1 = getFontMetrics(large);
		FontMetrics metrics2 = getFontMetrics(small);
		FontMetrics metrics3 = getFontMetrics(smallest);
		
		g.setColor(Color.WHITE);
		g.setFont(large);
		g.drawString(msg1, (B_WIDTH - metrics1.stringWidth(msg1)) / 2, (B_HEIGHT / 2) - 14);
		
		g.setFont(small);
		g.drawString(msgScore, (B_WIDTH / 8), (B_HEIGHT / 8));
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(msg2, (B_WIDTH - metrics2.stringWidth(msg2)) / 2, (B_HEIGHT / 2) + 7);
		
		g.setFont(smallest);
		g.drawString(msg3, (B_WIDTH - metrics2.stringWidth(msg2)) / 2 + 12, (B_HEIGHT / 2) + 21);
	}
	
	private void checkApple() {
		if ((x[0] == apple_x) && (y[0] == apple_y)) {
			dots++;
			score++;
			locateApple();
			delay -= ((delay / 20));
			timer.setDelay(delay);
		}
	}
	
	private void move() {
		for (int z = dots; z > 0; z--) {
			x[z] = x[(z - 1)];
			y[z] = y[(z - 1)];
		}
		
		if (leftDirection) {
			x[0] -= DOT_SIZE;
		}
		if (rightDirection) {
			x[0] += DOT_SIZE;
		}
		if (upDirection) {
			y[0] -= DOT_SIZE;
		}
		if (downDirection) {
			y[0] += DOT_SIZE;
		}
	}
	
	private void checkCollission() {
		for (int z = dots; z > 0; z--) {
			if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
				inGame = false;
				break;
			}
		}
		
		if (y[0] >= B_HEIGHT) {
			inGame = false;
		}
		if (y[0] < 0) {
			inGame = false;
		}
		if (x[0] >= B_WIDTH) {
			inGame = false;
		}
		if (x[0] < 0) {
			inGame = false;
		}
		if (!inGame) {
			//timer.stop();
		}
	}
	
	private void locateApple() {
		int r = (int) (Math.random() * RAND_POS);
		apple_x = r * DOT_SIZE;
		
		r = (int) (Math.random() * RAND_POS);
		apple_y = r * DOT_SIZE;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (rePlay) {
			timer.stop();
			initGame();
		}
		if (inGame) {
			checkApple();
			checkCollission();
			move();
		}
		
		repaint();
	}
	
	private class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
				leftDirection = true;
				upDirection = false;
				downDirection = false;
			}
			
			if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
				rightDirection = true;
				upDirection = false;
				downDirection = false;
			}
			
			if ((key == KeyEvent.VK_UP) && (!downDirection)) {
				upDirection = true;
				leftDirection = false;
				rightDirection = false;
			}
			
			if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
				downDirection = true;
				leftDirection = false;
				rightDirection = false;
			}
			
			if ((key == KeyEvent.VK_SPACE) && (!inGame)) {
				rePlay = true;
			}
		}
	}
}
