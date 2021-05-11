package it.polimi.ingsw;

import java.net.Socket;

public class VirtualClient {
    private Socket socket;
    private int id;
    private String nickName;

    public VirtualClient(int id, String name, Socket socket) {
        this.socket=socket;
        this.id=id;
        nickName=name;
    }

    public GameHandler getGameHandler() {
        return new GameHandler();
    }

}
