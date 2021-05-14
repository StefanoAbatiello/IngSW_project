package it.polimi.ingsw.server;


import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.LobbyMessage;
import it.polimi.ingsw.messages.SerializedMessage;
import it.polimi.ingsw.model.Game;
import java.util.ArrayList;

public class Lobby {
    private final MainServer server;
    private final int lobbyID;
    private int seatsAvailable;
    private ArrayList<VirtualClient> actualPlayers;
    private GameState stateOfGame;
    private final Controller controller;
    private Game game;

    public Lobby(int clientID, int lobbyID, int seatsAvailable, MainServer server){
        this.lobbyID=lobbyID;
        this.seatsAvailable = seatsAvailable;
        this.actualPlayers= new ArrayList<>();
        this.stateOfGame=GameState.WAITING;
        this.server=server;
        this.server.getLobbyFromClientID().put(clientID, this);
        controller=new Controller(this,this.server);
    }

    public int getLobbyID() {
        return lobbyID;
    }

    public void setStateOfGame(GameState stateOfGame) {
        this.stateOfGame = stateOfGame;
    }

    public boolean isLobbyFull(){
        return seatsAvailable == 0;
    }

    public GameState getStateOfGame() {
        return stateOfGame;
    }

    public void sendAll(SerializedMessage message) {
        for(VirtualClient player:actualPlayers){
            player.getClientHandler().send(message);
        }
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public ArrayList<VirtualClient> insertPlayer(int id) {
            actualPlayers.add(server.getClientFromId().get(id));
            server.getClientFromId().get(id).giveLobby(this);
            this.seatsAvailable--;
            if(isLobbyFull()) {
                System.out.println("numero di giocatori raggiunto, inizia la partita!!!");
                sendAll((SerializedMessage) new LobbyMessage("number of players reached, the game can start!!!"));
                controller.startGame();
            }
            return actualPlayers;
    }

    public ArrayList<VirtualClient> getPlayers() {
        return actualPlayers;
    }

    public ArrayList<VirtualClient> removePlayer(VirtualClient player) {
        actualPlayers.remove(player);
        seatsAvailable++;
        return actualPlayers;
    }
}
