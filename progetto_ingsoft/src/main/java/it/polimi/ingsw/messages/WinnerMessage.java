package it.polimi.ingsw.messages;

public class WinnerMessage implements SerializedMessage {

    private String message;

    public WinnerMessage(String message) {
        this.message=message;
    }

    public String getMessage() {
        return message;
    }
}
