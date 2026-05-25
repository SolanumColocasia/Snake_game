package gamewindow;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class InputHandler extends KeyAdapter{
	Engine engine;
	char nextDirection;
	InputHandler(Engine engine){
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
	Engine engine;
	Renderer renderer;	
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
		
		engine = new Engine();
		engine.setTimer(timer);
		renderer = new Renderer(engine);
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
	Color background = Color.decode("#01161E");
	Color foreground1 = Color.decode("#AEC3B0");
	Color foreground2 = Color.decode("#EFF6E0");
	GameWindow(){
		setTitle("Snake Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(background);
		setLayout(new BorderLayout(10,10));	
		
		currentscore = new JLabel("Current score: 0");
		currentscore.setForeground(foreground2);
		time = new JLabel("Time Running: 0ms");
		time.setForeground(foreground2);
		restart = new JButton("Restart");
		restart.setBackground(new Color(0, 169, 165));
		restart.setForeground(foreground2);
		restart.setToolTipText("Press ENTER to restart.");
		pause = new JButton("Pause");
		pause.setBackground(new Color(0, 169, 165));
		pause.setForeground(foreground2);
		pause.setToolTipText("Press SPACEBAR to pause.");
		
		//Score, run time information etc up north
		JPanel informatics = new JPanel();
		informatics.add(currentscore);informatics.add(time); 
		informatics.add(restart); informatics.add(pause);
		informatics.setBackground(background);
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
