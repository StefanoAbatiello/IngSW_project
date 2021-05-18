package it.polimi.ingsw.server;

import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.server.Lobby;

import java.net.Socket;

public class VirtualClient {
    private Socket socket;
    private int id;
    private String nickName;
    private Lobby lobby;
    private ClientHandler clientHandler;
    private boolean myTurn=false;

    public VirtualClient(int id, String name, Socket socket, ClientHandler clientHandler) {
        this.socket=socket;
        this.id=id;
        nickName=name;
        this.clientHandler=clientHandler;
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

    public boolean isMyTurn() {
        return myTurn;
    }
}
