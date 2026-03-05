package gamewindow;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import entity.*;
import sounds.*;
import java.util.Random;
enum GameState{HOME, PAUSE, RUNNING, END}

// Central game panel where snake and apples are created
class GamePanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	GameState state;
	final int SCREEN_WIDTH = 600;
	final int SCREEN_HEIGHT = 600;
	Entity apple;
	Image appleIcon, snakeIcon;
	Timer timer;
	Random rand;
	Snake s;
	int delay, score, elapsedTime, scoreperapple = 50, delaybyval = 3;
	long startMillis;
	char nextDirection;
	JLabel time, currentscore;
	
	GamePanel(JLabel time, JLabel currentscore){
		this.time = time;
		this.currentscore = currentscore;
		setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		setBackground(new Color(9, 35, 39));
		setFocusable(true);
		addKeyListener(new MyKeyAdapter());
		rand = new Random();
		appleIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/appleformattedfinal.png")).getImage();
		snakeIcon = new ImageIcon(getClass().getResource("/gamewindow/resources/snakehead.png")).getImage();
		state = GameState.HOME;
		Background.playMusic(getClass().getResource("/gamewindow/resources/snakegameambient.wav"));
		score = 0;
		delay = 150;
		timer = new Timer(delay,this);	
		//startGame();		
	}
	
	public void startGame() {
		timer.stop();
		score = 0;
		nextDirection = 'R';
		startMillis=System.currentTimeMillis();
		delay = 150;
		elapsedTime = 0;
	    time.setText("Time Running: 0ms");
		generateApples();
		int gridX = rand.nextInt((SCREEN_WIDTH-Entity.UNIT_SIZE)/Entity.UNIT_SIZE);
		int gridY = rand.nextInt((SCREEN_HEIGHT-Entity.UNIT_SIZE)/Entity.UNIT_SIZE);
		s = new Snake(gridX*Entity.UNIT_SIZE,gridY*Entity.UNIT_SIZE,'R');
		timer.start();
		timer.setDelay(delay);
		state = GameState.RUNNING;
		requestFocusInWindow();
	}
	
	public void generateApples() {
		int appleX = rand.nextInt((int)(SCREEN_WIDTH/Entity.UNIT_SIZE))*Entity.UNIT_SIZE;
		int appleY = rand.nextInt((int)(SCREEN_HEIGHT/Entity.UNIT_SIZE))*Entity.UNIT_SIZE;
		
		apple = new Entity(appleX,appleY,appleIcon);
	}
	
	public void checkEaten() {
		if(apple.intersects(s.getHead())) {
			s.appleEaten();
			score = score + scoreperapple;
			increaseSpeed();
			generateApples();
		}
	}
	
	public void increaseSpeed() {
		if(delay>=30) {
			delay = delay - delaybyval;
			timer.setDelay(delay);
		}
	}
	
	public void move() {
		s.setDirection(nextDirection);
		s.move();
	}
	
	public void checkCollision() {
		if(s.selfCollision()) {state = GameState.END;}
		
		Entity head = s.getHead();
		if(head.getX()>=SCREEN_WIDTH  || head.getX()<0) {state = GameState.END;}
		if(head.getY()>=SCREEN_HEIGHT || head.getY()<0) {state = GameState.END;}
		
		if(state == GameState.END) {
			Background.stop();
			timer.stop();}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		switch(state) {
		case HOME:
			homescreen(g);
			break;
		case RUNNING:
			draw(g);
			break;
		case PAUSE:
			pausescreen(g);
			break;
		case END:
			endGame(g);			
		}
				
	}
	
	public void homescreen(Graphics g) {
		g.setColor(new Color(0, 169, 165));
		g.setFont(new Font("Comic Sans MS",Font.BOLD+Font.ITALIC,32));
		FontMetrics fm = getFontMetrics(g.getFont());
		
		//Instructions Heading
		String msg = "Instructions!";
		int line = SCREEN_HEIGHT/2 - fm.getHeight() - 20;
		g.drawString(msg, (SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		//Instructions
		g.setFont(new Font("Comic Sans MS",Font.BOLD,24));
		fm = getFontMetrics(g.getFont());
		msg = "Hi there! Welcome to snake game!";
		line += fm.getHeight() + 30;
		g.drawString(msg, (SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
	
		msg = "Use the arrow keys or WASD keys to move.";
		line += fm.getHeight() + 10;
		g.drawString(msg, (SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		msg = "Hitting spacebar pauses the game,";
		line += fm.getHeight() + 10;
		g.drawString(msg, (SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		msg = "and enter restarts the game.";
		line += fm.getHeight() + 10;
		g.drawString(msg, (SCREEN_WIDTH - fm.stringWidth(msg))/2, line);
		
		msg = "Hit enter to start playing :D";
		line += fm.getHeight() + 10;
		g.drawString(msg, (SCREEN_WIDTH - fm.stringWidth(msg))/2, line);	
		
	}
	
	public void pausescreen(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
		
		FontMetrics fm = getFontMetrics(g.getFont());
		String msg = "GAME PAUSED";
		
		g.drawString(msg, (SCREEN_WIDTH-fm.stringWidth(msg))/2, SCREEN_HEIGHT/2);
	}

	public void draw(Graphics g) {
			//Grid for easy view
			for(int i=0; i<SCREEN_WIDTH/Entity.UNIT_SIZE;i++) {
				g.setColor(new Color(40, 40, 40));
				g.drawLine(i*Entity.UNIT_SIZE, 0, i*Entity.UNIT_SIZE, SCREEN_HEIGHT);
			}
			
			for(int i=0; i<SCREEN_HEIGHT/Entity.UNIT_SIZE;i++) {
				g.setColor(new Color(40, 40, 40));
				g.drawLine(0, i*Entity.UNIT_SIZE, SCREEN_WIDTH, i*Entity.UNIT_SIZE);
			}
			//Entity 
			g.drawImage(apple.getIcon(),(int)apple.getX(), (int)apple.getY(), Entity.UNIT_SIZE, Entity.UNIT_SIZE,null);		
			s.draw(g,score);
	}
	
	public void pauseGame() {
		if(state == GameState.HOME || state == GameState.END) return;
		
		if(state == GameState.PAUSE) {
			state = GameState.RUNNING;
			timer.start();
		}
		else if(state == GameState.RUNNING) {
			state = GameState.PAUSE;
			timer.stop();
		}
			
	}
	public void endGame(Graphics g) {
		g.setColor(new Color(11, 83, 81));
		int rectX = SCREEN_WIDTH/2 - 5*Entity.UNIT_SIZE;
		int rectY = SCREEN_HEIGHT/2 - 5*Entity.UNIT_SIZE;
		g.fillRoundRect(rectX,rectY,10*Entity.UNIT_SIZE,10*Entity.UNIT_SIZE,6,6);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 36));		
		FontMetrics fm = getFontMetrics(g.getFont());
		String msg = "GAME OVER";
		
		g.drawString(msg, (SCREEN_WIDTH-fm.stringWidth(msg))/2, rectY + 3*Entity.UNIT_SIZE);
		
		g.setFont(new Font("Arial", Font.BOLD, 24));
		msg = "Score: " + score; 
		
	    g.drawString(msg, rectX + Entity.UNIT_SIZE, rectY + 6*Entity.UNIT_SIZE);
	    msg = "You survived: " + (elapsedTime/1000) + "s";	    
	    g.drawString(msg, rectX + Entity.UNIT_SIZE, rectY + 9*Entity.UNIT_SIZE);	    
	}
	
	public void actionPerformed(ActionEvent ae) {
		if(state == GameState.RUNNING) {
			elapsedTime = (int)(System.currentTimeMillis() - startMillis);
			time.setText("Time Running: " +  elapsedTime + "ms");
			currentscore.setText("Current score: " + score);
			
			move();
			checkEaten();
			checkCollision();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		
		public void keyPressed(KeyEvent ke) {
			switch(ke.getKeyCode()) {
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				if(nextDirection != 'R') {
					nextDirection = 'L';
				}
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				if(nextDirection !='L') {
					nextDirection = 'R';
				}
				break;
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				if(nextDirection !='D') {
					nextDirection = 'U';
				}
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				if(nextDirection !='U') {
					nextDirection = 'D';
				}
				break;
			case KeyEvent.VK_SPACE:
				pauseGame();
				repaint();
				break;
			case KeyEvent.VK_ENTER:
				if(state == GameState.END || state == GameState.HOME) {startGame();}				
				return;
			}
		}
	}
} 
public class GameWindow extends JFrame{
	private static final long serialVersionUID = 2821681725216657707L;
	JLabel time,currentscore;
	JButton restart, pause;
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
		pause = new JButton("Pause");
		pause.setBackground(new Color(0, 169, 165));
		pause.setForeground(Color.WHITE);
		
		//Score, run time information etc up north
		JPanel informatics = new JPanel();
		informatics.add(currentscore);informatics.add(time); 
		informatics.add(restart); informatics.add(pause);
		informatics.setBackground(new Color(11, 83, 81));
		add(informatics, BorderLayout.NORTH);
		
		//Adding game panel to center
		GamePanel gp = new GamePanel(time,currentscore);
		add(gp, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		gp.requestFocusInWindow();
		
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.startGame();
				repaint();
			}
		});
		
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.pauseGame();
				repaint();
			}
		});
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GameWindow();
	}

}
