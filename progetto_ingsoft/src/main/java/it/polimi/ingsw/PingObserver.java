package it.polimi.ingsw;

import it.polimi.ingsw.messages.PingMessage;
import it.polimi.ingsw.messages.SerializedMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class PingObserver extends TimerTask {

    private boolean pingReceived;
    private boolean started;
    private Timer timer;
    private static final int timerPeriod = 4000; // time in milliseconds
    private final int maxTimeoutNumber = 5;
    private int counterTimeout;
    private final ClientCLI client;

    public PingObserver(ClientCLI client) {
        this.started = false;
        this.client=client;
        counterTimeout=0;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void run() {
        started = true;
        //System.out.println("creo il timer");[Debug]
        timer=new Timer();
        //System.out.println("creo il timertask");[Debug]
        TimerTask checkPing=new TimerTask() {
            @Override
            public void run() {
                //System.out.println("pongObserver a rapporto");[Debug]
                if (!pingReceived) {
                    counterTimeout = counterTimeout + 1;
                    //System.out.println("ping non ricevuto " + counterTimeout + " volta/e");[Debug]
                } else {
                    //System.out.println("ping ricevuto");
                    client.send(new PingMessage());
                    counterTimeout = 0;
                    pingReceived=false;
                    //System.err.println("blocco il timer");
                    timer.cancel();
                    timer.purge();
                    cancel();
                }
                if (counterTimeout == maxTimeoutNumber) {
                    System.out.println("non ho ricevuto ping per troppo tempo");
                    client.disconnect();
                }
            }
        };
        //System.out.println("lancio il timer");
        timer.schedule(checkPing, 0, timerPeriod);
    }

    public synchronized void setResponse(boolean received) {
        this.pingReceived=received;
    }
}
