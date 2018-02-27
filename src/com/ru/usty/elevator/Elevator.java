package com.ru.usty.elevator;

import java.util.PrimitiveIterator;
public class Elevator implements Runnable {

    private int currentElveator;
    private int currentFloor;
    private int MAX_IN_ELEVATOR = 6;
    private boolean isRunning = true;
    private boolean goingUp = true;
    private boolean isWaitingAtFloor = true;
    private int topFloor = ElevatorScene.that.getNumberOfFloors() - 1; // -1 because floors are from 0 to topfloor
    private int bottomFloor = 0;



    public Elevator(int number){
        this.currentElveator = number;
        this.currentFloor = 0;
    }
    // function that moves the elevator between floors
    private void moveToNextFloor() throws InterruptedException{

        if(goingUp ) {
            ElevatorScene.that.setCurrentFloorForElevator(this.currentElveator,(this.currentFloor) +1);
            this.currentFloor++;
            System.out.println("current Floor " + this.currentFloor);
        }
        else if(!goingUp) {
            ElevatorScene.that.setCurrentFloorForElevator(this.currentElveator,(this.currentFloor) -1);
            this.currentFloor--;
            System.out.println("current Floor " + this.currentFloor);
        }
        if(this.currentFloor == topFloor){
            goingUp = false;
        }
        if(this.currentFloor == bottomFloor){
            goingUp = true;
        }
    }

    @Override
    public void run() {


        // here we let the elevator thread sleep to allow persons threads to que up on each floor
        try{
            Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        // all process running while there are Person threads waiting on floors or in a elevator

        while (isRunning){

            try{
                // if elevator contains some persons Threads
                if(ElevatorScene.that.getNumberOfPeopleInElevator(this.currentElveator) > 0) {


                    // if the current elevator holds some persons threads we start by releasing as many semaphores as there are persons threads in the elevator for current floor

                    for(int i = 0; i < this.MAX_IN_ELEVATOR;i++) {


                        ElevatorScene.that.WaitingToExitElevator.get(this.currentElveator)[this.currentFloor].release();

                        // here we let the elevator thread sleep so person threads can enter until elevator is full if there are threads waiting on that floor
                        Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
                    }

                    // after releasing from the semaphore for current elevator on current floor check if there are unclaimed semaphore realeses and if so reacquire them

                    int unclaimed_semaphores = ElevatorScene.that.WaitingToExitElevator.get(this.currentElveator)[this.currentFloor].availablePermits();

                    // here we loop through unclaimed semaphores for current elavator and current floor and reacquire them
                    System.out.println(unclaimed_semaphores);

                    for(int i = 0; i < unclaimed_semaphores;i++) {
                        ElevatorScene.that.WaitingToExitElevator.get(this.currentElveator)[this.currentFloor].acquire();

                    }

                }

                // if the elevator isint full get the space available and realease semephores to let person thread in to fill the elevator
                if(ElevatorScene.that.getNumberOfPeopleInElevator(this.currentElveator) >= 0 && ElevatorScene.that.getNumberOfPeopleInElevator(this.currentElveator) < this.MAX_IN_ELEVATOR) {

                    // if there are persons threads waiting on current floor
                    if(ElevatorScene.that.personsWaitingOnFloor.get(this.currentFloor) > 0){

                        //set the active elevator to the elevator with availble space and telling the persons thread that the elevator is open by releasing the mutex accuired by the persons thread
                        ElevatorScene.whichElevatorMutex.acquire();

                        ElevatorScene.that.setActiveElevator(this.currentFloor,this.currentElveator);

                        int available_space = this.MAX_IN_ELEVATOR - ElevatorScene.that.personsInElevator.get(this.currentElveator);
                        System.out.println(available_space);

                        // for every space avilable on the elevator realeas a semaphore that allows a persons thread to eneter the elevator
                        for(int i = 0; i < available_space;i++) {
                            //System.out.println(available_space);
                            ElevatorScene.WaitingForElevator.get(this.currentFloor).release();
                            // we let the elevator thread sleep so the persons thread can leave the elevator one at a time
                            Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
                        }

                        // check if anay of the semaphores released went unclaimed if so reacquire them

                        int unclaimed_semaphores = ElevatorScene.WaitingForElevator.get(this.currentFloor).availablePermits();

                        for(int i = 0; i < unclaimed_semaphores; i++){
                            ElevatorScene.WaitingForElevator.get(this.currentFloor).acquire();
                        }

                        ElevatorScene.whichElevatorMutex.release();

                    }

                }

                // check if there are any Persons threads waiting on any floor.
                for(int i = 0; i<= topFloor;i++) {
                   // System.out.println(ElevatorScene.that.getNumberOfPeopleWaitingAtFloor(i));

                    if(ElevatorScene.that.personsWaitingOnFloor.get(i) > 0) {
                        System.out.println("enters isWiating = true ");
                        isWaitingAtFloor = true;
                        break;
                    }
                    isWaitingAtFloor = false;
                    //System.out.println("enters isWaiting = false stop");
                }

                // and if there are no persons threads waiting and the elevator is empty stop the Elevator system on current floor
                if(isWaitingAtFloor == false && ElevatorScene.that.personsInElevator.get(this.currentElveator) == 0 ){
                    isRunning = false;
                    System.out.println("Elevator: " + this.currentElveator + " finished");

                }
                // move the Elevator between floors
                moveToNextFloor();

            } catch(InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
