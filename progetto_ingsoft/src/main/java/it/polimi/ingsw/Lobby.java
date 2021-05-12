package it.polimi.ingsw;


import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;

import it.polimi.ingsw.controller.Controller;

import java.util.ArrayList;

public class Lobby {
    private MainServer server;
    private int lobbyID;
    private int seatsAvailable;
    private ArrayList<VirtualClient> actualPlayers;
    private boolean full;
    private GameState stateOfGame;
    private Controller controller;
    private Game game;
    private Controller controller;

    public Lobby(int lobbyID, int numPlayers, MainServer server) {
        this.lobbyID = lobbyID;
        this.numPlayers = numPlayers;
        this.full = false;
        this.stateOfGame = GameState.WAITING;
        this.server = server;
        controller=new Controller(this,this.server);
    public Lobby(int clientID, int lobbyID, int seatsAvailable, MainServer server){
        this.lobbyID=lobbyID;
        //TODO controllo dove aggiungere 1
        this.seatsAvailable = seatsAvailable+1;
        this.actualPlayers= new ArrayList<>();
        this.full=false;
        this.stateOfGame=GameState.WAITING;
        this.server=server;
        this.server.getLobbyFromClientID().put(clientID, this);
    }

    public int getLobbyID() {
        return lobbyID;
    }

    public void setStateOfGame(GameState stateOfGame) {
        this.stateOfGame = stateOfGame;
    }


    public boolean isLobbyFull(){
        if (seatsAvailable==0)
            return true;
        return false;
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

    public ArrayList<VirtualClient> insertPlayer(int id) {
            actualPlayers.add(server.getClientFromId().get(id));
            server.getClientFromId().get(id).giveLobby(this);
            this.seatsAvailable--;
            if(isLobbyFull())
                controller.startGame();
            return actualPlayers;
    }
}
