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
    /**
     * the port of the connection
     */
    private final int port;
    /**
     * the executorService of the connection
     */
    private final ExecutorService executorService;
    //volatile for thread safe
    /**
     * it says if the connection is active
     */
    private volatile boolean active;
    /**
     * the server of the connection
     */
    private final MainServer server;
    /**
     * timer delay for the ping
     */
    private static final int timerInitialDelay = 1000; // time in milliseconds
    /**
     * timer period for the ping
     */
    private static final int timerPeriod = 30000; // time in milliseconds
    /**
     * timer for the ping
     */
    private static Timer timer;
    /**
     * ping manager of the connection
     */
    private static TimerTask pingManager;
    /**
     * array of observers for the pong from clients
     */
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
        synchronized (observers) {
            observers.add(observer);
        }
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
    public synchronized void acceptConnections(ServerSocket serverSocket){
        while (active){
            try{
                Socket socket= serverSocket.accept();
                System.out.println("sto accettando la connessione di un client");//[Debug]
                ClientHandler clientHandler=new ClientHandler(socket, server);
                System.out.println("CH creato");
                executorService.submit(clientHandler);
            }
            catch (IOException e){
                System.err.println("Error:"+ e.getMessage());
            }
        }
    }

    /**
     * create the timer of pingMnager, create a serverSocket and prepares to accept clients
     */
    public void run() {
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            System.out.println("Socket Server started. Listening on port "+port+". Type \"quit\" to exit" );
            executorService.submit(()->timer.schedule(pingManager,timerInitialDelay,timerPeriod));
            acceptConnections(serverSocket);
        } catch (IOException e) {
            System.err.println("Error during server socket creation");
            System.exit(0);
        }
    }

    /**
     *
     * @return the list of observers for the pong
     */
    public ArrayList<PongObserver> getObservers() {
        return observers;
    }

    /**
     *
     * @return the server of the connection
     */
    public MainServer getServer() {
        return server;
    }


}