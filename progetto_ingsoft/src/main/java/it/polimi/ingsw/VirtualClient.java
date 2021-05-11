package it.polimi.ingsw;

import java.net.Socket;

public class VirtualClient {
    public VirtualClient(int id, String name, Socket socket) {
    }

    public GameHandler getGameHandler() {
        return new GameHandler();
    }
}
