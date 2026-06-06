package gamewindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;

import javax.swing.*;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;

import entity.Apple;
import entity.Snake;

public class Renderer{
	private Engine engine;
	private Window ownerframe;
	private JDialog pauseDialog;
	private JLabel pauseHeaderLabel;
	private JLabel timeLabel;
	private JLabel scoreLabel;
	private JButton playButton;
	private JButton restartButton;
	private static int screen_height, screen_width;
	private static int UNIT_SIZE = 25;
	private static int arcWidth = 6, arcHeight = 6;
	private static Color snakeHeadColor, snakeBodyColor, appleColor, tileLight, tileDark, textColor, backgroundColor;
	private static Font headerText, bodyText;
	private Random colorRand = new Random();
	
	//appleIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/appleformattedfinal.png")).getImage();
	//snakeIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/snakehead.png")).getImage();
			
	Renderer(Engine engine){
		this.engine = engine;
		
		// Colors
		snakeHeadColor = Color.decode("#4A752C");
		snakeBodyColor = Color.decode("#528036");
		appleColor = Color.decode("#E74C3C");
		tileLight = Color.decode("#A2D149");
		tileDark = Color.decode("#AAD751");
		textColor = Color.WHITE;
		backgroundColor = new Color(11, 83, 81);

		// Font
		headerText = new Font("Comic Sans MS", Font.BOLD+Font.ITALIC, 36);
		bodyText = new Font("Comic Sans MS",Font.BOLD,24);
		
		screen_height = GamePanel.SCREEN_HEIGHT;
		screen_width = GamePanel.SCREEN_WIDTH;
	}
	
	public void initPauseDialog(Window ownerframe) {
		this.ownerframe = ownerframe;
		
		// Build pause dialog once to avoid recreating components every render call
		pauseDialog = new JDialog(ownerframe, "Game Paused", Dialog.DEFAULT_MODALITY_TYPE);
		pauseDialog.setSize(350, 300);
		pauseDialog.setLayout(new BorderLayout());
		pauseDialog.setResizable(false);
		pauseDialog.getContentPane().setBackground(backgroundColor);
		
		pauseHeaderLabel = new JLabel("GAME PAUSED", JLabel.CENTER);
		pauseHeaderLabel.setFont(headerText);
		pauseHeaderLabel.setForeground(textColor);

		timeLabel = new JLabel("", JLabel.CENTER);
		timeLabel.setFont(bodyText);
		timeLabel.setForeground(textColor);

		scoreLabel = new JLabel("", JLabel.CENTER);
		scoreLabel.setFont(bodyText);
		scoreLabel.setForeground(textColor);

		JPanel labels = new JPanel(new GridLayout(3,1));
		labels.setBackground(backgroundColor);
		labels.add(pauseHeaderLabel); labels.add(timeLabel); labels.add(scoreLabel);
		pauseDialog.add(labels, BorderLayout.CENTER);

		playButton = new JButton("Play");
		restartButton = new JButton("Restart");
		playButton.setFont(bodyText);
		playButton.setForeground(textColor);
		playButton.setBackground(tileLight);
		playButton.setToolTipText("Resumes the game");
		restartButton.setFont(bodyText);
		restartButton.setForeground(textColor);
		restartButton.setBackground(tileLight);
		restartButton.setToolTipText("Restarts to a new game");

		JPanel buttons = new JPanel(new GridLayout(1,2));
		buttons.setBackground(backgroundColor);
		buttons.add(playButton); buttons.add(restartButton);
		pauseDialog.add(buttons, BorderLayout.SOUTH);

		// Add listeners once
		playButton.addActionListener(_ -> {
			pauseDialog.setVisible(false);
			engine.togglePause();
		});

		restartButton.addActionListener(_ -> {
			pauseDialog.setVisible(false);
			engine.toggleRestart();
		});

		pauseDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}
	// Draws the entire screen
	public void render(Graphics g) {
		switch(engine.getGameState()) {
			case HOME:
				renderHomeScreen(g);
				break;
			case PAUSE:
				renderGamePaused(g);
				break;
			case RUNNING:
				renderGameRunning(g);
				break;
			case END:
				renderGameOver(g);
		}
	}
	
	private void renderHomeScreen(Graphics g) {
		g.setColor(textColor);
		g.setFont(headerText);
		FontMetrics fm = g.getFontMetrics(g.getFont());
		
		//Instructions Heading
		String msg = "Instructions!";
		int line = screen_height/3 - fm.getHeight();
		g.drawString(msg, (screen_width - fm.stringWidth(msg))/2, line);
		
		//Instructions
		g.setFont(bodyText);
		fm = g.getFontMetrics(g.getFont());
		msg = "Hi there! Welcome to snake game!";
		line += fm.getHeight() + 30;
		g.drawString(msg, (screen_width - fm.stringWidth(msg))/2, line);
	
		msg = "Use the arrow keys or WASD keys to move.";
		line += fm.getHeight() + 10;
		g.drawString(msg, (screen_width - fm.stringWidth(msg))/2, line);
		
		msg = "Hitting spacebar pauses the game,";
		line += fm.getHeight() + 10;
		g.drawString(msg, (screen_width - fm.stringWidth(msg))/2, line);
		
		msg = "and enter restarts the game.";
		line += fm.getHeight() + 10;
		g.drawString(msg, (screen_width - fm.stringWidth(msg))/2, line);
		
		msg = "Hit enter to start playing :D";
		line += fm.getHeight() + 10;
		g.drawString(msg, (screen_width - fm.stringWidth(msg))/2, line);	
	
	}
	
	// Method for rendering game paused screen.
	private void renderGamePaused(Graphics g) {		
		// Update pause dialog data and show it only if not already visible
		int elapsedTime = engine.getElapsedTime()/1000;
		timeLabel.setText("Time Survived: " + elapsedTime + "s.");
		int score = engine.getScore();
		scoreLabel.setText("Score: " + score);
		pauseDialog.setLocationRelativeTo(ownerframe);
		if (!pauseDialog.isShowing()) {
			pauseDialog.setVisible(true);
		}
	}
	
	// Method for rendering game over screen.
	private void renderGameOver(Graphics g) {
		g.setColor(backgroundColor);
		int rectX = screen_width/2 - 5*UNIT_SIZE;
		int rectY = screen_height/2 - 5*UNIT_SIZE;
		g.fillRoundRect(rectX,rectY,10*UNIT_SIZE,10*UNIT_SIZE,arcWidth*10,arcHeight*10);
		
		g.setColor(Color.WHITE);
		g.setFont(headerText);		
		FontMetrics fm = g.getFontMetrics(g.getFont());
		String msg = "GAME OVER";
		
		g.drawString(msg, (screen_width-fm.stringWidth(msg))/2, rectY + 3*UNIT_SIZE);
		
		g.setFont(bodyText);
		msg = "Score: " + engine.getScore(); 
		
	    g.drawString(msg, rectX + UNIT_SIZE, rectY + 6*UNIT_SIZE);
	    msg = "You survived: " + (engine.getFinishedTime()/1000) + "s";	    
	    g.drawString(msg, rectX + UNIT_SIZE, rectY + 9*UNIT_SIZE);
	}
	
	// Method for rendering the playable screen.
	private void renderGameRunning(Graphics g) {
		Snake s = engine.getSnake();
		Apple a = engine.getApple();
		for(int rows = 0; rows<screen_width/UNIT_SIZE;rows++) {
			for(int cols = 0; cols<screen_height/UNIT_SIZE;cols++) {
				if((rows + cols)%2 == 0) {
					g.setColor(tileDark);
				}
				else {
					g.setColor(tileLight);
				}
				g.fillRect(rows*UNIT_SIZE, cols*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
			}
		}		
		drawSnake(g, s.getBody());
		drawApple(g, a.getApple());
	}
	
	// Method to render the snake. Accepts Graphics objects and a Deque of Points that represents the snake body.
	private void drawSnake(Graphics g, Deque<Point> body) {
		if(body == null || body.isEmpty()) {return;}
		int magic = 50;
		Iterator<Point> b = body.iterator();
		if(b.hasNext()) {
			Point p = b.next();
			g.setColor(snakeHeadColor);
			g.fillRoundRect((int)p.getX()*UNIT_SIZE, (int)p.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,arcWidth,arcHeight);
			
			if(engine.getScore()%magic != 0 || engine.getScore() == 0) {
				while(b.hasNext()) {
					g.setColor(snakeBodyColor);
					Point s = b.next();
					g.fillRoundRect((int)s.getX()*UNIT_SIZE, (int)s.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,arcWidth,arcHeight);
				}
			}
			else {
				while(b.hasNext()) {
					int rCol = colorRand.nextInt(255);
					int gCol = colorRand.nextInt(255);
					int bCol = colorRand.nextInt(255);
					g.setColor(new Color(rCol, gCol, bCol));
					Point s = b.next();
					g.fillRoundRect((int)s.getX()*UNIT_SIZE, (int)s.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,arcWidth,arcHeight);
				}
				
			}
		}
	}
	
	// Method to render apple. Accepts Graphic object and Point object for location.
	private void drawApple(Graphics g, Point p) {
		g.setColor(appleColor);
		g.fillRoundRect((int)p.getX()*UNIT_SIZE, (int)p.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, arcWidth, arcHeight);
	}
}
