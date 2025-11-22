package entity;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
public class Snake extends Entity {
	char direction;
	ArrayList<Entity> body;
	public int bodySize;
	public int foodEaten;
	
	public Snake(float x, float y, char direction){
		super(x,y,new Color(0, 128, 0));
		this.direction = direction;
		bodySize = 8;
		foodEaten = 0;
		body = new ArrayList<>();
		body.add(new Entity(x,y,new Color(0, 128, 0)));
		for(int i = 1; i<bodySize;i++) {
			body.add(new Entity(x - i*Entity.UNIT_SIZE,y, Color.GREEN));
		}		
	}
	
	public void draw(Graphics g, int score) {
		if(score%500 != 0 || score==0) {
			for(int i = 0;i<bodySize;i++) {
				g.setColor(body.get(i).color);
				g.fillRoundRect((int)body.get(i).x, (int)body.get(i).y, Entity.UNIT_SIZE, Entity.UNIT_SIZE,6,6);
			}
		}
		else {
			Random rand = new Random();
			g.setColor(body.get(0).color);
			g.fillRoundRect((int)body.get(0).x, (int)body.get(0).y, Entity.UNIT_SIZE, Entity.UNIT_SIZE,6,6);
			for(int i = 1;i<bodySize;i++) {
				g.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
				g.fillRoundRect((int)body.get(i).x, (int)body.get(i).y, Entity.UNIT_SIZE, Entity.UNIT_SIZE,6,6);
			}
		}
	}
	
	public void move() {
		for(int i = bodySize-1;i>0;i--) {
			body.get(i).x = body.get(i-1).x; 
			body.get(i).y = body.get(i-1).y; 
		}
		switch(direction) {
		case 'U':
			body.get(0).y = body.get(0).y - Entity.UNIT_SIZE;
			break;
		case 'D':
			body.get(0).y = body.get(0).y + Entity.UNIT_SIZE;
			break;
		case 'L':
			body.get(0).x = body.get(0).x - Entity.UNIT_SIZE;
			break;
		case 'R':
			body.get(0).x = body.get(0).x + Entity.UNIT_SIZE;
			break;
		}
		//Updating head for snake
		x = body.get(0).x;
		y = body.get(0).y;
	}
	
	public void appleEaten() {
		foodEaten++;
		Entity last = body.get(body.size()-1);	
		body.add(new Entity(last.x,last.y,Color.GREEN));
		bodySize++;
	}
	
	public boolean selfCollision() {
		for(int i = bodySize-1;i>0;i--) {
			if(body.get(i).intersects(body.get(0))) {
				return true;
			}
		}
		return false;
	}
	
	public Entity getHead() {return body.get(0);}	
	public char getDirection() {return direction;}
	public void setDirection(char direction) {this.direction = direction;}
}
