package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */


public class ElevatorScene {

	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	public static final int VISUALIZATION_WAIT_TIME = 50;  //milliseconds

	// to bind (this) to a varible we creata a instance of Elevator scene
	public static ElevatorScene that;
	public ArrayList <Thread> Elevators;

	public static ArrayList<Semaphore> WaitingForElevator;


	public ArrayList<Semaphore[]> WaitingToExitElevator;

	public static Semaphore currentFloorMutex;
	public static Semaphore exitedCountMutex;
	public static Semaphore whichElevatorMutex;
	public static Semaphore personsInElevatorMutex;
	public static Semaphore personsWaitingMutex;

	public ArrayList<Integer> personsInElevator;
	public ArrayList<Integer> personsWaitingOnFloor;
	public ArrayList<Integer> exitedCount;
	public ArrayList<Integer> currentFloor;
	public ArrayList<Integer> whichElevatorOnFloor;


	private int numberOfFloors;
	private int numberOfElevators;


    // constructor that initilizes all our values for the ElevatorScene class and lets us acess this from that instnace through the (that) variable
	public ElevatorScene() {
		ElevatorScene.that = this; // bind this
		that.Elevators = new ArrayList<>(); // init Elevators
		that.WaitingForElevator = new ArrayList<>();
		that.WaitingToExitElevator = new ArrayList<>();
		that.personsInElevator = new ArrayList<>();
		that.personsWaitingOnFloor = new ArrayList<>();
		that.exitedCount = new ArrayList<>();
		that.currentFloor = new ArrayList<>();
		that.whichElevatorOnFloor = new ArrayList<>();

	}
	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

        /**
         * Important to add code here to make new
         * threads that run your elevator-runnables
         *
         * Also add any other code that initializes
         * your system for a new run
         *
         * If you can, tell any currently running
         * elevator threads to stop
         */

		// start by joning all threads active in Elevator thread array list
		// run through all elevator threads and join them
		for (Thread elevatorThread:Elevators) {
			// if thread is not initilized or doing anything skipp it
			if(elevatorThread != null) {
				// if thread is alive join it
				if(elevatorThread.isAlive()){
					try{
						elevatorThread.join();

					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		}

		// initilize number of floors and elevators

		that.numberOfFloors = numberOfFloors;
		that.numberOfElevators = numberOfElevators;
/*-------------------------------------- next we create the elevator threads -------------------------------------------*/

        // start by clearing old elevators,current floors and people in elevator when restarting the scene and then repopulate them

        that.Elevators.clear(); // clear Elevator arraylist before resting them
        that.personsInElevator.clear(); // clear personsInElevator arraylist  before resting them
        that.currentFloor.clear(); // clear currentFloor arraylist before reseting them

        // create the elevator threads again after clearing them
        for(int i = 0; i < numberOfElevators;i++){

            // create all the elevator threads
            that.Elevators.add(new Thread(new Elevator(i)));
            that.Elevators.get(i).start();

            // initilize all elevator threads to be empty and set the current floor for them to be ground floor

            that.currentFloor.add(0);
            that.personsInElevator.add(0);

        }

		/*----------------------- next we init all Mutex semaphores  --------------------------------*/


		currentFloorMutex = new Semaphore(1);
		whichElevatorMutex = new Semaphore(1);
		exitedCountMutex = new Semaphore(1);
		personsWaitingMutex = new Semaphore(1);
		personsInElevatorMutex = new Semaphore(1);



        /*------------------------------Next we reinitilize and populate the following Arraylists ,personsWaitingOnFloor,exitedCount,whatElevator ---------------------------------------------------------*/


       // clear and reinitilize semephores for people waiting for elevator
        WaitingForElevator.clear();
        that.whichElevatorOnFloor.clear();

        for(int i = 0; i< numberOfFloors;i++){
            WaitingForElevator.add(new Semaphore(0));
            that.whichElevatorOnFloor.add(0);
        }

        // clear and reinitilize semephores for people leaving elevator

        that.WaitingToExitElevator.clear();

        for(int i = 0; i < numberOfElevators;i++) {

           Semaphore [] index = new Semaphore[numberOfFloors];

           for(int j = 0; j < numberOfFloors; j++) {
               index[j] = new Semaphore(0);
           }
           that.WaitingToExitElevator.add(index);
        }


       //first we clear the tha arraylist then we reinitalize the number of persons leaving the elevators and waiting for a elevator
       that.personsWaitingOnFloor.clear();
       that.exitedCount.clear();
       for(int i = 0; i< numberOfFloors;i++) {
           that.personsWaitingOnFloor.add(0);
           that.exitedCount.add(0);
       }

	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */
		// Creata a new instance of the thread Persons class
        Thread new_Person = new Thread(new Persons(sourceFloor,destinationFloor));
        new_Person.start();
        return new_Person;
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {


        return currentFloor.get(elevator);
	}

	// sets the current floor for elevator
	public void setCurrentFloorForElevator(int elevator, int floor) {
	    try{
	        currentFloorMutex.acquire();
	            currentFloor.set(elevator,floor);
	        currentFloorMutex.release();

        }catch (InterruptedException e) {
	        e.printStackTrace();
        }
    }


	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {

	    int persons_in = 0;

	    try{
	        // this is for thread safety so that only one Elevator thread can access personInElevator each time
	        personsInElevatorMutex.acquire();
                persons_in = personsInElevator.get(elevator);
	        personsInElevatorMutex.release();

        } catch (InterruptedException e) {
	        e.printStackTrace();
        }

        return persons_in;

	}

	// sets the number of people in the elevator
	public void setNumberOfPeopleInElevator(int elevator, int numberOfPeople) {

	    try{
	        personsInElevatorMutex.acquire();
	            personsInElevator.set(elevator,numberOfPeople);
	        personsInElevatorMutex.release();
            System.out.println("People in elevator " + elevator + " : " + numberOfPeople);

        }catch(InterruptedException e) {
	        e.printStackTrace();
        }
    }

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

	    int persons_waiting = 0;
	    //System.out.println(persons_waiting);

	    try {
	        personsWaitingMutex.acquire();
	            persons_waiting = personsWaitingOnFloor.get(floor);
	        personsWaitingMutex.release();
            //System.out.println(persons_waiting);

        } catch (InterruptedException e) {
	        e.printStackTrace();
        }

		return persons_waiting;
	}
	// set the number of people waiting at floor
	public void setNumberOfPeopleWaitingAtFloor(int floor, int numberOfPeople) {

	    try {
	        personsWaitingMutex.acquire();
	            personsWaitingOnFloor.set(floor,numberOfPeople);
	        personsWaitingMutex.release();
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }

    }

    // gets the elveator that is open and letting persons in
    public int getActiveElevator(int floor) {

	    int active_elevator;

        active_elevator = whichElevatorOnFloor.get(floor);

        return active_elevator;
    }

    // sets the elevator that is open and letting persons in
    public void setActiveElevator(int floor,int elevator) throws InterruptedException{


        whichElevatorOnFloor.set(floor,elevator);

    }



	//Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	//Base function: no need to change unless you choose
	//				 not to "open the doors" sometimes
	//				 even though there are people there
	public boolean isElevatorOpen(int elevator) {

		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {

		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

	//Person threads must call this function to
	//let the system know that they have exited.
	//Person calls it after being let off elevator
	//but before it finishes its run.
	public void personExitsAtFloor(int floor) {
		try {
			
			exitedCountMutex.acquire();
			    exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutex.release();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public int getExitedCountAtFloor(int floor) {
		if(floor < getNumberOfFloors()) {
			return exitedCount.get(floor);
		}
		else {
			return 0;
		}
	}

}
