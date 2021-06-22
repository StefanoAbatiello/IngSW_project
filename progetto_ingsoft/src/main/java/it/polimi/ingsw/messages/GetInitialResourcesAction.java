package it.polimi.ingsw.messages;

public class GetInitialResourcesAction implements SerializedMessage {

    private final String message;
    private final int numRes;

    public GetInitialResourcesAction(String s,int num) {
        this.message=s;
        this.numRes=num;
    }

    public int getNumRes(){
        return numRes;
    }

    public String getMessage() {
        return message;
    }
}
