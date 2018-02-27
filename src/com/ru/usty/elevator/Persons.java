package com.ru.usty.elevator;
// person class is responsible for populating the system with persons threads by calling functions from ElevatorScene and to do its functions it had to call a Mutex.
public class Persons implements Runnable {

    int srcFloor, dstFloor, elevator;

    public Persons(int srcFloor, int dstFloor){
        this.srcFloor = srcFloor;
        this.dstFloor = dstFloor;
        this.elevator = 0;
    }
    @Override
    public void run() {

        try {


            // a new persons thread arrives at a designated floor and increments the number of persons on that floor.

            ElevatorScene.that.setNumberOfPeopleWaitingAtFloor(this.srcFloor, (ElevatorScene.that.getNumberOfPeopleWaitingAtFloor(this.srcFloor)) + 1);

            // Persons starts waiting for elevator and increments the number of persons waiting for elevator at source floor.

            ElevatorScene.WaitingForElevator.get(this.srcFloor).acquire();

            // Persons finds elevator that is open and letting people in.

            this.elevator = ElevatorScene.that.getActiveElevator(this.srcFloor);

            // Persons thread increments the number of people in Active-elevator.

            ElevatorScene.that.setNumberOfPeopleInElevator(this.elevator, (ElevatorScene.that.getNumberOfPeopleInElevator(this.elevator)) + 1);

            // Persons thread decrement number of people waiting at this floor.

            ElevatorScene.that.setNumberOfPeopleWaitingAtFloor(this.srcFloor,(ElevatorScene.that.getNumberOfPeopleWaitingAtFloor(this.srcFloor)) - 1);

            // Persons thread lets elevator know what floor it wants to leave the elevator on.

            ElevatorScene.that.WaitingToExitElevator.get(this.elevator)[this.dstFloor].acquire();

            // Persons thread has arrived at its destination floor and leaves the elevator and decrements the number of persons in that elevator.

            ElevatorScene.that.setNumberOfPeopleInElevator(this.elevator,(ElevatorScene.that.getNumberOfPeopleInElevator(this.elevator)) - 1);

            // Persons thread increments the number of persons who have arrived at their destination floor.

            ElevatorScene.that.personExitsAtFloor(this.dstFloor);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
