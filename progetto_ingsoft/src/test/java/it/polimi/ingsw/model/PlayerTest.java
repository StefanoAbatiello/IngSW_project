package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.AbilityAlreadySetException;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.cards.LeadAbility;
import it.polimi.ingsw.model.cards.LeadAbilityWhiteMarble;
import it.polimi.ingsw.model.cards.LeadDeck;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void doBasicProduction() throws ResourceNotValidException {
        Player player=new Player(1);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SHIELD);
        player.setPotentialResource("COIN");

        assertEquals(Resource.COIN,player.doBasicProduction(Resource.SERVANT, Resource.SHIELD));
    }


    @Test
    void doNotValidBasicProduction1() throws ResourceNotValidException {
        Player player=new Player(1);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SHIELD);
        player.setPotentialResource("COIN");
        player.doBasicProduction(Resource.STONE, Resource.SHIELD);

        //assertEquals(1,player.getCoderr());
    }

    @Test
    void doNotValidBasicProduction2() throws ResourceNotValidException {
        Player player=new Player(1);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SHIELD);
        player.setPotentialResource("COIN");
        player.doBasicProduction(Resource.STONE, Resource.COIN);

        //assertEquals(2,player.getCoderr());
    }

    //TODO divide the test in different tries
    @Test
    void choose2leadsWorks() throws WrongAbilityInCardException, CardChosenNotValidException, AbilityAlreadySetException, playerLeadsNotEmptyException {

        LeadDeck deck= new LeadDeck();
        Player player = new Player(2);
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

}