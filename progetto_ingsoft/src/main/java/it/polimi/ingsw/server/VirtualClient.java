package it.polimi.ingsw.server;

import java.net.Socket;

public class VirtualClient {
    private final int id;
    private final String nickName;
    private Lobby lobby;
    private final ClientHandler clientHandler;
    private final boolean turn =false;

    /**
     * Constructor of Virtual Client, that is a reference of the client in server
     */
    public VirtualClient(int id, String name, ClientHandler clientHandler) {
        this.id=id;
        nickName=name;
        this.clientHandler=clientHandler;
    }

    public String getNickName() {
        return nickName;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public int getID() {
        return id;
    }

    public void giveLobby(Lobby lobby) {
        this.lobby=lobby;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public boolean isTurn() {
        return turn;
    }
}
