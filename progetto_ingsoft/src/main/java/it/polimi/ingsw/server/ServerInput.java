package it.polimi.ingsw.server;

import it.polimi.ingsw.server.MainServer;

import java.util.Scanner;

import static java.lang.Thread.sleep;

public class ServerInput implements Runnable {

    /**
     * The mainServer of which this class is the input
     */
    private final MainServer server;

    /**
     *
     * @param server the mainServer of the class
     */
    public ServerInput(MainServer server){
        this.server=server;
    }

    @Override
    public void run() {
        Scanner scanner=new Scanner(System.in);
        while (true){
            if(scanner.next().equalsIgnoreCase("quit")){
                server.getConnectionServer().setActive(false);
                /*try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                System.out.println("sto chiudendo");
                System.exit(0);
                break;
            }
        }
    }
}
