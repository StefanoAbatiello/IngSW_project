package it.polimi.ingsw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

//è il socket server
public class ConnectionServer implements Runnable{
        private final int port;
        private final ExecutorService executorService;
    //volatile for thread safe
        private volatile boolean active;
        private final MainServer server;

    /**
     * this class is used to create the socket connection from the server
     */
    public ConnectionServer(int port, MainServer server){
        this.port=port;
        this.server=server;
        this.executorService= Executors.newCachedThreadPool();
        this.active=true;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void acceptConnections(ServerSocket serverSocket){
        while (active){
            try{
                Socket socket= serverSocket.accept();
                System.out.println("sono nel try");
                ClientHandler clientHandler=new ClientHandler(socket, server);
                executorService.submit(clientHandler);
            }
            catch (IOException e){
                System.out.println("Error:"+ e.getMessage());
            }

        }
    }

    //TODO classe di tutte le costanti
    public void run() {
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("Socket Server started. Listening on port "+port+". Type \"quit\" to exit" );
            acceptConnections(serverSocket);
        } catch (IOException e) {
            System.err.println("Error during server socket creation");
            System.exit(0);
        }
    }

}