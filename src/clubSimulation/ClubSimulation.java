//M. M. Kuttel 2023 mkuttel@gmail.com

package clubSimulation;
// the main class, starts all threads
import javax.swing.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClubSimulation {
	static int noClubgoers=100;
   	static int frameX=400;
	static int frameY=500;
	static int yLimit=400;
	static int gridX=15; //number of x grids in club - default value if not provided on command line
	static int gridY=15; //number of y grids in club - default value if not provided on command line
	static int max=30; //max number of customers - default value if not provided on command line
	
	static Clubgoer[] patrons; // array for customer threads
	static PeopleLocation [] peopleLocations;  //array to keep track of where customers are
	
	static PeopleCounter tallys; //counters for number of people inside and outside club

	static ClubView clubView; //threaded panel to display terrain
	static ClubGrid clubGrid; // club grid
	static CounterDisplay counterDisplay ; //threaded display of counters
	
	private static int maxWait=800; //for the slowest customer
	private static int minWait=200; //for the fastest cutomer

	public static AtomicBoolean running = new AtomicBoolean(true);
	private static int pauseState = 1;
	public static CountDownLatch latch;

	public static void setupGUI(int frameX,int frameY,int [] exits) {
		// Frame initialize and dimensions
    	JFrame frame = new JFrame("club animation"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
      	g.setSize(frameX,frameY);
 	    
		clubView = new ClubView(peopleLocations, clubGrid, exits);
		clubView.setSize(frameX,frameY);
	    g.add(clubView);
	    
	    //add all the counters to the panel
	    JPanel txt = new JPanel();
	    txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
	    JLabel maxAllowed =new JLabel("Max: " + tallys.getMax() + "    ");
	    JLabel caught =new JLabel("Inside: " + tallys.getInside() + "    ");
	    JLabel missed =new JLabel("Waiting:" + tallys.getWaiting()+ "    ");
	    JLabel scr =new JLabel("Left club:" + tallys.getLeft()+ "    ");    
	    txt.add(maxAllowed);
	    txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);
	    g.add(txt);
	    counterDisplay = new CounterDisplay(caught, missed,scr,tallys);      //thread to update score
       
	    //Add start, pause and exit buttons
	    JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
        JButton startB = new JButton("Start");
        
		// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e)  {
			    // open the latch, to allow all threads to start running
				latch.countDown();
		    }
		   });
			
			final JButton pauseB = new JButton("Pause ");;
			
			// add the listener to the jbutton to handle the "pressed" event
			pauseB.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		    		//pause or resume the programme, by switching the state of the atomic boolean
					// notify all waitin threads once the boolean has been changed
					synchronized (running){
						switchRunState();
						running.notifyAll();
					}
					//GUI Code to change button labels correctly
					pauseState *= -1;
					if(pauseState == -1){
						pauseB.setText("Resume");
					}
					else{ pauseB.setText("Pause");}
		      }
		    });
			
		JButton endB = new JButton("Quit");
				// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		b.add(startB);
		b.add(pauseB);
		b.add(endB);
		
		g.add(b);
    	
      	frame.setLocationRelativeTo(null);  // Center window on screen.
      	frame.add(g); //add contents to window
        frame.setContentPane(g);     
        frame.setVisible(true);	
	}
	
	public static void main(String[] args) throws InterruptedException {
		//Create new latch object to control the exectution of threads
		latch = new CountDownLatch(1);
		//deal with command line arguments if provided
		if (args.length==4) {
			noClubgoers=Integer.parseInt(args[0]);  //total people to enter room
			gridX=Integer.parseInt(args[1]); // No. of X grid cells  
			gridY=Integer.parseInt(args[2]); // No. of Y grid cells  
			max=Integer.parseInt(args[3]); // max people allowed in club
		}
		
		//hardcoded exit doors
		int [] exit = {0,(int) gridY/2-1};  //once-cell wide door on left
				
	    tallys = new PeopleCounter(max); //counters for people inside and outside club
		clubGrid = new ClubGrid(gridX, gridY, exit,tallys); //setup club with size and exitsand maximum limit for people    
		Clubgoer.club = clubGrid; //grid shared with class
	   
	    peopleLocations = new PeopleLocation[noClubgoers];
		patrons = new Clubgoer[noClubgoers];
		
		Random rand = new Random();

        for (int i=0;i<noClubgoers;i++) {
        		peopleLocations[i]=new PeopleLocation(i);
        		int movingSpeed=(int)(Math.random() * (maxWait-minWait)+minWait); //range of speeds for customers
    			patrons[i] = new Clubgoer(i,peopleLocations[i],movingSpeed);
    		}
		           
		setupGUI(frameX, frameY,exit);  //Start Panel thread - for drawing animation
        //start all the threads
		Thread t = new Thread(clubView); 
      	t.start();
      	//Start counter thread - for updating counters
      	Thread s = new Thread(counterDisplay);  
      	s.start();
      	
      	for (int i=0;i<noClubgoers;i++) {
			patrons[i].start();
		}
 	}

	//Its a getter. its not that deep
	public static AtomicBoolean getRunState(){
		return running;
	}

	//Pause or resume the program, by switching the atomic boolean
	public static synchronized void switchRunState(){
		boolean state = getRunState().get();
		state= !state;
		running.set(state);
	}
}
