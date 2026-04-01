package entity;
import java.util.ArrayDeque;
import java.util.Deque;
import java.awt.Point;
public class Snake {
	private Deque<Point> body;
	private int bodySize;
	private int UNIT_SIZE = 25;

	public Snake(int x, int y){
		bodySize = 8;
		body = new ArrayDeque<>();
		for(int i = 0;i<bodySize;i++) {
			body.add(new Point(x/UNIT_SIZE - i ,y/UNIT_SIZE));
		}		
	}
	public void addHead(Point newHead) {
		body.addFirst(newHead);
	}
	
	public void removeTail() {
		body.removeLast();
	}
	
	public boolean contains(Point p) {
		for(Point s : body) {
			if(s.x == p.x && s.y == p.y) {
				return true;
			}
		}
		return false;
	}
	public Point getHead() {return body.getFirst();}
	public Deque<Point> getBody(){return body;}
}
