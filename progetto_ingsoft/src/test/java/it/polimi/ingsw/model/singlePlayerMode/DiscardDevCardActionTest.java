package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.cards.DevCard;
import it.polimi.ingsw.model.cards.DevDeckMatrix;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiscardDevCardActionTest {

    /*
    this Test is implemented to check if the token's effect of removing a card works correctly
     */
    @Test
    void applyEffect() {
        int i=1;
        SinglePlayer sP = new SinglePlayer();
        ArrayList<DevCard> devDeck = DevDeckMatrix.getDevMatrix()[i][0].getLittleDevDeck();
        String color = devDeck.get(0).getColor();
        DiscardDevCardAction token = new DiscardDevCardAction(color);
        token.applyEffect(sP.getTokensStack());
        devDeck.remove(0);
        assertEquals(devDeck,DevDeckMatrix.getDevMatrix()[i][0].getLittleDevDeck());
    }
}