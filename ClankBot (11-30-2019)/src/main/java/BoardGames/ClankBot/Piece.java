package BoardGames.ClankBot;

import java.io.File;

public class Piece {
	public int x;
	public int y;
	public File pic;
	public String type;
	
	public Piece(File pic, String type, int x, int y) {
		this.pic = pic;
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getType() {
		return type;
	}
	
	public File getPic() {
		return pic;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setPic(File pic) {
		this.pic = pic;
	}
}
