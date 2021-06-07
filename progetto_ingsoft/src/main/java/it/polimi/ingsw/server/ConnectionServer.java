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
    private static ArrayList<PongObserver> observers;

    /**
     * this class is used to create the socket connection from the server
     */
    public ConnectionServer(int port, MainServer server){
        this.port=port;
        this.server=server;
        this.executorService= Executors.newCachedThreadPool();
        this.active=true;
        pingManager = new PingManager(server);
        timer=new Timer();
        observers = new ArrayList<>();
    }

    /**
     * @param observer is the one to add in the pongObserver's list
     */
    public void addPingObserver(PongObserver observer){
        observers.add(observer);
    }

    /**
     * @param observer is the one to remove from the pongObserver's list
     */
    public static void removePingObserver(PongObserver observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * set the attribute and notify all client that the server is closing
     * @param active is the value to give to the connectionServer's attribute active
     */
    public void setActive(boolean active) {
        this.active = active;
        ArrayList<Lobby> lobbies= (ArrayList<Lobby>) server.getLobbyFromClientID().values().stream().distinct().collect(Collectors.toList());
        for(Lobby lobby:lobbies)
            lobby.sendAll(new LobbyMessage("Server is closing!"));
    }

    /**
     * accept a client, create the socket then create its clientHandler and run it on a new thread
     * @param serverSocket is the serverSocket that accept the connection of clients
     */
    public void acceptConnections(ServerSocket serverSocket){
        while (active){
            try{
                Socket socket= serverSocket.accept();
                System.out.println("sto accettando la connessione di un client");//[Debug]
                ClientHandler clientHandler=new ClientHandler(socket, server);
                executorService.submit(clientHandler);
            }
            catch (IOException e){
                System.out.println("Error:"+ e.getMessage());
            }
        }
    }

    //TODO classe di tutte le costanti

    /**
     * create the timer of pingMnager, create a serverSocket and prepares to accept clients
     */
    public void run() {
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("Socket Server started. Listening on port "+port+". Type \"quit\" to exit" );
            new Thread(() -> timer.schedule(pingManager,timerInitialDelay,timerPeriod)).start();
            acceptConnections(serverSocket);
        } catch (IOException e) {
            System.err.println("Error during server socket creation");
            System.exit(0);
        }
    }

    public ArrayList<PongObserver> getObservers() {
        return observers;
    }

    public MainServer getServer() {
        return server;
    }
}