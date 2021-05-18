package it.polimi.ingsw.messages;

public class InitialResourceMessage {

    private String resource;
    private int shelfNum;

    public InitialResourceMessage(String resource, int shelfNum) {
        this.resource=resource;
        this.shelfNum=shelfNum;
    }
}
