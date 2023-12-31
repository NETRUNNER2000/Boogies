//M. M. Kuttel 2023 mkuttel@gmail.com
//Grid for the club

package clubSimulation;

//This class represents the club as a grid of GridBlocks
public class ClubGrid {
	private GridBlock [][] Blocks;
	private final int x;
	private final int y;
	public  final int bar_y;
	
	private GridBlock exit;
	private GridBlock entrance; //hard coded entrance
	private final static int minX =5;//minimum x dimension
	private final static int minY =5;//minimum y dimension
	
	private PeopleCounter counter;
	
	ClubGrid(int x, int y, int [] exitBlocks,PeopleCounter c) throws InterruptedException {
		if (x<minX) x=minX; //minimum x
		if (y<minY) y=minY; //minimum x
		this.x=x;
		this.y=y;
		this.bar_y=y-3;
		Blocks = new GridBlock[x][y];
		this.initGrid(exitBlocks);
		entrance=Blocks[getMaxX()/2][0];
		counter=c;
	}
	
	//initialise the grid, creating all the GridBlocks
	private void initGrid(int []exitBlocks) throws InterruptedException {
		for (int i=0;i<x;i++) {
			for (int j=0;j<y;j++) {
				boolean exit_block=false;
				boolean bar=false;
				boolean dance_block=false;
				if ((i==exitBlocks[0])&&(j==exitBlocks[1])) {exit_block=true;}
				else if (j>=(y-3)) bar=true; 
				else if ((i>x/2) && (j>3) &&(j< (y-5))) dance_block=true;
				//bar is hardcoded two rows before  the end of the club
				Blocks[i][j]=new GridBlock(i,j,exit_block,bar,dance_block);
				if (exit_block) {this.exit = Blocks[i][j];}
			}
		}
	}
	
	public int getMaxX() {
		return x;
	}
	
	public int getMaxY() {
		return y;
	}

	public GridBlock whereEntrance() { 
		return entrance;
	}

	public boolean inGrid(int i, int j) {
		if ((i>=x) || (j>=y) ||(i<0) || (j<0)) 
			return false;
		return true;
	}
	
	public  boolean inPatronArea(int i, int j) {
		if ((i>=x) || (j>bar_y) ||(i<0) || (j<0)) 
			return false;
		return true;
	}
	
	//Only allow new clubgoers into the club if the club has space and nobody is occupying the entrance
	public GridBlock enterClub(PeopleLocation myLocation) throws InterruptedException  {	
		 // block the entract if the club is full or someone is occupying the entrance
			 synchronized(entrance){
				counter.personArrived(); //add to counter of people waiting 
				while(counter.overCapacity()|| !entrance.get(myLocation.getID())){
				entrance.wait();
			}
			entrance.get(myLocation.getID());
			counter.personEntered(); //add to counter
			myLocation.setLocation(entrance); //move onto the entrace block
			myLocation.setInRoom(true);
			return entrance;
		}
	}

	
	//Move clubgoer to new grid block, while checking for entrance notify conditions.
	public GridBlock move(GridBlock currentBlock,int step_x, int step_y,PeopleLocation myLocation) throws InterruptedException {  //try to move in 
		
		int c_x= currentBlock.getX();
		int c_y= currentBlock.getY();
		
		int new_x = c_x+step_x; //new block x coordinates
		int new_y = c_y+step_y; // new block y  coordinates
		
		//restrict i an j to grid
		if (!inPatronArea(new_x,new_y)) {
			//Invalid move to outside  - ignore
			return currentBlock;
		}

		if ((new_x==currentBlock.getX())&&(new_y==currentBlock.getY())) //not actually moving
			return currentBlock;
		 
		GridBlock newBlock = Blocks[new_x][new_y];
		
		if (!newBlock.get(myLocation.getID())) return currentBlock; //stay where you are
		
		//code runs when we are the clubgoer is about to move
		//we check if the current clubgoer is on the entrance. if they are, tell the entrace to open.
		synchronized(entrance){
			
			currentBlock.release(); //must release current block
			if(currentBlock.equals(entrance)){	// check if on entrace, and notify		
				entrance.notifyAll();
			}
			myLocation.setLocation(newBlock);
			return newBlock;
		}			
	} 
	
	//Remove clubgoer from the simulation. This decrememnts the amount of people in the club and
	//increases the counter for people that have left
	//this method must notify the entrace object to open and allow a new thread to enter.
	public void leaveClub(GridBlock currentBlock,PeopleLocation myLocation)   {
		try{
			currentBlock.release();
			counter.personLeft(); //add to counter
			myLocation.setInRoom(false);
			entrance.notifyAll(); //open the entrance and allow a new person in
		}
		catch(Exception e){
			//catch exception. do nothing
		}
	}

	public GridBlock getExit() {
		return exit;
	}

	public GridBlock whichBlock(int xPos, int yPos) {
		if (inGrid(xPos,yPos)) {
			return Blocks[xPos][yPos];
		}
		System.out.println("block " + xPos + " " +yPos + "  not found");
		return null;
	}
	
	public void setExit(GridBlock exit) {
		this.exit = exit;
	}

	public int getBar_y() {
		return bar_y;
	}

}


	

	

