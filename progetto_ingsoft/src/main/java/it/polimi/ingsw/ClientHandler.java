package it.polimi.ingsw;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

class ClientHandler implements Runnable {
    private Socket socket;
    private MainServer server;
    private ObjectInputStream inputStreamObj;
    private ObjectOutputStream outputStreamObj;
    private int clientID;
    private boolean active;

    public InputStream getInputStream() {
        return inputStreamObj;
    }

    public OutputStream getOutputStreamObj() {
        return outputStreamObj;
    }



    public ClientHandler(Socket socket, MainServer server) {
        System.out.println("sto creando il CH");
        this.socket = socket;
        this.server = server;
        System.out.println("socket");
        try {
            OutputStream output= socket.getOutputStream();
            outputStreamObj = new ObjectOutputStream(output);
            InputStream input =socket.getInputStream();
            inputStreamObj = new ObjectInputStream(input) ;
            System.out.println("ho creato gli stream");
            active = true;
        } catch (IOException e) {
            System.err.println("Error during socket creation");
        }
    }

    public synchronized boolean isActive() {
        return active;
    }

    @Override
    public void run() {
        try {
            while (isActive()) {
                SerializedMessage input = (SerializedMessage) inputStreamObj.readObject();
                actionHandler(input);
            }
        }
        //TODO tolgo client dal server e nel caso di partita in atto, lo tolgo
        catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void actionHandler(SerializedMessage input) {
        if(input instanceof NickNameAction){
            clientID =checkNickName((NickNameAction) input);
            if(clientID >=0) {
                if (checkFirstPlayer(clientID))
                    send(new RequestNumOfPlayers("You are the host of a new lobby."
                            +" Choose how many players you want to challenge [0 to 3]"));
            }
        }
        else if(input instanceof NumOfPlayersAction){
            int num=((NumOfPlayersAction)input).getPlayersNum();
            if(num<0 || num >3)
                send(new RequestNumOfPlayers("Number of Player not valid."
                        +" Please type a valid number [0 to 3]"));
            else
                new Lobby(server.generateLobbyId(), num, server);
                //TODO dopo la creazione della lobby il giocatore deve essere inserito
        }
    }

    private boolean checkFirstPlayer(int id) {
        if(server.getFromClientIDToLobby().containsKey(id))
            return false;
        ArrayList<Lobby> lobbies= (ArrayList<Lobby>) server.getFromClientIDToLobby().values().stream().distinct().collect(Collectors.toList());
        for(Lobby lobby:lobbies) {
            if (!lobby.isLobbyFull()) {
                lobby.insertPlayer(id);
                return false;
            }
        }
        return true;
    }

    //TODO gestione disconnessione

    private int checkNickName(NickNameAction message) {
        int ID;
        System.out.println("sei dentro checknickname"+message.getNickname());
        for (String name : server.getNameFromId().values()) {
            System.out.println(name);
            if (message.getNickname().equals(name)) {
                ID = server.getIDfromName().get(name);
                if (server.getFromClientIDToLobby().containsKey(ID)) ;
                    Lobby lobby = server.getFromClientIDToLobby().get(ID);
                    if (lobby.getStateOfGame() == GameState.WAITING || lobby.getStateOfGame() == GameState.ONGOING) {
                        if (server.getClientFromId().containsKey(ID))
                            return -1;
                        //TODO domando client se vuole entrare in partita in corso
                        reconnectClient(ID,name);
                        server.getClientFromId().get(ID).giveLobby(lobby);
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
        VirtualClient newClient = new VirtualClient(ID, name, this.socket, this);
        server.getClientFromId().put(ID,newClient);
        return ID;
    }

    private int reconnectClient(int ID, String name) {
        VirtualClient newClient = new VirtualClient(ID, name, this.socket, this);
        server.getClientFromId().put(ID,newClient);
        return ID;
    }

    public void send(SerializedMessage message){
        try {
            outputStreamObj.writeObject(message);
            outputStreamObj.flush();
        } catch (IOException e) {
            e.printStackTrace();//TODO sistemare eccezioni
        }
    }

}

