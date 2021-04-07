package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.InvalidInputException;
import it.polimi.ingsw.model.cards.DevCard;

import java.util.ArrayList;

public class DevCardSlot {
    private int slotID;
    private DevCard[][] slot;
    private ArrayList<DevCard> ActiveCards;
    private int VictoryPoints;

    public DevCardSlot() {
        this.slot = new DevCard[3][3];
        VictoryPoints=0;
    }


    /**
     * @return actual slot at the call moment
     */
    public DevCard[][] getSlot() {
        return slot;
    }


    /**
     * @param devCard is the card that is added in slot
     * @param slotID is the selected row
     * @return slot after overlapped devcard, if it is possible
     * @throws InvalidInputException if selected slot is not between 0 and 2
     * @throws NullPointerException if any card is not given as input
     */
    public DevCard[][] overlap(DevCard devCard,int slotID) throws InvalidInputException, NullPointerException {
        if(slotID<0||slotID>2){
            throw new InvalidInputException();
        }
        if(devCard==null)
            throw new NullPointerException("Insert card in input");
        for(int j=0;j<2;j++){
                if((devCard.getDevCardLevel()==slot[slotID][j].getDevCardLevel()+1) && (slot[slotID][j].isActive())){
                    slot[slotID][j].setActive(false);
                    slot[slotID][j+1]=devCard;
                    slot[slotID][j+1].setActive(true);
                }
                else if (devCard.getDevCardLevel()==1){
                    slot[slotID][0]= devCard;
                    slot[slotID][0].setActive(true);
                }
        }
        return slot;
    }

    /**
     * @return only active cards in game
     */
    public ArrayList<DevCard> getActiveCards() {
        for(int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                if(slot[i][j].isActive())
                    ActiveCards.add(slot[i][j]);
            }
        }
        return ActiveCards;
    }

    /**
     * @return at the end of game points aggregated in all development cards
     */
    public int getVictoryPoints(){
        for (int i=0;i<2;i++){
            for (int j=0;j<2;j++) {
                VictoryPoints = VictoryPoints + slot[i][j].getDevCardPoint();
            }
        }
        return VictoryPoints;
    }


}
