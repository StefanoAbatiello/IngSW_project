package it.polimi.ingsw.server;


import java.util.HashMap;
import java.util.Map;

public class MainServer {
    private static ConnectionServer connectionServer;
    //this map identifies a client from their id;
    private final Map<Integer, VirtualClient> clientFromId;
    private final Map<Integer,String> nameFromId;
    private final Map<String,Integer> IDFromName;
    private final Map<Integer, Lobby> LobbyFromClientID;
    private int nextLobbyId;
    private static ServerInput keyboardReader;

    public Map<Integer, VirtualClient> getClientFromId() {
        return clientFromId;
    }

    public Map<String, Integer> getIDFromName() {
        return IDFromName;
    }

    public Map<Integer, String> getNameFromId() {
        return nameFromId;
    }

    public Map<Integer, Lobby> getLobbyFromClientID() {
        return LobbyFromClientID;
    }

    public int generateLobbyId(){
        int actualLobbyId=nextLobbyId;
        nextLobbyId++;
        return actualLobbyId;
    }


    public MainServer(int port) {
        this.connectionServer = new ConnectionServer(port,this);
        this.LobbyFromClientID = new HashMap<>();
        this.clientFromId=new HashMap<>();
        this.nameFromId = new HashMap<>();
        this.IDFromName = new HashMap<>();
        nextLobbyId = 0;
        keyboardReader =new ServerInput();
    }

    public static void main(String[] args) {
        System.out.println("I'm the Server, welcome!");
        //String hostname=args[0];
        int portNumber = Integer.parseInt(args[1]);
        if(portNumber<0||(portNumber>0 && portNumber<1024)){
            System.err.println("Port number not valid, restart the program");
            System.exit(0);
        }
        System.err.println("Creating socket server");
        new MainServer(portNumber);
        /*ExecutorService executorService= Executors.newCachedThreadPool();
        executorService.submit(mainServer.connectionServer);
         */
        Thread connectionServerThread = new Thread(connectionServer);
        connectionServerThread.start();
        Thread keyboardReaderThread = new Thread(keyboardReader);
        keyboardReaderThread.start();
    }

    //TODO controlla se getconn ha bisogno di sinc
    public static ConnectionServer getConnectionServer() {
        return connectionServer;
    }

///TODO gestione id e metodi lobby
}
