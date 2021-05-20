package it.polimi.ingsw;

import it.polimi.ingsw.messages.PingMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;

public class PongObserver implements Runnable{

    private final ObjectOutputStream socketOut;
    private boolean pingReceived;
    private boolean started;
    private final int maxTimeoutNumber = 3;
    private int counterTimeout;
    private final MainClient client;

    public PongObserver(MainClient client, ObjectOutputStream socketOut) {
        this.socketOut=socketOut;
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
                    } else {
                        try {
                            //System.out.println("ho ricevuto il ping");[Debug]
                            socketOut.writeObject(new PingMessage());
                            //System.out.println("ho inviato il pong");[Debug]
                            socketOut.flush();
                        } catch (IOException e) {
                            client.disconnect();
                        }
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
