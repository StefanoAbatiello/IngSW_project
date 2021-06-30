package it.polimi.ingsw.messages;

import java.util.Map;

public class LorenzoActionMessage implements SerializedMessage {

    public int getVal() {
        return val;
    }

    public String getMessage() {
        return message;
    }

    int val;
    String message;

    public LorenzoActionMessage(Map<Integer, String> result) {
        if(result.keySet().stream().findAny().isPresent())
            val=result.keySet().stream().findAny().get();
        if (!result.isEmpty())
            message=result.values().stream().toString();
    }
}
