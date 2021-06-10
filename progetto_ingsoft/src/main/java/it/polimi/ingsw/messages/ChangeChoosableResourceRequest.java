package it.polimi.ingsw.messages;

public class ChangeChoosableResourceRequest implements SerializedMessage {

    private int num;
    private String message;

    public ChangeChoosableResourceRequest(int num, String message) {
        this.num=num;
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public int getNum() {
        return num;
    }
}
