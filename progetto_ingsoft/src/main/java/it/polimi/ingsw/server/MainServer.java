package it.polimi.ingsw.server;


import it.polimi.ingsw.messages.LobbyMessage;

import java.util.*;

public class MainServer {
    private static ConnectionServer connectionServer;
    //this map identifies a client from their id;
    private final Map<Integer, VirtualClient> clientFromId;
    private final Map<Integer,String> nameFromId;
    private final Map<String,Integer> IDFromName;
    private final Map<Integer, Lobby> lobbyFromClientID;
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
        return lobbyFromClientID;
    }

    public int generateLobbyId(){
        int actualLobbyId=nextLobbyId;
        nextLobbyId++;
        return actualLobbyId;
    }


    public MainServer(int port) {
        connectionServer = new ConnectionServer(port,this);
        this.lobbyFromClientID = new HashMap<>();
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
        new Thread(connectionServer).start();
        new Thread(keyboardReader).start();
    }

    //TODO controlla se getconn ha bisogno di sinc
    public static ConnectionServer getConnectionServer() {
        return connectionServer;
    }

    public void disconnectClient(int clientId) {
        ClientHandler clientHandler = getClientFromId().get(clientId).getClientHandler();
        clientHandler.getPingObserver().setActive(false);
        ConnectionServer.removePingObserver(clientHandler.getPingObserver());
        if(lobbyFromClientID.containsKey(clientId)) {
            Lobby lobby=lobbyFromClientID.get(clientId);
            lobby.removePlayer(clientFromId.get(clientId));
            lobby.sendAll(new LobbyMessage(nameFromId.get(clientId)+" left the game"));
        }
        clientFromId.remove(clientId);
        clientHandler.setActive(false);
        String name = nameFromId.get(clientId);
        System.out.println(name + " is disconnected");
    }

///TODO gestione id e metodi lobby
}
