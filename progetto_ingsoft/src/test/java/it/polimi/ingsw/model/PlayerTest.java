package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ActionAlreadySetException;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.LeadDeck;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void doBasicProduction() throws ResourceNotValidException {
        Player player=new Player("Pippo");
        ArrayList<Resource> prodInput=new ArrayList<>();
        prodInput.add(Resource.SERVANT);
        prodInput.add(Resource.SHIELD);
        player.getPersonalBoard().getWarehouseDepots().addInShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(prodInput);
        //assertEquals(Resource.COIN,player.doBasicProduction(prodInput,Resource.COIN));
    }

    //TODO controller test
    /*@Test
    void doNotValidBasicProduction1() throws ResourceNotValidException {
        Player player=new Player("Paki");
        ArrayList<Resource> prodInput=new ArrayList<>();
        prodInput.add(Resource.SERVANT);
        prodInput.add(Resource.SHIELD);
        player.getPersonalBoard().getWarehouseDepots().addInShelf(0, Resource.SERVANT);
        assertThrows(ResourceNotValidException.class,()-> player.doBasicProduction(prodInput,Resource.COIN));
    }*/

    @Test
    void firstLeadCardChosen() throws  playerLeadsNotEmptyException, IOException, ParseException {
        LeadDeck deck= new LeadDeck();
        Player player = new Player("Ciccio");
        deck.giveToPlayer(player);
        System.out.println("num leads: " +player.getLeadCards().size());
        ArrayList<LeadCard> oldPlayerCards= player.getLeadCards();
        player.choose2Leads(player.getLeadCards().get(0).getId(), player.getLeadCards().get(1).getId());
        assertEquals(oldPlayerCards.get(0),player.getLeadCards().get(0));
    }

    @Test
    void secondLeadCardChosen() throws playerLeadsNotEmptyException, IOException, ParseException {
        LeadDeck deck= new LeadDeck();
        Player player = new Player("Ciccio");
        deck.giveToPlayer(player);
        ArrayList<LeadCard> oldPlayerCards= player.getLeadCards();
        player.choose2Leads(player.getLeadCards().get(0).getId(), player.getLeadCards().get(1).getId());
        assertEquals(oldPlayerCards.get(1),player.getLeadCards().get(1));
    }

    @Test
    void numberOfLeadCardChosen() throws playerLeadsNotEmptyException, IOException, ParseException {
        LeadDeck deck= new LeadDeck();
        Player player = new Player("Ciccio");
        deck.giveToPlayer(player);
        player.choose2Leads(player.getLeadCards().get(0).getId(), player.getLeadCards().get(1).getId());
        assertEquals(2, player.getLeadCards().size());
    }

    @Test
    void checkSetAction() throws ActionAlreadySetException {
        Player player= new Player("Jenny");
        player.setAction(Action.ACTIVATEPRODUCTION);
        assertEquals(Action.ACTIVATEPRODUCTION,player.getAction());
    }

   //TODO controller test
    /* @Test
    void checkExceptionSetAction() throws ActionAlreadySetException {
        Player player= new Player("USER");
        player.setAction(Action.ACTIVATEPRODUCTION);
        assertThrows(ActionAlreadySetException.class,()->player.setAction(Action.BUYCARD));
    }*/

}