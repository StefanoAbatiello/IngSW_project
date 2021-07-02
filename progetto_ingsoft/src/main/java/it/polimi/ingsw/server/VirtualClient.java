package it.polimi.ingsw.server;

import java.net.Socket;

public class VirtualClient {
    private final int id;
    private final String nickName;
    private Lobby lobby;
    private final ClientHandler clientHandler;

    /**
     * Constructor of Virtual Client, that is a reference of the client in server
     */
    public VirtualClient(int id, String name, ClientHandler clientHandler) {
        this.id=id;
        nickName=name;
        this.clientHandler=clientHandler;
    }

    /**
     *
     * @return nickname of the client
     */
    public String getNickName() {
        return nickName;
    }

    /**
     *
     * @return the lobby in which is the client
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     *
     * @return the id of the client
     */
    public int getID() {
        return id;
    }

    /**
     *
     * @param lobby is the lobby in which is the client
     */
    public void giveLobby(Lobby lobby) {
        this.lobby=lobby;
    }

    /**
     *
     * @return the clienthandler
     */
    public ClientHandler getClientHandler() {
        return clientHandler;
    }

}
