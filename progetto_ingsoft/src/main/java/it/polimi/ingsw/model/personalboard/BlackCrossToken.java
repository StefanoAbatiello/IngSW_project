package it.polimi.ingsw.model.personalboard;

public class BlackCrossToken {
    private int crossPosition;

    public BlackCrossToken() {
        this.crossPosition = 0;
    }

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
        if(pos<=24)
            crossPosition=pos;
        return crossPosition;
    }

}
