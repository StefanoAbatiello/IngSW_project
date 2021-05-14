package it.polimi.ingsw;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static java.lang.Thread.sleep;

public class PongObserver implements Runnable{

    private boolean pingReceived;
    private boolean started;
    private int maxTimeoutNumber = 3;
    private int counterTimeout;
    private MainClient2 client;
    private ObjectOutputStream pongOutStreamObj;
    private ObjectInputStream pingInStreamObj;

    public PongObserver(MainClient2 client) {
        this.started = false;
        this.counterTimeout = 0;
        this.client = client;
        this.pingInStreamObj = client.getSocketIn();
        this.pongOutStreamObj = client.getSocketOut();
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void run() {
        started = true;
        while (true) {
            try {
                sleep(30000);
                System.out.println("pongObserver a rapporto");
                while (counterTimeout < maxTimeoutNumber) {
                    if (pingReceived) {
                        counterTimeout = counterTimeout + 1;
                        System.out.println("ping non ricevuto " + counterTimeout + " volta/e");
                        sleep(2000);
                    } else {
                        System.out.println("ping ricevuto");
                        counterTimeout = 0;
                        pingReceived = false;
                        break;
                    }
                }
                if(counterTimeout!=0) {
                    client.disconnect();
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setResponse(boolean received) {
        this.pingReceived=received;
    }
}
