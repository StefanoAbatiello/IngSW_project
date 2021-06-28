package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.PingMessage;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class PongObserver implements Runnable {

    private boolean active;
    private final int maxTimeoutNumber = 5;
    private int counterTimeout;
    private final ClientHandler clientHandler;
    private Timer timer;
    private static final int timerPeriod = 5000;// time in milliseconds
    private boolean pingReceived;

    public PongObserver(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        counterTimeout = 0;
        this.active=true;
        timer=new Timer();
        pingReceived=false;
    }

    public synchronized void setActive(boolean active){
        this.active=active;
    }

    @Override
    public void run() {
        System.out.println("mando il ping al client: "+clientHandler.getClientId());
        clientHandler.send(new PingMessage());
        //System.out.println("aspetto il ping del client: "+clientHandler.getClientId());[Debug]
        timer=new Timer();
        TimerTask checkResponse=new TimerTask() {
            @Override
            public void run() {
                if (active) {
                    if (pingReceived) {
                        System.out.println("ping ricevuto dal client "+clientHandler.getClientId());
                        counterTimeout = 0;
                        pingReceived = false;
                        timer.cancel();
                        timer.purge();
                        cancel();
                    } else {
                        counterTimeout = counterTimeout + 1;
                        System.out.println("ping non ricevuto " + counterTimeout + " volta/e dal client: " + clientHandler.getClientId());
                        clientHandler.send(new PingMessage());
                    }
                    if (counterTimeout==maxTimeoutNumber){
                        clientHandler.getServer().disconnectClient(clientHandler.getClientId());
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        };
        timer.schedule(checkResponse, 1000, timerPeriod);
    }

    public synchronized void setResponse() {
        pingReceived=true;
    }
}
