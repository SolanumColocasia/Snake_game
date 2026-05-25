package gamewindow;

import java.awt.Point;
import java.util.Random;
import javax.swing.Timer;
import entity.Apple;
import entity.Snake;
import sounds.Background;

public class Engine{
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
	//private Timer countdownTimer;
	private Timer restartTimer;
	
	Engine(){
		currentState = GameState.HOME;
		Background.playMusic(getClass().getResource("/gamewindow/resources/snakegameambient.wav"));
		rand = new Random();
		
		int startX = rand.nextInt(10, GamePanel.SCREEN_WIDTH/UNIT_SIZE - 10);
		int startY = rand.nextInt(1, GamePanel.SCREEN_HEIGHT/UNIT_SIZE);
		snake = new Snake(startX, startY);	
		apple = new Apple();
		
		restartTimer = new Timer(1500, _ -> {
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
