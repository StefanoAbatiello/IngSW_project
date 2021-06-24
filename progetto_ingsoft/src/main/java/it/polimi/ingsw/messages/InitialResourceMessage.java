package it.polimi.ingsw.messages;

import java.util.Map;

public class InitialResourceMessage implements SerializedMessage {

    private Map<Integer,String> resource;
    private Map<Integer,Integer> shelfNum;

    public InitialResourceMessage(Map<Integer,String> resource, Map<Integer,Integer> shelfNum) {
        this.resource=resource;
        this.shelfNum=shelfNum;
    }

    public Map<Integer,String> getResource() {
        return resource;
    }

    public Map<Integer,Integer> getShelfNum() {
        return shelfNum;
    }
}
