package it.polimi.ingsw.model.personalboard;

public class BlackCross {

    private int crossPosition;

    public BlackCross() {
        this.crossPosition = 0;
    }

    /**
     * @return current cross position
     */
    public  int getCrossPosition() {
        return crossPosition;
    }

    /**
     * @param pos is the points gained by the cross
     * @return cross position after the updating
     */
    public  int updateBlackCross(int pos){
        crossPosition+=pos;
        return crossPosition;
    }

}
