package it.polimi.ingsw;


import java.util.ArrayList;

public class Lobby {
    private MainServer server;
    private int lobbyID;
    private int numPlayers;
    private ArrayList<VirtualClient> actualPlayers;
    private boolean full;
    private GameState stateOfGame;

    public Lobby(int lobbyID, int numPlayers, MainServer server){
        this.lobbyID=lobbyID;
        this.numPlayers=numPlayers;
        this.full=false;
        this.stateOfGame=GameState.WAITING;
        this.server=server;
    }

    public int getLobbyID() {
        return lobbyID;
    }


    public boolean isLobbyFull(){
        return actualPlayers.size() == numPlayers;
    }

    public GameState getStateOfGame() {
        return stateOfGame;
    }


    public void sendAll(String s) {
    }

    public void insertPlayer(int id) {

    }
}
