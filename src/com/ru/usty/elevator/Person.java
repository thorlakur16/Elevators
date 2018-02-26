package com.ru.usty.elevator;

public class Person implements Runnable {

    /*
    persónan er því bara:

    bíða á inn-semafóru
    lækka á hæð, hækka í lyftu
    bíða á út-semafóru
    lækka í lyftu, hækka á hæð

    Persónan þarf aldrei að tékka á neinu því það er öruggt að hún losnar ekki af þessum semafórum fyrr en
    lyftan kemur á rétta hæð og opnar þær.
    */
    int sourceFloor, destinationFloor;

    public Person(int src, int dst){
        this.sourceFloor = 0;
        this.destinationFloor = 1;
    }

    @Override
    public void run() {
        try {

            ElevatorScene.elevatorElevatorWaitMutex.acquire();
                ElevatorScene.semaphore1.acquire(); //wait
            ElevatorScene.elevatorElevatorWaitMutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Person through barrier
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);

        System.out.println("Person thread released.");

    }
}