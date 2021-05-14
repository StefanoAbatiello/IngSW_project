package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.server.GameState;
import it.polimi.ingsw.messages.LobbyMessage;
import it.polimi.ingsw.messages.SerializedMessage;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.MainServer;
import it.polimi.ingsw.server.VirtualClient;
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
        lobby.sendAll((SerializedMessage) new LobbyMessage("The game is starting..."));
        lobby.setStateOfGame(GameState.ONGOING);
        int id;
        if(lobby.getPlayers().size()==1) {
            id = lobby.getPlayers().get(0).getID();
            try {
                System.out.println("creo partita singlePlayer");
                game = new SinglePlayer(server.getNameFromId().get(id));
                System.out.println("partita singlePlayer creata");
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
            for (String name:playersName)
                System.out.println(name);
            try {
                System.out.println("creo partita multiPlayer");
                game=new MultiPlayer(playersName, lobby.getPlayers().size());
                System.out.println("partita multiPlayer creata");
            } catch (playerLeadsNotEmptyException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO methods actions
    public boolean checkBuy(String card){
        return true;
    }

    public boolean checkMarket(int gameObj) {
        return true;
    }

    public boolean checkProduction(ArrayList<Resource> gameObj) {
        return true;
    }
}
