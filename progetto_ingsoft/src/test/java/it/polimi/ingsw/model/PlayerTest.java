package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.AbilityAlreadySetException;
import it.polimi.ingsw.exceptions.ActionAlreadySet;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.cards.LeadAbility;
import it.polimi.ingsw.model.cards.LeadAbilityWhiteMarble;
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
        player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SHIELD);
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


    //TODO divide the test in different tries
    @Test
    void choose2leadsWorks() throws WrongAbilityInCardException, CardChosenNotValidException, AbilityAlreadySetException, playerLeadsNotEmptyException {

        LeadDeck deck= new LeadDeck();
        Player player = new Player("Ciccio");
        deck.giveToPlayer(player);

        LeadAbility ability1= player.getLeadCards().get(0).getAbilityFromCard();
        ability1.setAbilityResource(Resource.SERVANT);
        LeadAbility ability2=player.getLeadCards().get(1).getAbilityFromCard();
        ability2.setAbilityResource(Resource.SHIELD);
        player.choose2LeadsAndSetAbilities(player.getLeadCards().get(0), player.getLeadCards().get(1));

        assertEquals(ability1.getClass(), player.getAbility1().getClass());
        assertEquals(ability2.getClass(), player.getAbility2().getClass());
        assertEquals(ability1.getAbilityResource(),player.getAbility1().getAbilityResource());
        assertEquals(ability2.getAbilityResource(),player.getAbility2().getAbilityResource());

    }

    @Test
    void set2Abilities() {

    }

    @Test
    void checkSetAction() throws ActionAlreadySet {
        Player player= new Player("Jenny");
        player.setAction(Action.ACTIVATEPRODUCTION);
        assertEquals(Action.ACTIVATEPRODUCTION,player.getAction());
    }

    @Test
    void checkExceptionSetAction() throws ActionAlreadySet {
        Player player= new Player("USER");
        player.setAction(Action.ACTIVATEPRODUCTION);
        assertThrows(ActionAlreadySet.class,()->player.setAction(Action.BUYCARD));
    }

}