package it.polimi.ingsw.server;


import it.polimi.ingsw.messages.LobbyMessage;
import it.polimi.ingsw.messages.WaitingRoomAction;

import java.util.*;
import java.util.stream.Collectors;

public class MainServer {
    private final ConnectionServer connectionServer;
    //this map identifies a client from their id(>0);
    private final Map<Integer, VirtualClient> clientFromId;
    private final Map<Integer,String> nameFromId;
    private final Map<String,Integer> IDFromName;
    private final Map<Integer, Lobby> lobbyFromClientID;
    private int nextLobbyId;
    private int nextClientId;
    private final ServerInput keyboardReader;

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

    /**
     * @return a new valid Lobby id
     */
    public int generateLobbyId(){
        int actualLobbyId=nextLobbyId;
        nextLobbyId++;
        return actualLobbyId;
    }

    /**
     * @return a new valid client id
     */
    private int generateClientId(){
        int actualClientId=nextClientId;
        nextClientId++;
        return actualClientId;
    }

    public MainServer(int port) {
        connectionServer = new ConnectionServer(port,this);
        this.lobbyFromClientID = new HashMap<>();
        this.clientFromId=new HashMap<>();
        this.nameFromId = new HashMap<>();
        this.IDFromName = new HashMap<>();
        nextLobbyId = 1;
        nextClientId= 1;
        keyboardReader =new ServerInput(this);
        new Thread(connectionServer).start();
        new Thread(keyboardReader).start();
    }

    public static void main(String[] args) {
        System.out.println("I'm the Server, welcome!");
        System.out.println("Please insert PORT");
        System.out.println("Type: \"[PORT chosen]\"");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        if (port < 0 || (port > 0 && port < 1024)) {
            System.err.println("Port number not valid, restart the program");
            System.exit(0);
        }
        new MainServer(port);
    }

    public ConnectionServer getConnectionServer() {
        return connectionServer;
    }

    /**
     * @param name is the user's nickname
     * @param clientHandler is the user's clientHandler
     * @return player ID after connected his client to the server
     */
    private int connectClient(String name, ClientHandler clientHandler) {
        int ID = generateClientId();
        nameFromId.put(ID,name);
        IDFromName.put(name,ID);
        VirtualClient newClient = new VirtualClient(ID, name, clientHandler);
        clientFromId.put(ID,newClient);
        return ID;
    }

    /**
     * @param ID is player ID
     * @param name is player's nickname
     * @param clientHandler is the user's clientHandler
     */
    private void reconnectClient(int ID, String name, ClientHandler clientHandler) {
        VirtualClient newClient = new VirtualClient(ID, name, clientHandler);
        clientFromId.put(ID,newClient);
    }

    /**
     * @param clientId is the id of the client to disconnect
     */
    public void disconnectClient(int clientId) {
        ClientHandler clientHandler = getClientFromId().get(clientId).getClientHandler();
        //clientHandler.closeStream();
        clientHandler.getPingObserver().setActive(false);
        ConnectionServer.removePingObserver(clientHandler.getPingObserver());
        clientFromId.remove(clientId);
        clientHandler.setActive(false);
        System.out.println(nameFromId.get(clientId) + " is disconnected");//[Debug]
        if(lobbyFromClientID.containsKey(clientId)) {
            Lobby lobby=lobbyFromClientID.get(clientId);
            lobby.removePlayer(clientFromId.get(clientId));
        }
        lobbyFromClientID.remove(clientId);
    }

    /**
     * @param newName is the nickname chosen by client
     * @param clientHandler is the user's clientHandler
     * @return new player ID when he is inserted in a new game, -1 when he is already logged into
     */
    public int checkNickName(String newName, ClientHandler clientHandler) {
        //System.out.println("sei dentro checknickname con: " + message.getMessage());[Debug]
        int id;
        for (String name : nameFromId.values()) {
            if (newName.equals(name)) {
                id = IDFromName.get(name);
                System.out.println("nickname già scelto dall'utente: " + id);//[Debug]
                if (isClientOnline(id)) {
                    return -2;
                } else {
                    System.out.println("riconnessione dell'utente "+id);//[Debug]
                    reconnectClient(id, name, clientHandler);
                    if (isClientInALobby(id)) {
                        Lobby lobby = lobbyFromClientID.get(id);
                        lobby.reinsertPlayer(id);
                        lobby.sendAll(new LobbyMessage(name + " is back in the game"));
                    }
                    return id;
                }
            }
        }
        id = connectClient(newName, clientHandler);
        return id;
    }

    /**
     * @param id the id of the client to check
     * @return true if the client was in a lobby yet and can be reinsert in it
     */
    public boolean isClientInALobby(int id) {
        if (lobbyFromClientID.containsKey(id)) {
            System.out.println("esiste partita in cui giocava");//[Debug]
            Lobby lobby = lobbyFromClientID.get(id);
            if (lobby.getStateOfGame() != GameState.ENDED) {
                System.out.println("la partita è in corso, il giocatore può essere ricollegato");//[Debug]
                return true;
            }
            return false;
        } else
            return false;
    }

    /**
     * @param id the id of the client to check
     * @return true if the client checked is logged
     */
    public boolean isClientOnline(int id) {
        if (clientFromId.containsKey(id)) {
            System.out.println("l'utente " + id + "è online");//[Debug]
            return true;
        }
        return false;
    }

    /**
     * @param name the name of the client to check
     * @return true if the client checked is logged
     */
    public boolean isClientOnline(String name) {
        int id=getIDFromName().get(name);
        if (clientFromId.containsKey(id)) {
            System.out.println("l'utente " + id + " è online");//[Debug]
            return true;
        }
        return false;
    }

    /**
     * @param id is the player ID
     * @return true if player is already contained in someone lobby or if he is entered in a new lobby, false if every lobby is full
     */
    public synchronized boolean findEmptyLobby(int id) {
        //System.out.println("sei dentro find empty lobby");[Debug]
        ArrayList<Lobby> lobbies= (ArrayList<Lobby>) lobbyFromClientID.values().stream().distinct().collect(Collectors.toList());
        for(Lobby lobby:lobbies) {
            if (!lobby.isLobbyFull() && lobby.getStateOfGame()==GameState.WAITING && !isSinglePlayerLobby(lobby)) {
                System.out.println("c'è una lobby libera");//[Debug]
                lobby.insertPlayer(id);
                int lobbySpace=lobby.getClientFromPosition().size() + lobby.getSeatsAvailable();
                if(lobby.getSeatsAvailable()>1)
                    clientFromId.get(id).getClientHandler().send(new WaitingRoomAction("You joined a new lobby for "+ lobbySpace + " players, please wait for other players"));
                return true;
            }
        }
        System.out.println("tutte le lobby sono piene, creo una nuova lobby");//[Debug]
        return false;
    }

    /**
     * @param lobby is the lobby to check
     * @return true if the lobby is a single player game
     */
    private boolean isSinglePlayerLobby(Lobby lobby) {
        return(lobby.getClientFromPosition().size()+lobby.getSeatsAvailable())==1;
    }

    public void quitter() {
            connectionServer.setActive(false);
            System.exit(0);
    }
}
