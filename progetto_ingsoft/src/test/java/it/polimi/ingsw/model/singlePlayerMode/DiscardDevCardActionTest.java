package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.cards.DevCard;
import it.polimi.ingsw.model.cards.DevDeckMatrix;
import it.polimi.ingsw.model.cards.LittleDevDeck;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DiscardDevCardActionTest {

    @Test
    void applyEffect() {
        int i=3;
        new DevDeckMatrix();
        ArrayList<DevCard> devDeck = DevDeckMatrix.getDevMatrix()[i][0].getLittleDevDeck();
        String color = devDeck.get(0).getColor();
        DiscardDevCardAction token = new DiscardDevCardAction(color);
        token.applyEffect();
        devDeck.remove(0);
        assertEquals(devDeck,DevDeckMatrix.getDevMatrix()[i][0].getLittleDevDeck());
    }
}