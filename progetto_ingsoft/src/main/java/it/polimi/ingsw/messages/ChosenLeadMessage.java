package it.polimi.ingsw.messages;

import java.util.ArrayList;

public class ChosenLeadMessage implements GameMessage{

    ArrayList<Integer> chosenId;

    public ChosenLeadMessage(ArrayList<Integer> chosenId) {
        this.chosenId=chosenId;
    }

    public ArrayList<Integer> getChosenId() {
        return chosenId;
    }

}
