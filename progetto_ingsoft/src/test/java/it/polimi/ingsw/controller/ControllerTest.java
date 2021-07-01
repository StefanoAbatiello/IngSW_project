package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ActionAlreadySetException;
import it.polimi.ingsw.exceptions.NotAcceptableSelectorException;
import it.polimi.ingsw.messages.SerializedMessage;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Market.Market;
import it.polimi.ingsw.model.Market.MarketMarble;
import it.polimi.ingsw.model.Market.WhiteMarble;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import it.polimi.ingsw.server.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private MainServer server;
    private Lobby lobby;
    private Player player;
    private Controller controller;

    public static class ClientHandlerStub extends ClientHandler{
        /**
         * Constructor of Client Handler which generates output and input stream of the socket
         *
         * @param socket is the socket of type Socket
         * @param server is the server of type MainServer
         */
        public ClientHandlerStub(Socket socket, MainServer server) {
            super(socket, server);
        }

        @Override
        public void send(SerializedMessage message){}
    }

    @BeforeEach
    void initialization() {
        server = new MainServer(1337);
        VirtualClient client=new VirtualClient(1,"player", new ClientHandlerStub(new Socket(), server));
        server.getClientFromId().put(1,client);
        server.getIDFromName().put("player",1);
        server.getNameFromId().put(1,"player");
        lobby =new Lobby(0,1,server);
        lobby.insertPlayer(1);
        lobby.getController().createGame();
        controller=lobby.getController();
        player=controller.getGame().getPlayers().get(0);
    }

    @AfterEach
    void reset(){
        server=null;
        lobby=null;
        controller=null;
        player=null;
    }

    @Test
    //checks state of game after create game is PREPARATION1
    void checkStateOfGameAfterCreation(){
        initialization();
        assertEquals(GameState.PREPARATION1,lobby.getStateOfGame());
        reset();
    }

    @Test
    //checks if anyone does not have choose the leader card
    void checkAnyoneNotChooseLeader(){
        initialization();
        System.out.println(player.getLeadCards());
        assertFalse(controller.checkAllPlayersChooseLeads());
        reset();
    }

    @Test
    //checks if all players has chosen the Leader cards
    void checkAllLeaderChosen(){
        initialization();
        ArrayList<Integer> cardsId=player.getLeadCardsId();
        controller.check2Leads(1,cardsId.get(0),cardsId.get(1));
        System.out.println(player.getLeadCards());
        assertTrue(controller.checkAllPlayersChooseLeads());
        reset();
    }

    @Test
    //check if the player tries to choose the leaders when they already did
    void checkAlreadyChose2Leads(){
        initialization();
        ArrayList<Integer> cardsId=player.getLeadCardsId();
        controller.check2Leads(1,cardsId.get(0),cardsId.get(1));
        assertFalse(controller.check2Leads(1,cardsId.get(0),cardsId.get(1)));
        reset();
    }

    @Test
        //check if the player tries to choose correct leaders
    void checkChoose2CorrectLeads(){
        initialization();
        ArrayList<Integer> cardsId=player.getLeadCardsId();
        assertTrue(controller.check2Leads(1,cardsId.get(0),cardsId.get(1)));
        reset();
    }

    @Test
        //check if the player tries to choose wrong leaders
    void checkChose2WrongLeads(){
        initialization();
        assertFalse(controller.check2Leads(1,70,86));
        reset();
    }

    @Test
    void exceptionCheckMarket() {
        initialization();
        controller.startGame();
        assertThrows(NotAcceptableSelectorException.class, ()->lobby.getController().checkMarket(7,1) );
        reset();
    }
}