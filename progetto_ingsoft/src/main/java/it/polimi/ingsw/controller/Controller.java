package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.messages.LobbyMessage;
import it.polimi.ingsw.messages.SerializedMessage;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;

import java.util.ArrayList;

public class Controller {

    private MainServer server;
    private final Lobby lobby;
    private Game game;
    private VirtualClient actualPlayerTurn;

    public Controller(Lobby lobby, MainServer server) {
        this.lobby=lobby;
        this.server=server;
    }

    public VirtualView startGame() {
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
        actualPlayerTurn=lobby.getPlayers().get(0);
        lobby.sendAll(new LobbyMessage("is the turn of "+server.getNameFromId().get(actualPlayerTurn.getID())));
        return createVirtualView();
    }

    //TODO
    private VirtualView createVirtualView() {
        String[][] virtualMarket=game.getMarket().viewMarketBoard();
        int[][] virtualDevCards=new int[4][3];
        int k=0;
        for(int i=0;i<4;i++)
            for(int j=0;j<3;j++) {
                virtualDevCards[i][j] = k;
                k++;
            }
        ArrayList<Integer> virtualFaithPos=new ArrayList<>();
        for(Player player:game.getPlayers())
            virtualFaithPos.add(player.getPersonalBoard().getFaithMarker().getFaithPosition());
        return new VirtualView(virtualMarket,virtualDevCards,virtualFaithPos);
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
