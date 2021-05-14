package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.PingMessage;

import java.io.*;
import java.util.Timer;
import static java.lang.Thread.sleep;

public class PingObserver implements Runnable {

    private boolean pongReceived;
    private final int maxTimeoutNumber = 10;
    private int counterTimeout;
    private ObjectOutputStream pingOutStreamObj;
    private final ClientHandler clientHandler;

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public PingObserver(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        counterTimeout = 0;
        pingOutStreamObj = clientHandler.getOutputStreamObj();
        this.pongReceived = false;
    }

    public void sendPing() throws IOException {
            pingOutStreamObj.writeObject(new PingMessage());
            pingOutStreamObj.flush();
        }

        public boolean waitResponse () {
            try {
                while (counterTimeout < maxTimeoutNumber) {
                    sleep(4000);
                    if (pongReceived) {
                        counterTimeout = 0;
                        break;
                    } else {
                        counterTimeout = counterTimeout + 1;
                        System.out.println("pong non ricevuto " + counterTimeout + " volta/e dal client: " + clientHandler.getClientId());
                        try {
                            sendPing();
                        } catch (IOException e) {
                            System.out.println("il socket è stato chiuso per qualche motivo. chiudo il CH");
                            clientHandler.getServer().disconnectClient(clientHandler.getClientId());
                            break;
                        }
                    }
                }
            }catch(InterruptedException e){
                    e.printStackTrace();
            }
            return pongReceived;
        }


    @Override
    public void run() {
        System.out.println("mando il ping al client: "+clientHandler.getClientId());
        try {
            sendPing();
            System.out.println("aspetto il pong del client: "+clientHandler.getClientId());
            if(!waitResponse()) {
                clientHandler.getServer().disconnectClient(clientHandler.getClientId());
            }
            else
                System.out.println("pong del client "+clientHandler.getClientId()+" ricevuto ");
            pongReceived=false;
        } catch (IOException e) {
            System.out.println("il socket è stato chiuso per qualche motivo. chiudo il CH");
            clientHandler.getServer().disconnectClient(clientHandler.getClientId());
        }
    }

    public void setResponse(boolean response) {
        this.pongReceived=response;
    }
}
