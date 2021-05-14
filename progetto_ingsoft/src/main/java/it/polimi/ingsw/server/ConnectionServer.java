package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.LobbyMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

//è il socket server
public class ConnectionServer implements Runnable{
    private final int port;
    private final ExecutorService executorService;
    //volatile for thread safe
    private volatile boolean active;
    private final MainServer server;
    private static final int timerInitialDelay = 1000; // time in milliseconds
    private static final int timerPeriod = 30000; // time in milliseconds
    private static Timer timer;
    private static TimerTask pingManager;
    private static ArrayList<PingObserver> observers;

    /**
     * this class is used to create the socket connection from the server
     */
    public ConnectionServer(int port, MainServer server){
        this.port=port;
        this.server=server;
        this.executorService= Executors.newCachedThreadPool();
        this.active=true;
        pingManager = new PingManager();
        timer=new Timer();
        observers = new ArrayList<>();
    }

    public void addPingObserver(PingObserver observer){
        System.out.println("sto aggiungendo l'observer");
        observers.add(observer);
    }

    public static void removePingObserver(PingObserver observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }
    public void setActive(boolean active) {
        this.active = active;
        ArrayList<Lobby> lobbies= (ArrayList<Lobby>) server.getLobbyFromClientID().values().stream().distinct().collect(Collectors.toList());
        for(Lobby lobby:lobbies)
            lobby.sendAll(new LobbyMessage("Server is closing!"));
    }

    public void acceptConnections(ServerSocket serverSocket){
        while (active){
            try{
                Socket socket= serverSocket.accept();
                System.out.println("sto accettando la connessione di un client");
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
            timer.schedule(pingManager, timerInitialDelay, timerPeriod);
            acceptConnections(serverSocket);
        } catch (IOException e) {
            System.err.println("Error during server socket creation");
            System.exit(0);
        }
    }

    public ArrayList<PingObserver> getObservers() {
        return observers;
    }

    public MainServer getServer() {
        return server;
    }
}