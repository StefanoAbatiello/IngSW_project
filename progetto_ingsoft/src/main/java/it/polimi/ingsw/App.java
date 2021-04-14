package it.polimi.ingsw;

import it.polimi.ingsw.exceptions.InvalidSlotException;
import it.polimi.ingsw.model.cards.DevCard;
import it.polimi.ingsw.model.personalboard.DevCardSlot;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        DevCardSlot devCardSlot=new DevCardSlot();
        DevCard devCard=new DevCard();
        try {
            devCardSlot.overlap(devCard,2);
        } catch (InvalidSlotException e) {
            e.printStackTrace();
        }

        System.out.println(devCardSlot.getActiveCards().size());
    }
}
