package gamewindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;

import entity.Apple;
import entity.Snake;

public class Renderer{
	private Engine engine;
	private static int UNIT_SIZE = 25;
	private static int arcWidth = 6, arcHeight = 6;
	private static Color snakeHeadColor, snakeBodyColor, appleColor, tileLight, tileDark, homeColor, pauseColor, textColor;
	private static Font headerText, bodyText;
	
	//appleIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/appleformattedfinal.png")).getImage();
	//snakeIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/snakehead.png")).getImage();
			
	Renderer(Engine engine){
		this.engine = engine;
		
		// Colors
		snakeHeadColor = Color.decode("#1A936F");
		snakeBodyColor = Color.decode("#88D498");
		appleColor = Color.decode("#ED254E");
		tileLight = Color.decode("#ACB3C3");
		tileDark = Color.decode("#96ADC5");
		textColor = new Color(0, 169, 165);
		homeColor = new Color(0,168,160);
		pauseColor = Color.gray;
		
		// Font
		headerText = new Font("Comic Sans MS", Font.BOLD+Font.ITALIC, 36);
		bodyText = new Font("Comic Sans MS",Font.BOLD,24);
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
		int line = GamePanel.SCREEN_HEIGHT/3 - fm.getHeight();
		g.drawString(msg, (GamePanel.SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		//Instructions
		g.setFont(bodyText);
		fm = g.getFontMetrics(g.getFont());
		msg = "Hi there! Welcome to snake game!";
		line += fm.getHeight() + 30;
		g.drawString(msg, (GamePanel.SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
	
		msg = "Use the arrow keys or WASD keys to move.";
		line += fm.getHeight() + 10;
		g.drawString(msg, (GamePanel.SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		msg = "Hitting spacebar pauses the game,";
		line += fm.getHeight() + 10;
		g.drawString(msg, (GamePanel.SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		msg = "and enter restarts the game.";
		line += fm.getHeight() + 10;
		g.drawString(msg, (GamePanel.SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		msg = "Hit enter to start playing :D";
		line += fm.getHeight() + 10;
		g.drawString(msg, (GamePanel.SCREEN_WIDTH - fm.stringWidth(msg))/2, line);	
	
	}
	private void renderGamePaused(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(headerText);
		
		FontMetrics fm = g.getFontMetrics(g.getFont());
		String msg = "GAME PAUSED";
		
		g.drawString(msg, (GamePanel.SCREEN_WIDTH-fm.stringWidth(msg))/2, GamePanel.SCREEN_HEIGHT/2);
	}
	private void renderGameOver(Graphics g) {
		g.setColor(new Color(11, 83, 81));
		int rectX = GamePanel.SCREEN_WIDTH/2 - 5*UNIT_SIZE;
		int rectY = GamePanel.SCREEN_HEIGHT/2 - 5*UNIT_SIZE;
		g.fillRoundRect(rectX,rectY,10*UNIT_SIZE,10*UNIT_SIZE,6,6);
		
		g.setColor(Color.WHITE);
		g.setFont(headerText);		
		FontMetrics fm = g.getFontMetrics(g.getFont());
		String msg = "GAME OVER";
		
		g.drawString(msg, (GamePanel.SCREEN_WIDTH-fm.stringWidth(msg))/2, rectY + 3*UNIT_SIZE);
		
		g.setFont(bodyText);
		msg = "Score: " + engine.getScore(); 
		
	    g.drawString(msg, rectX + UNIT_SIZE, rectY + 6*UNIT_SIZE);
	    msg = "You survived: " + (engine.getFinishedTime()/1000) + "s";	    
	    g.drawString(msg, rectX + UNIT_SIZE, rectY + 9*UNIT_SIZE);
	}
	private void renderGameRunning(Graphics g) {
		Snake s = engine.getSnake();
		Apple a = engine.getApple();
		for(int rows = 0; rows<GamePanel.SCREEN_WIDTH/UNIT_SIZE;rows++) {
			for(int cols = 0; cols<GamePanel.SCREEN_HEIGHT/UNIT_SIZE;cols++) {
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
					Random rand = new Random();
					g.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
					Point s = b.next();
					g.fillRoundRect((int)s.getX()*UNIT_SIZE, (int)s.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,arcWidth,arcHeight);
				}
				
			}
		}
	}
	private void drawApple(Graphics g, Point p) {
		g.setColor(appleColor);
		g.fillRoundRect((int)p.getX()*UNIT_SIZE, (int)p.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, arcWidth, arcHeight);
	}
}
