package it.polimi.ingsw.model.personalboard;

public class FaithMarker {
    private int position=0;

    public int updatePosition() {
        this.position++;
        return this.position;
    }

    public int getPosition() {
        return this.position;
    }
}
