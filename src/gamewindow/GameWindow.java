package gamewindow;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import entity.*;
import sounds.*;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;


// Central game panel where snake and apples are created
class GameEngine{
	enum GameState{HOME, PAUSE, RUNNING, END}
	GameState currentState;
	private Apple apple;
	private Snake snake;
	private Random rand;
	private char direction = 'R';
	private int delay, delaybyval=3, score, pausedTime, elapsedTime, finishedTime;
	private long startMillis;
	private static final int UNIT_SIZE = 25;
	private Timer gameTimer;
	private Timer countdownTimer;
	private Timer restartTimer;
	
	GameEngine(){
		currentState = GameState.HOME;
		Background.playMusic(getClass().getResource("/gamewindow/resources/snakegameambient.wav"));
		rand = new Random();
		
		int startX = rand.nextInt(10, GamePanel.SCREEN_WIDTH/UNIT_SIZE - 10);
		int startY = rand.nextInt(1, GamePanel.SCREEN_HEIGHT/UNIT_SIZE);
		snake = new Snake(startX, startY);	
		apple = new Apple();
		
		restartTimer = new Timer(1500, e -> {
            startGame();
            restartTimer.stop();
        });
        restartTimer.setRepeats(false);
	}
	
	
	public void setGameState(GameState state) {	this.currentState = state;} 	// Sets new game state for updates
	public GameState getGameState() {return currentState;}						// Returns the current game state
	
	public void setTimer(Timer timer) {gameTimer = timer;}
	
	// General function to update the game
	public void update(){
		if(currentState == GameState.RUNNING) {
			elapsedTime = (int)(System.currentTimeMillis() - startMillis - pausedTime);
			move();
			if(checkEaten(snake.getHead())) {
				score++;
				increaseSpeed();
				int gridWidth = GamePanel.SCREEN_WIDTH/UNIT_SIZE;
				int gridHeight = GamePanel.SCREEN_HEIGHT/UNIT_SIZE;
				int maxAttempts = 1000;
				int attempts = 0;
				do {
					apple.generateApples(gridWidth, gridHeight);
					attempts++;
					if(attempts >= maxAttempts) {
						System.out.println("ERROR: Could not find valid apple position!");
					}
				}while(snake.contains(apple.getApple()));
			}
		}
	}
	
	// Changes direction of snake as necessary
	public void changeDirection(char nextDirection) {
		switch(direction) {
		case 'L':
			if(nextDirection != 'R') {direction = nextDirection;}
			break;
		case 'R':
			if(nextDirection != 'L') {direction = nextDirection;}
			break;
		case 'U':
			if(nextDirection != 'D') {direction = nextDirection;}
			break;
		case 'D':
			if(nextDirection != 'U') {direction = nextDirection;}
			break;
		default:
			break;
		}
	}
	
	// Toggles the game state to pause or otherwise
	public void togglePause(){
		pauseGame();
	}
	
	// Toggles restart: HOME/END -> RUNNING
	public void toggleRestart() {
		if(currentState == GameState.RUNNING || currentState == GameState.PAUSE) {
			endGame();
			restartTimer.start();
		}
		else if(currentState == GameState.END) {
			restartTimer.stop();
			Background.playMusic(getClass().getResource("/gamewindow/resources/snakegameambient.wav"));
			startGame();
		}
		else  {
			Background.playMusic(getClass().getResource("/gamewindow/resources/snakegameambient.wav"));
			startGame();			
		}
	}
	
	// HELPER FUNCTIONS
	
	// Starts the game
	private void startGame() {
		if(restartTimer != null) {
			restartTimer.stop();
		}
		
		score = 0;
		delay = 150;
		direction = 'R';
		pausedTime = 0;
		elapsedTime = 0;
		finishedTime = 0;
		int startX = rand.nextInt(10, GamePanel.SCREEN_WIDTH/UNIT_SIZE - 10);
		int startY = rand.nextInt(1, GamePanel.SCREEN_HEIGHT/UNIT_SIZE);
		snake = new Snake(startX, startY);	
		apple = new Apple();
		setGameState(GameState.RUNNING);
		startMillis = System.currentTimeMillis();
		
		if(gameTimer!=null) {
			gameTimer.start();
		}
	}
	
	// Keeps the game in pause
	private void pauseGame() {
		if(currentState == GameState.HOME || currentState == GameState.END) {return;}

		if(currentState == GameState.PAUSE) {
			pausedTime = (int)(System.currentTimeMillis() - startMillis - elapsedTime);
			setGameState(GameState.RUNNING);
		}
		else {setGameState(GameState.PAUSE);}
	}
	
	// Ends the game
	private void endGame() {
		finishedTime = elapsedTime;
		Background.stop();
		setGameState(GameState.END);
	}
	
	// Check only for self-collisions, collisions to the walls	
	private boolean checkCollisions(Point head) {
		int gridWidth = GamePanel.SCREEN_WIDTH/UNIT_SIZE;
		int gridHeight = GamePanel.SCREEN_HEIGHT/UNIT_SIZE;
		
		if(snake.contains(head)) {return true;}	// Checks for self collision
		if(head.getX() >= gridWidth || head.getX() <0) {return true;}	
		if(head.getY() >= gridHeight || head.getY() <0) {return true;}
		return false;
	}
	
	private Point computeNewHead() {
		Point newHead;
		int dx=0, dy=0;
		switch(direction) {
		case 'L':
			dx = -1;
			break;
		case 'R':
			dx = 1;
			break;
		case 'U':
			dy = -1;
			break;
		case 'D':
			dy = 1;
			break;
		}
		newHead = new Point((int)snake.getHead().getX() + dx, (int)snake.getHead().getY() + dy);
		return newHead; 
	}
	
	// Checks if apple is eaten
	private boolean checkEaten(Point newHead) {
		if (newHead.equals(apple.getApple())){
			return true;
		}
		return false;
	}
	
	// Causes the snake to move
	private void move() {
		if(snake == null || snake.getHead() == null) {return;}
		Point newHead = computeNewHead();		
		if(!checkEaten(newHead)) {
			snake.removeTail();
		}
		if(checkCollisions(newHead)) {
			endGame();
			return;
		};
		snake.addHead(newHead);		
	}
	
	// Increases speed
	private void increaseSpeed() {
		if(delay>=30) {
			delay = delay - delaybyval;
		}
	}
	
	// Getter methods (excluding getGameState())
	public int getScore() {return score;}					// Returns score
	public int getElapsedTime() {return elapsedTime;}		// Returns the running time
	public int getFinishedTime() {return finishedTime;}		// Returns the end time
	public int getDelay() {return delay;}					// Returns delay for swing timer
	public Snake getSnake() {return snake;}					// Returns the snake: for renders
	public Apple getApple() {return apple;}					// Returns the apple: for renders
}

class GameRenderer{
	private GameEngine engine;
	private int UNIT_SIZE = 25;
	private int arcWidth = 6, arcHeight = 6;
	//appleIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/appleformattedfinal.png")).getImage();
	//snakeIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/snakehead.png")).getImage();
			
	GameRenderer(GameEngine engine){
		this.engine = engine;
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
		g.setColor(new Color(0, 169, 165));
		g.setFont(new Font("ARIAL",Font.BOLD+Font.ITALIC,32));
		FontMetrics fm = g.getFontMetrics(g.getFont());
		
		//Instructions Heading
		String msg = "Instructions!";
		int line = GamePanel.SCREEN_HEIGHT/2 - fm.getHeight() - 20;
		g.drawString(msg, (GamePanel.SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		//Instructions
		g.setFont(new Font("Comic Sans MS",Font.BOLD,24));
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
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		
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
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 36));		
		FontMetrics fm = g.getFontMetrics(g.getFont());
		String msg = "GAME OVER";
		
		g.drawString(msg, (GamePanel.SCREEN_WIDTH-fm.stringWidth(msg))/2, rectY + 3*UNIT_SIZE);
		
		g.setFont(new Font("Arial", Font.BOLD, 24));
		msg = "Score: " + engine.getScore(); 
		
	    g.drawString(msg, rectX + UNIT_SIZE, rectY + 6*UNIT_SIZE);
	    msg = "You survived: " + (engine.getFinishedTime()/1000) + "s";	    
	    g.drawString(msg, rectX + UNIT_SIZE, rectY + 9*UNIT_SIZE);
	}
	private void renderGameRunning(Graphics g) {
		Snake s = engine.getSnake();
		Apple a = engine.getApple();
		for(int i=0;i<GamePanel.SCREEN_WIDTH/UNIT_SIZE;i++) {
			g.setColor(new Color(40,40,40));
			g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, GamePanel.SCREEN_HEIGHT);
		}
		
		for(int i=0;i<GamePanel.SCREEN_HEIGHT/UNIT_SIZE;i++) {
			g.setColor(new Color(40,40,40));
			g.drawLine(0, i*UNIT_SIZE, GamePanel.SCREEN_WIDTH, i*UNIT_SIZE);
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
			g.setColor(new Color(0,128,0));
			g.fillRoundRect((int)p.getX()*UNIT_SIZE, (int)p.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,arcWidth,arcHeight);
			
			if(engine.getScore()%magic != 0 || engine.getScore() == 0) {
				while(b.hasNext()) {
					g.setColor(Color.GREEN);
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
		g.setColor(Color.RED);
		g.fillRoundRect((int)p.getX()*UNIT_SIZE, (int)p.getY()*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, arcWidth, arcHeight);
	}
}

class InputHandler extends KeyAdapter{
	GameEngine engine;
	char nextDirection;
	InputHandler(GameEngine engine){
		this.engine = engine;
	}
	public void keyPressed(KeyEvent event) {
		switch(event.getKeyCode()) {
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			engine.changeDirection('L');
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			engine.changeDirection('R');
			break;
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			engine.changeDirection('U');
			break;
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			engine.changeDirection('D');
			break;
		case KeyEvent.VK_SPACE:
			engine.togglePause();
			break;
		case KeyEvent.VK_ENTER:
			engine.toggleRestart();
			return;
		}
	}
}


class GamePanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	final static int SCREEN_WIDTH = 600;
	final static int SCREEN_HEIGHT = 600;
	GameEngine engine;
	GameRenderer renderer;	
	Timer timer;
	
	int delay;
	long startMillis;
	char nextDirection;
	JLabel time, currentscore;
	
	GamePanel(JLabel time, JLabel currentscore){
		this.time = time;
		this.currentscore = currentscore;
		this.delay = 150;
		setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		setBackground(new Color(9, 35, 39));
		setFocusable(true);
		
		timer = new Timer(delay,this);
		
		engine = new GameEngine();
		engine.setTimer(timer);
		renderer = new GameRenderer(engine);
		addKeyListener(new InputHandler(engine));
		
	
	}
	public void actionPerformed(ActionEvent ae) {
		engine.update();
		currentscore.setText("Current score: " + engine.getScore());
		time.setText("Time Running: " + engine.getElapsedTime() + "ms");
		
		if(timer.getDelay() != engine.getDelay()) {
			timer.setDelay(engine.getDelay());
		}
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		renderer.render(g);
	}
	
} 
public class GameWindow extends JFrame{
	private static final long serialVersionUID = 2821681725216657707L;
	JLabel time,currentscore;
	JButton restart, pause;
	GamePanel gp;
	GameWindow(){
		setTitle("Snake Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(new Color(11, 83, 81));
		setLayout(new BorderLayout(10,10));	
		
		currentscore = new JLabel("Current score: 0");
		currentscore.setForeground(Color.WHITE);
		time = new JLabel("Time Running: 0ms");
		time.setForeground(Color.WHITE);
		restart = new JButton("Restart");
		restart.setBackground(new Color(0, 169, 165));
		restart.setForeground(Color.WHITE);
		restart.setToolTipText("Press ENTER to restart.");
		pause = new JButton("Pause");
		pause.setBackground(new Color(0, 169, 165));
		pause.setForeground(Color.WHITE);
		pause.setToolTipText("Press SPACEBAR to pause.");
		
		//Score, run time information etc up north
		JPanel informatics = new JPanel();
		informatics.add(currentscore);informatics.add(time); 
		informatics.add(restart); informatics.add(pause);
		informatics.setBackground(new Color(11, 83, 81));
		add(informatics, BorderLayout.NORTH);
		
		//Adding game panel to center
		gp = new GamePanel(time,currentscore);
		add(gp, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		gp.requestFocusInWindow();
		
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.engine.toggleRestart();
				gp.requestFocusInWindow();
				repaint();
			}
		});
		
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.engine.togglePause();
				gp.requestFocusInWindow();
				repaint();
			}
		});
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GameWindow();
	}
}
