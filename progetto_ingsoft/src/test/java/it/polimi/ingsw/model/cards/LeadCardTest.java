package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeadCardTest {

    @Test
    void getLeadCardAbility() {
        LeadCard card= new LeadCard(2,"whiteMarble",
                Resource.STONE);

        assertEquals("whiteMarble",card.getAbility());
        assertNotSame("specialShelf",card.getAbility());
    }

    @Test
    void getLeadCardRes() {
        LeadCard card= new LeadCard(2,"whiteMarble",
                Resource.SHIELD);

        assertEquals(Resource.SHIELD,card.getResource());
        assertNotSame(Resource.COIN,card.getResource());
    }

    @Test
    void getResReq() {
    }

    @Test
    void getDevCardReq() {
    }

    @Test
    void isLeadActive() throws InvalidActiveParameterException {
        LeadCard card= new LeadCard(2,"whiteMarble",
                Resource.SHIELD);
        card.setActive(true);
        assertTrue(card.isActive());
    }

    /*@Test
    //testo che parte throws
    void setLeadActive(boolean leadActive) {
    }

    @Test
    void useAbility() {
    }*/
}