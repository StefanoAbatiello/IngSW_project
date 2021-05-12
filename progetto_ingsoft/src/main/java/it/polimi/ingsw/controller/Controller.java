package it.polimi.ingsw.controller;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.Lobby;
import it.polimi.ingsw.MainServer;
import it.polimi.ingsw.VirtualClient;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayer;
import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;

import java.util.ArrayList;

public class Controller {

    private MainServer server;
    private final Lobby lobby;
    private Game game;

    public Controller(Lobby lobby, MainServer server) {
        this.lobby=lobby;
        this.server=server;
    }

    public void startGame() {
        lobby.sendAll("The game is starting");
        lobby.setStateOfGame(GameState.ONGOING);
        int id;
        if(lobby.getNumPlayer()==0) {
            id = lobby.getPlayers().get(0).getID();
            try {
                game = new SinglePlayer(server.getNameFromId().get(id));
            } catch (playerLeadsNotEmptyException e) {
                e.printStackTrace();
            }
        }
        else{
            ArrayList<String> playersName=new ArrayList<>();
            for(VirtualClient player: lobby.getPlayers()) {
                id = player.getID();
                playersName.add(server.getNameFromId().get(id));
            }
            try {
                game=new MultiPlayer(playersName, lobby.getNumPlayer());
            } catch (playerLeadsNotEmptyException e) {
                e.printStackTrace();
            }
        }
    }
}
