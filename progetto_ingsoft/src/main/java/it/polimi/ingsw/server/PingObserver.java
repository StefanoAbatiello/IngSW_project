package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.PingMessage;

import java.io.*;
import java.util.Timer;
import static java.lang.Thread.sleep;

public class PingObserver implements Runnable {

    private boolean active;
    private boolean pingReceived;
    private final int maxTimeoutNumber = 5;
    private int counterTimeout;
    private ObjectOutputStream pingOutStreamObj;
    private final ClientHandler clientHandler;

    public PingObserver(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        counterTimeout = 0;
        pingOutStreamObj = clientHandler.getOutputStreamObj();
        this.pingReceived = false;
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
                    if (pingReceived) {
                        counterTimeout = 0;
                        break;
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
        return pingReceived;
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
        pingReceived=false;
    }

    public void setResponse(boolean response) {
        this.pingReceived=response;
    }
}
