package clubSimulation;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.*;

public class PeopleLocation  { // this is a separate class so don't have to access thread
	
	private final int ID; //each person has an ID
	private Color myColor; //colour of the person
	private AtomicBoolean inRoom; //are they in the club?
	private AtomicBoolean arrived; //have they arrived at the club?
	private GridBlock location; //which GridBlock are they on?
	
	PeopleLocation(int ID ) {
		Random rand = new Random();
		float c = rand.nextFloat(); //bit of a hack to get different colours
		myColor = new Color(c, rand.nextFloat(), c);	//only set at beginning	by thread
		inRoom = new AtomicBoolean(false); //not in club
		arrived = new AtomicBoolean(false); //have not arrive outside
		this.ID=ID;
	}
	
	//setter
	public synchronized void setInRoom(boolean in) {
		this.inRoom.set(in);
	}
	
	//getter and setter
	public synchronized boolean getArrived() {
		return arrived.get();
	}
	public synchronized void setArrived() {
		this.arrived.set(true);;
	}

	//getter and setter
	public synchronized GridBlock getLocation() {
		return location;
	}
	public synchronized void setLocation(GridBlock location) {
		this.location = location;
	}

	//getter
	public synchronized int getX() { return location.getX();}	
	
	//getter
	public synchronized int getY() {	return location.getY();	}
	
	//getter
	public synchronized int getID() { return ID;	}

	//getter
	public synchronized boolean inRoom() {
		return inRoom.get();
	}
	//getter and setter
	public synchronized Color getColor() { return myColor; }
	public synchronized void setColor(Color myColor) { this.myColor= myColor; }
}
