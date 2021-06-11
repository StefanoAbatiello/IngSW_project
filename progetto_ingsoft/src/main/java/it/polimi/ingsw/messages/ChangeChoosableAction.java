package it.polimi.ingsw.messages;

import java.util.ArrayList;

public class ChangeChoosableAction implements SerializedMessage{

    private ArrayList<String> newRes;

    public ChangeChoosableAction(ArrayList<String> newRes) {
        this.newRes=newRes;
    }

    public ArrayList<String> getNewRes() {
        return newRes;
    }
}
