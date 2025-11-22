package entity;
import java.awt.Color;
import java.awt.Image;

public class Entity {
	protected float x,y;
	protected int width,height;
	protected Color color;
	protected Image icon;
	public static int UNIT_SIZE = 25;
	public Entity(float x, float y, Color color){
		this.x = x;
		this.y = y;
		this.color = color;
		width = UNIT_SIZE;
		height = UNIT_SIZE;
	}
	
	public Entity(float x, float y, Image icon){
		this.x = x;
		this.y = y;
		this.icon = icon;
		width = UNIT_SIZE;
		height = UNIT_SIZE;
	}
		
	///Returns true when two entities overlap each other
	public boolean intersects(Entity other) {		
		return (x < other.x+other.width && x + width>other.x && y<other.y+other.height && y+height>other.y);
	}
	
	public float getX() {return x;}
	public float getY() {return y;}
	public Color getColor() {return color;}
	public Image getIcon() {return icon;}

}
