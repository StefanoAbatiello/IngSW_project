package it.polimi.ingsw.messages.answerMessages;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class PersonalBoardChangeMessage {

    private Optional<Map<Integer,Boolean>> cardID;
    private Optional<ArrayList<String>[]> warehouse;
    private Optional<Integer> faithPosition;

    public Optional<Map<Integer, Boolean>> getCardID() {
        return cardID;
    }

    public Optional<ArrayList<String>[]> getWarehouse() {
        return warehouse;
    }

    public Optional<Integer> getFaithPosition() {
        return faithPosition;
    }

    public PersonalBoardChangeMessage(Map<Integer, Boolean> cardsId) {
        this.cardID= Optional.ofNullable(cardsId);
    }

    public PersonalBoardChangeMessage(ArrayList<String>[] warehouse){
        this.warehouse=Optional.ofNullable(warehouse);
    }

    public PersonalBoardChangeMessage(int faithPosition){
        this.faithPosition=Optional.of(faithPosition);
    }
}
