package it.polimi.ingsw.server;


import it.polimi.ingsw.messages.LobbyMessage;
import it.polimi.ingsw.messages.NickNameAction;

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
        nextLobbyId++;
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
        //String hostname=args[0];
        try {
            int portNumber = Integer.parseInt(args[1]);
            if (portNumber < 0 || (portNumber > 0 && portNumber < 1024)) {
                System.err.println("Port number not valid, restart the program");
                System.exit(0);
            }
            System.out.println("Creating socket server");
            new MainServer(portNumber);
        }catch (NumberFormatException e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
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
        VirtualClient newClient = new VirtualClient(ID, name, clientHandler.getSocket(), clientHandler);
        clientFromId.put(ID,newClient);
        return ID;
    }

    /**
     * @param ID is player ID
     * @param name is player's nickname
     * @param clientHandler is the user's clientHandler
     */
    private void reconnectClient(int ID, String name, ClientHandler clientHandler) {
        VirtualClient newClient = new VirtualClient(ID, name, clientHandler.getSocket(), clientHandler);
        clientFromId.put(ID,newClient);
    }

    /**
     * @param clientId is the id of the client to disconnect
     */
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
        System.out.println(name + " is disconnected");//[Debug]
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
            System.out.println("l'utente" + id + "non è collegato, ma esiste partita in cui giocava");//[Debug]
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
    private boolean isClientOnline(int id) {
        if (clientFromId.containsKey(id)) {
            System.out.println("l'utente " + id + "è online. Il nuovo utente deve cambiare nickname");//[Debug]
            return true;
        }
        return false;
    }

    /**
     * @param id is the player ID
     * @return true if player is already contained in someone lobby or if he is entered in a new lobby, false if every lobby is full
     */
    public boolean findEmptyLobby(int id) {
        System.out.println("sei dentro check first player");//[Debug]
        ArrayList<Lobby> lobbies= (ArrayList<Lobby>) lobbyFromClientID.values().stream().distinct().collect(Collectors.toList());
        for(Lobby lobby:lobbies) {
            if (!lobby.isLobbyFull() && lobby.getStateOfGame()==GameState.WAITING && !isSinglePlayerLobby(lobby)) {
                System.out.println("c'è una lobby libera");//[Debug]
                lobby.sendAll(new LobbyMessage(nameFromId.get(id) + " is entered in the lobby"));
                lobby.insertPlayer(id);
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
        return(lobby.getPlayers().size()+lobby.getSeatsAvailable())!=1;
    }

}
