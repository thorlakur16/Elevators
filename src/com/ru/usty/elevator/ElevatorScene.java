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

	/*
	Arraylista af semafórum sem tákna persónur að bíða eftir lyftu
	Arraylista af semafórum sem tákna persónur að bíða eftir að komast út úr lyftu

	Kannski er þetta bara orðalag og þið eruð að hugsa þetta rétt, en endilega reynið
	að aftengja þessa hugsum að semafóran tengist á einhvern hátt persónum.  hugsið frekar
	semafórurnar sem hlið sem persónan bíður á og lyftan síðan opnar og lokar.

	Mér sýnist samt á öllu að þið séuð að hugsa þetta nokkurn vegin rétt.
	Ég myndi samt hugsa þetta meira aðskilið fyrir persónu og lyftu (sjá hér neðar),
	þ.e. annars vegar aðgerðaröð fyrir persónu og hins vegar aðgerðaröð fyrir lyftu.
	Þetta tvennt á ekkert að tala saman eða vita hvort af öðru, heldur bara kalla á
	semafórurnar og stýra þannig flæðinu í sameiningu, án þess nokkurn tíman að tala
	beint saman.

	Þið þurfið líka að hugsa um hversu oft (eða fyrir hve mörg pláss) lyftan opna og
	lokar.

	Hugsið þessa mutexa líka alveg up á nýtt.  T.d. personWaitingMutex hefur engan tilgang.
	Það þarf ekki að passa upp á thread safety á semafórunum.  Ef þið viljið nota mutexa þá
	ættu þeir bara að vera til að passa upp á critical section, t.d. þar sem verið er að
	breyta gildina á einhverjum counter, sem aðrir þræðir gætu reynt að breyta á sama tíma.
	Lyftukerfið sjálft er bara keyrt á þessum inn og út semafórum.

	Það má hugsa það þannig á hverjum stað sem þarf að bíða á sé semafóra.  þannig sé semafóra
	inn á neðri hæðinni og önnur semfóra út á efri hæðinni.  persóna bíður fyrst á inn-semafórunni
	og er í raun föst þar þangað til lyfta opnar (SIGNAL, release()).  Um leið og persónan losnar
	lækkar hún counter á hæðinni og hækkar í lyftunni til að herma það að hún hafi stigið inn í
	lyftuna.  Svo bíður hún á út-semafórunni.  Þegar lyftan kemur upp á efri hæðina opnar hún
	út-semafóruna og persónan losnar.  Persónan lækkar þá counter í lyftunni og hækkar á út hlið
	hæðarinnar til að herma það að hafa stigið út.
	 */

	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	public static final int VISUALIZATION_WAIT_TIME = 500;  //milliseconds

	public static boolean elevatorsMayDie;

	public static Semaphore semaphore1;

	public static Semaphore personCountMutex;
	public static Semaphore elevatorWaitMutex;

	private Thread elevatorThread = null;
	public static ElevatorScene scene;

	private int numberOfFloors;
	private int numberOfElevators;

	ArrayList<Integer> personCount; //use if you want but
									//throw away and
									//implement differently
									//if it suits you
	ArrayList<Integer> exitedCount = null;
	public static Semaphore exitedCountMutex;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

		elevatorsMayDie = true;

		if(elevatorThread != null){
			if(elevatorThread.isAlive()){
				try {
					elevatorThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		elevatorsMayDie = false;

		scene = this;
		semaphore1 = new Semaphore(0);
		personCountMutex = new Semaphore(1);
		elevatorWaitMutex = new Semaphore(1);

		elevatorThread = new Thread(new Elevator(numberOfFloors));
		elevatorThread.start();

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


		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;

		personCount = new ArrayList<Integer>();
		for(int i = 0; i < numberOfFloors; i++) {
			this.personCount.add(0);
		}

		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		exitedCountMutex = new Semaphore(1);
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

		Thread thread = new Thread(new Person( sourceFloor, destinationFloor));
		thread.start();

		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */

		//dumb code, replace it!
		try {

			personCountMutex.acquire();
				personCount.set(sourceFloor, personCount.get(sourceFloor) + 1);
			personCountMutex.release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return thread;  //this means that the testSuite will not wait for the threads to finish
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {

		//dumb code, replace it!
		return 1;
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {
		
		//dumb code, replace it!
		switch(elevator) {
		case 1: return 1;
		case 2: return 4;
		default: return 3;
		}
	}

	//Láki
	public void incrementNumberOfPeopleInElevator() {

	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

	public void decrementNumberOfPeopleWaitingAtFloor(int floor){
		try {

			personCountMutex.acquire();
				personCount.set(floor, (personCount.get(floor) -1));
			personCountMutex.release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
