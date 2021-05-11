package it.polimi.ingsw;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ClientHandler implements Runnable {
    private Socket socket;
    private MainServer server;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private int ClientID;
    private boolean active;


    public ClientHandler(Socket socket, MainServer server) {
        this.socket = socket;
        this.server = server;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            active = true;
        } catch (IOException e) {
            System.err.println("Error during client creation");
        }
    }

    public synchronized boolean isActive() {
        return active;
    }

    @Override
    public void run() {
        try {
            while (isActive()) {

                SerializedMessage input = (SerializedMessage) inputStream.readObject();
                actionHandler((GameMessage) input);
            }
        }
        //TODO tolgo client dal server e nel caso di partita in atto, lo tolgo
        catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void actionHandler(PreGameMessage input) {
        if(input instanceof NickNameAction){
            if(checkNickName((NickNameAction) input)>=0)
                checkFirstPlayer();
        }

    }
    private void actionHandler(GameMessage input) {


    }

    private void checkFirstPlayer() {
    }

    //TODO gestione disconnessione

    private int checkNickName(NickNameAction message) {
        int ID;
        for (String name : server.getNameFromId().values()) {
            if (message.getNickname() == name) {
                ID = server.getIDfromName().get(name);
                if (server.getFromClientIDToLobby().containsKey(ID)) ;
                    Lobby lobby = server.getFromClientIDToLobby().get(ID);
                    if (lobby.getStateOfGame() == GameState.WAITING || lobby.getStateOfGame() == GameState.ONGOING) {
                        if (server.getClientFromId().containsKey(ID))
                            return -1;
                        //TODO domando client se vuole entrare in partita in corso
                        reconnectClient(ID,name);
                        lobby.sendAll(name + DefaultMessages.reconnession);
                    }else
                        reconnectClient(ID,name);
                    return ID;
            }
        }
        ID=connectClient(message.getNickname());
        return ID;

}

    private int connectClient(String name) {
        int ID= server.getNameFromId().size() +1;
        server.getNameFromId().put(ID,name);
        server.getIDfromName().put(name,ID);
        VirtualClient newClient = new VirtualClient(ID, name, this.socket);
        server.getClientFromId().put(ID,newClient);
        return ID;
    }

    private int reconnectClient(int ID, String name) {
        VirtualClient newClient = new VirtualClient(ID, name, this.socket);
        server.getClientFromId().put(ID,newClient);

        return ID;
    }
    }

