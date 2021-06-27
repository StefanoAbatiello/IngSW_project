package it.polimi.ingsw.messages;

public class ActivePopeMeetingMessage implements SerializedMessage {

    private final int meetingNumber;

    public ActivePopeMeetingMessage(int meetingNumber) {
        this.meetingNumber=meetingNumber;
    }

    public int getMeetingNumber() {
        return meetingNumber;
    }
}
