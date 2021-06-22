package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Points;
import it.polimi.ingsw.model.cards.DevCard;
import java.util.ArrayList;

public class DevCardSlot implements Points {

    /**
     * this is the structure where development card are set
     */
    private final ArrayList<DevCard>[] slot;

    /**
     * this is a support ArrayList which contain al the active cards(the ones with the highest level)
     */
    private final ArrayList<DevCard> ActiveCards;

    /**
     * this is the amount of points gained from the cards bought
     */
    private int VictoryPoints;

    public DevCardSlot() {
        this.slot = new ArrayList[3];
        for (int i=0;i<3;i++)
            slot[i]=new ArrayList<>();
        ActiveCards=new ArrayList<>();
        VictoryPoints=0;
    }


    /**
     * @return actual slot at the call moment
     */
    public ArrayList<DevCard>[] getSlot() {
        return slot;
    }

    /**
     * @return an Arraylist containing all player's development cards
     */
    public ArrayList<DevCard> getDevCards(){
        ArrayList<DevCard> cards= new ArrayList<>();
        for(int i=0;i<3;i++)
            cards.addAll(slot[i]);
        return cards;
    }

    /**
     * @param devCard is the card that is added in slot
     * @param slotID is the selected row
     * @return slot after overlapped development card, if it is possible
     */
    public ArrayList<DevCard>[] overlap(DevCard devCard, int slotID)  {
        for(int row=0;row<3;row++){
                if(!slot[slotID].isEmpty()){
                    if(devCard.getLevel()==slot[slotID].get(row).getLevel()+1 && slot[slotID].get(row).isActive()) {
                        slot[slotID].get(row).setActive(false);
                        slot[slotID].add(devCard);
                        slot[slotID].get(row + 1).setActive(true);
                        return slot;
                    }
                }
                else if (devCard.getLevel()==1&&slot[slotID].isEmpty()){
                    slot[slotID].add(devCard);
                    slot[slotID].get(0).setActive(true);
                    return slot;
                }
        }
        return slot;
    }

    /**
     * @return an ArrayList containing only active cards in game
     */
    public ArrayList<DevCard> getActiveCards() {
        for(int i=0;i<=2;i++){
            for (DevCard dev : slot[i]) {
                if (dev.isActive())
                    ActiveCards.add(dev);
            }
        }
        return ActiveCards;
    }

    /**
     * @return at the end of game points aggregated in all development cards
     */
    @Override
    public int getPoints(){
        for (int i=0;i<=2;i++){
                for(DevCard dev:slot[i])
                       VictoryPoints = (VictoryPoints + dev.getPoints());
        }
        return VictoryPoints;
    }

}
