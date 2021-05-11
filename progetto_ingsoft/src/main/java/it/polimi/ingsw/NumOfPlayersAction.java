package it.polimi.ingsw;

public class NumOfPlayersAction implements SerializedMessage {

    private int playersNum;

    public NumOfPlayersAction(int playersNum) {
        this.playersNum = playersNum;
    }

    public int getPlayersNum() {
        return playersNum;
    }

}
