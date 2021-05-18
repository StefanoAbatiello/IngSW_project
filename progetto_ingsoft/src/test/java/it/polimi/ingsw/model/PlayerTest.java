package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.AbilityAlreadySetException;
import it.polimi.ingsw.exceptions.ActionAlreadySetException;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.LeadDeck;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void doBasicProduction() throws ResourceNotValidException {
        Player player=new Player("Pippo");
        ArrayList<Resource> prodInput=new ArrayList<>();
        prodInput.add(Resource.SERVANT);
        prodInput.add(Resource.SHIELD);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(prodInput);
        assertEquals(Resource.COIN,player.doBasicProduction(prodInput,Resource.COIN));
    }


    @Test
    void doNotValidBasicProduction1() throws ResourceNotValidException {
        Player player=new Player("Paki");
        ArrayList<Resource> prodInput=new ArrayList<>();
        prodInput.add(Resource.SERVANT);
        prodInput.add(Resource.SHIELD);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        assertThrows(ResourceNotValidException.class,()-> player.doBasicProduction(prodInput,Resource.COIN));
    }

    @Test
    void firstLeadCardChosen() throws WrongAbilityInCardException, CardChosenNotValidException, AbilityAlreadySetException, playerLeadsNotEmptyException {
        LeadDeck deck= new LeadDeck();
        Player player = new Player("Ciccio");
        deck.giveToPlayer(player);
        ArrayList<LeadCard> oldPlayerCards= player.getLeadCards();
        player.choose2Leads(player.getLeadCards().get(0), player.getLeadCards().get(1));
    }

    @Test
    void secondLeadCardChosen() throws WrongAbilityInCardException, CardChosenNotValidException, AbilityAlreadySetException, playerLeadsNotEmptyException {
        LeadDeck deck= new LeadDeck();
        Player player = new Player("Ciccio");
        deck.giveToPlayer(player);
        ArrayList<LeadCard> oldPlayerCards= player.getLeadCards();
        player.choose2Leads(player.getLeadCards().get(0), player.getLeadCards().get(1));
        assertEquals(oldPlayerCards.get(1),player.getLeadCards().get(1));
    }

    @Test
    void numberOfLeadCardChosen() throws WrongAbilityInCardException, CardChosenNotValidException, AbilityAlreadySetException, playerLeadsNotEmptyException {
        LeadDeck deck= new LeadDeck();
        Player player = new Player("Ciccio");
        deck.giveToPlayer(player);
        ArrayList<LeadCard> oldPlayerCards= player.getLeadCards();
        player.choose2Leads(player.getLeadCards().get(0), player.getLeadCards().get(1));
        assertTrue(player.getLeadCards().size()==2);
    }

    @Test
    void checkSetAction() throws ActionAlreadySetException {
        Player player= new Player("Jenny");
        player.setAction(Action.ACTIVATEPRODUCTION);
        assertEquals(Action.ACTIVATEPRODUCTION,player.getAction());
    }

    @Test
    void checkExceptionSetAction() throws ActionAlreadySetException {
        Player player= new Player("USER");
        player.setAction(Action.ACTIVATEPRODUCTION);
        assertThrows(ActionAlreadySetException.class,()->player.setAction(Action.BUYCARD));
    }

}