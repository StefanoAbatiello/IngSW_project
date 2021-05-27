package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.PingMessage;

import java.io.*;
import java.util.Timer;
import static java.lang.Thread.sleep;

public class PingObserver implements Runnable {

    private boolean active;
    private final int maxTimeoutNumber = 5;
    private int counterTimeout;
    private final ClientHandler clientHandler;

    public PingObserver(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        counterTimeout = 0;
        this.active=true;
    }

    public void setActive(boolean active){
        this.active=active;
    }

    public boolean waitResponse () {
        try {
            while (counterTimeout < maxTimeoutNumber) {
                sleep(4000);
                if (active) {
                    if (clientHandler.getPingReceived()) {
                        counterTimeout = 0;
                        return true;
                    } else {
                        counterTimeout = counterTimeout + 1;
                        System.out.println("ping non ricevuto " + counterTimeout + " volta/e dal client: " + clientHandler.getClientId());
                        clientHandler.asyncSend(new PingMessage());
                    }
                }else
                    return false;
            }
        }catch(InterruptedException e){
                e.printStackTrace();
        }
        return false;
    }


    @Override
    public void run() {
        System.out.println("mando il ping al client: "+clientHandler.getClientId());
        clientHandler.asyncSend(new PingMessage());
        System.out.println("aspetto il ping del client: "+clientHandler.getClientId());
        if(!waitResponse()) {
            clientHandler.getServer().disconnectClient(clientHandler.getClientId());
        }
        else
            System.out.println("ping del client "+clientHandler.getClientId()+" ricevuto ");
        clientHandler.setPingRecieved(false);
    }

}
