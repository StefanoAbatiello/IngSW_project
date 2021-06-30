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
        if (result.containsKey(-1))
            val=-1;
        if(result.containsKey(0))
            val=0;
        else if(result.containsKey(1))
            val=1;
        else if (result.containsKey(2))
            val=2;
        message=result.get(val);

    }
}
