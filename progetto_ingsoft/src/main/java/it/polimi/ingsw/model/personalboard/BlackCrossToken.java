package it.polimi.ingsw.model.personalboard;

public class BlackCrossToken {
    private int crossPosition;

    /**
     * @return current cross position
     */
    public int getCrossPosition() {
        return crossPosition;
    }

    /**
     * @param pos is the future position of cross
     * @return cross position after the updating
     */
    public int UpdateBlackCross(int pos){
        crossPosition=pos;
        return crossPosition;
    }

}
