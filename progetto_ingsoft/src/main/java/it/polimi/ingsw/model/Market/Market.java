package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.exceptions.NotAcceptableSelectorException;
import it.polimi.ingsw.model.Player;
import java.util.*;

public class Market {

    /*
    this matrix is the market tray
    where the player choose the resource to take
    */
    private final MarketMarble[][] marketTray = new MarketMarble[3][4];

    /*
    this is the marble which remains out of the tray
    */
    private MarketMarble externalMarble;

    /**
     * this is a constructor method that create all the marbles and put them randomly in marketTray
     */
    public Market() {
        List<MarketMarble> marbles = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            marbles.add(new WhiteMarble());
        for (int i = 0; i < 2; i++)
            marbles.add(new BlueMarble());
        for (int i = 0; i < 2; i++)
            marbles.add(new GreyMarble());
        for (int i = 0; i < 2; i++)
            marbles.add(new YellowMarble());
        for (int i = 0; i < 2; i++)
            marbles.add(new PurpleMarble());
        marbles.add(new RedMarble());
        Collections.shuffle(marbles);
        int index = 0;
        for (MarketMarble marble : marbles) {
            if (index != 12) {
                marketTray[index / 4][index % 4] = marble;
                index++;
            } else
                externalMarble = marble;
        }
    }

    /**
     * @return the actual composition of marketTray
     */
    public MarketMarble[][] getMarketBoard() {
        return marketTray;
    }

    /**
     * @return the marble that is out of the marketTray
     */
    public MarketMarble getExtMarble() {
        return externalMarble;
    }

    /**
     * @param selector is an integer number to indicate which column or line the player has chosen
     * @param p is a reference to actual player
     * @return a boolean to indicate if the purchase is done or not
     * @throws NotAcceptableSelectorException if the selector received is negative or greater than 6
     */
    public MarketMarble[][] buyResources(int selector, Player p) throws FullSupplyException, NotAcceptableSelectorException {
        if(selector<0)
            throw new NotAcceptableSelectorException("Selector is negative, so isn't acceptable");
        if(selector<=2) {
            for (int i = 0; i < 4; i++)
                marketTray[selector][i].changeMarble(p);
            return insertExtMarble(selector);
        }
        if(selector<=6) {
            for (int i = 0; i < 3; i++)
                    marketTray[i][selector-3].changeMarble(p);
            return insertExtMarble(selector);
        }
        throw new NotAcceptableSelectorException("Selector is greater than 6, so isn't acceptable");
    }

    /**
     * @param selector is an integer number to indicate which column or line the player has chosen
     * @return marketTray modified by the insertion of externalMarble
     */
    private MarketMarble[][] insertExtMarble(int selector) {
        MarketMarble temp = externalMarble;
        if (selector >= 0 && selector <= 2) {
            externalMarble = marketTray[selector][0];
            for(int i=0;i<3;i++)
                marketTray[selector][i]=marketTray[selector][i+1];
            marketTray[selector][3] = temp;
        }
        else if (selector >= 3  && selector <= 6) {
            externalMarble = marketTray[0][selector-3];
            for(int i=0;i<2;i++)
                marketTray[i][selector-3]=marketTray[i+1][selector-3];
            marketTray[2][selector-3] = temp;
        }
        return marketTray;
    }

}