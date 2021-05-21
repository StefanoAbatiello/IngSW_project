package it.polimi.ingsw.messages;

public class InitialResourceMessage implements SerializedMessage {

    private String resource;
    private int shelfNum;

    public InitialResourceMessage(String resource, int shelfNum) {
        this.resource=resource;
        this.shelfNum=shelfNum;
    }

    public String getResource() {
        return resource;
    }

    public int getShelfNum() {
        return shelfNum;
    }
}
