package entity;
import java.util.Random;
import java.awt.Point;
public class Apple{
	private Point loc;
	int startX = 2, startY = 17;
	Random rand;
	public Apple() {		
		rand = new Random();
		loc = new Point(startX,startY);
	}
	
	public void generateApples(int horizontal, int vertical) {
		int x = rand.nextInt(Math.max(1, horizontal - 2)) + 1;
		int y = rand.nextInt(Math.max(1, vertical - 2)) + 1;
		loc.setLocation(x, y);
	}
	
	public Point getApple() {return loc;}
	
}
