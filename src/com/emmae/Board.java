package com.emmae;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {
	
	enum Direction {UP,DOWN,LEFT,RIGHT}
	
	private final int B_WIDTH  = 600, B_HEIGHT = B_WIDTH,
					  DOT_SIZE =  30, ALL_DOTS = (B_WIDTH * B_HEIGHT) / DOT_SIZE,
					  RAND_POS =  ((B_WIDTH / (DOT_SIZE / 10)) - DOT_SIZE) / 10;
	
	private final int x[] = new int[ALL_DOTS];
	private final int y[] = new int[ALL_DOTS];
	
	private int dots, apple_x, apple_y;
	private byte score;
	
	private boolean inGame, rePlay;
	
	private Timer timer;
	private Image dot, apple, head;
	
	private final Font scoreFont = new Font("Helvetica", Font.BOLD, 30);
	private final Font large = new Font("Helvetica", Font.BOLD, 32);
	private final Font small = new Font("Helvetica", Font.BOLD, 26);
	private final Font smallest = new Font("Helvetica", Font.BOLD, 22);
	private final FontMetrics metrics1 = getFontMetrics(large);
	private final FontMetrics metrics2 = getFontMetrics(small);
	private final FontMetrics metrics3 = getFontMetrics(smallest);
	
	private int delay;
	
	Direction nextDirection, previousDirection;
	
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
		dot = iid.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_DEFAULT);
		
		ImageIcon iia = new ImageIcon(getClass().getResource("/res/apple.png"));
		apple = iia.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_DEFAULT);
		
		ImageIcon iih = new ImageIcon(getClass().getResource("/res/head.png"));
		head = iih.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_DEFAULT);
	}
	
	private void initGame() {
		dots = 3;
		score = 0;
		
		for (int z = 0; z < dots; z++) {
			x[z] = DOT_SIZE*8 - z * 10;
			y[z] = DOT_SIZE*8;
		}
		
		locateApple();
		
		nextDirection = Direction.RIGHT;
		previousDirection = Direction.RIGHT;
		
		rePlay = false;
		inGame = true;
		
		delay = 2000;
		
		timer = new Timer(delay / 10, this);
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
			
			g.setColor(Color.WHITE);
			g.setFont(scoreFont);
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
		
		g.setColor(Color.WHITE);
		g.setFont(large);
		g.drawString(msg1, (B_WIDTH - metrics1.stringWidth(msg1)) / 2, (B_HEIGHT / 2) - 20);
		
		g.setFont(small);
		g.drawString(msgScore, (B_WIDTH / 8), (B_HEIGHT / 8));
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(msg2, (B_WIDTH - metrics2.stringWidth(msg2)) / 2, (B_HEIGHT / 2) + 15);
		
		g.setFont(smallest);
		g.drawString(msg3, (B_WIDTH - metrics3.stringWidth(msg3)) / 2, (B_HEIGHT / 2) + 40);
	}
	
	private void checkApple() {
		if ((x[0] == apple_x) && (y[0] == apple_y)) {
			dots++;
			score++;
			locateApple();
			delay -= (delay / 40);
			timer.setDelay(delay/10);
		}
	}
	
	private void move() {
		for (int z = dots; z > 0; z--) {
			x[z] = x[(z - 1)];
			y[z] = y[(z - 1)];
		}
		
		if (nextDirection == Direction.LEFT) {
			x[0] -= DOT_SIZE;
		}
		if (nextDirection == Direction.RIGHT) {
			x[0] += DOT_SIZE;
		}
		if (nextDirection == Direction.UP) {
			y[0] -= DOT_SIZE;
		}
		if (nextDirection == Direction.DOWN) {
			y[0] += DOT_SIZE;
		}
		previousDirection = nextDirection;
	}
	
	private void checkCollision() {
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
	}
	
	private void locateApple() {
		int r = (int) (Math.random() * (RAND_POS));
		apple_x = r * (DOT_SIZE);
		
		r = (int) (Math.random() * (RAND_POS));
		apple_y = r * (DOT_SIZE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (rePlay) {
			timer.stop();
			initGame();
		}
		if (inGame) {
			checkApple();
			checkCollision();
			move();
		}
		
		repaint();
	}
	
	private class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if ((key == KeyEvent.VK_LEFT) && (previousDirection != Direction.RIGHT)) {
				nextDirection = Direction.LEFT;
			}
			
			if ((key == KeyEvent.VK_RIGHT) && (previousDirection != Direction.LEFT)) {
				nextDirection = Direction.RIGHT;
			}
			
			if ((key == KeyEvent.VK_UP) && (previousDirection != Direction.DOWN)) {
				nextDirection = Direction.UP;
			}
			
			if ((key == KeyEvent.VK_DOWN) && (previousDirection != Direction.UP)) {
				nextDirection = Direction.DOWN;
			}
			
			if ((key == KeyEvent.VK_SPACE) && (!inGame)) {
				rePlay = true;
			}
		}
	}
}