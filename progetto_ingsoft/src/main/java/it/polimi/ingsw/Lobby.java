package it.polimi.ingsw;


import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;

import java.util.ArrayList;

public class Lobby {
    private MainServer server;
    private int lobbyID;
    private int numPlayers;
    private ArrayList<VirtualClient> actualPlayers;
    private boolean full;
    private GameState stateOfGame;
    private Game game;
    private Controller controller;

    public Lobby(int lobbyID, int numPlayers, MainServer server) {
        this.lobbyID = lobbyID;
        this.numPlayers = numPlayers;
        this.full = false;
        this.stateOfGame = GameState.WAITING;
        this.server = server;
        controller=new Controller(this,this.server);
    }

    public int getLobbyID() {
        return lobbyID;
    }

    public void setStateOfGame(GameState stateOfGame) {
        this.stateOfGame = stateOfGame;
    }


    public boolean isLobbyFull() {
        return actualPlayers.size() == numPlayers;
    }

    public GameState getStateOfGame() {
        return stateOfGame;
    }


    public void sendAll(String s) {
        SerializedMessage message=new GameMessage(s);
        for(VirtualClient player:actualPlayers){
            player.getClientHandler().send(message);
        }
    }

    public ArrayList<VirtualClient> insertPlayer(int id) {
        actualPlayers.add(server.getClientFromId().get(id));
        server.getClientFromId().get(id).giveLobby(this);
        if(isLobbyFull())
            controller.startGame();
        return actualPlayers;
    }

    public int getNumPlayer() {
        return numPlayers;
    }

    public ArrayList<VirtualClient> getPlayers() {
        return actualPlayers;
    }
}
