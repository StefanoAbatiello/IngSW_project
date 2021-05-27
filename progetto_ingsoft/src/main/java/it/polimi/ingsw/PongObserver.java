package it.polimi.ingsw;

import it.polimi.ingsw.messages.PingMessage;
import it.polimi.ingsw.messages.SerializedMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class PongObserver implements Runnable{

    private boolean pingReceived;
    private boolean started;
    private final int maxTimeoutNumber = 3;
    private int counterTimeout;
    private final ClientCLI client;

    public PongObserver(ClientCLI client) {
        this.started = false;
        this.counterTimeout = 0;
        this.client = client;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void run() {
        started = true;
        while (true) {
            try {
                //System.out.println("pongObserver a rapporto");[Debug]
                while (counterTimeout < maxTimeoutNumber) {
                    if (!pingReceived) {
                        counterTimeout = counterTimeout + 1;
                        //System.out.println("ping non ricevuto " + counterTimeout + " volta/e");[Debug]
                        sleep(2000);
                    } else{
                        client.asyncSend(new PingMessage());
                        counterTimeout = 0;
                        pingReceived = false;
                        break;
                    }
                }
                if(counterTimeout!=0) {
                    client.disconnect();
                    break;
                }
                sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setResponse(boolean received) {
        this.pingReceived=received;
    }
}
