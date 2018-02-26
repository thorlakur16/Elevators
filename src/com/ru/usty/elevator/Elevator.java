package com.ru.usty.elevator;

public class Elevator implements Runnable {

    /*
    Lyftan aftur á moti er svona:

    kem á hæð
    opnar út semafóru hæðarinnar fyrir jafnmörg pláss og er fólk í lyftunni
    bíður smá stund
    lokar aftur út-semafórunni fyrir þá sem ekki fóru út úr lyftunni (til að skilja ekki eftir opna semafóru)
    opna inn-semafóru hæðarinnar fyrir jafnmörg pláss og hún hefur laus
    bíður smá stund (t.d. hálfan ELEVATOR_WAIT_TIME)
    lokar aftur inn-semafórunni fyrir þau pláss sem ekki fylltust (til að skilja ekki eftir opna semafóru)

    Þannig er heildarkerfið samspil lyftunnar og persónunnar, gegnum notkun þeirra á semafórunum,
    sem eru bara hlið inn á og út af hæðunum.

    ATHUGA að heildarbiðtími á hæð verður að vera ELEVATOR_WAIT_TIME.
    Ef það er beðið oftar en einu sinni á hæðinni, verður að skipta þeim tíma niður.

    Þegar komnar eru fleiri hæðir og hægt að fara inn og út á öllum þá þarf að vera inn-semfóra fyrir hverja
    hæð og út-semafóra fyrir hverja hæð.
    Þá þarf persóna bara að bíða alltaf á réttri semafóru og henni mun verða hleypt inn og út á réttum tíma.
     */

    int currentFloor = 0;
    private int elevatorCapacity = 6;
    boolean isGoingUp = true;
    int numberOfFloors;

    public Elevator(int numberOfFloors){
        this.numberOfFloors = numberOfFloors;
    }


    @Override
    public void run() {
        if(ElevatorScene.elevatorsMayDie == true){
            return;
        }
        for(int i = 0; i < 16; i++){

            ElevatorScene.semaphore1.release(); //signal

        }

        while(true){
            if(numberOfFloors == currentFloor){
                isGoingUp = false;
            }


        }
    }
}
