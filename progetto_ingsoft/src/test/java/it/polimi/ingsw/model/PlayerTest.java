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
    void checkSetAction(){
        Player player= new Player("Jenny");
        player.setAction(Action.ACTIVATEPRODUCTION);
        assertEquals(Action.ACTIVATEPRODUCTION,player.getAction());
    }


}