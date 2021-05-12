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
            SerializedMessage input;
            do{
               send(new NickNameAction("Please choose a nickname"));
               input = (SerializedMessage) inputStreamObj.readObject();
            }while(isActive() && !(input instanceof NickNameAction));

            while (isActive()) {
                input = (SerializedMessage) inputStreamObj.readObject();
                actionHandler(input);
            }
        }
        //TODO tolgo client dal server e nel caso di partita in atto, lo tolgo
        catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void actionHandler(SerializedMessage input) {
        if (input instanceof NickNameAction) {
            clientID = checkNickName((NickNameAction) input);
            //TODO altri casi di ritorno clientID non validi
            if (clientID >= 0) {
                if (checkFirstPlayer(clientID))
                    send(new RequestNumOfPlayers("You are the host of a new lobby."
                            + " Choose how many players you want to challenge [0 to 3]"));
            }else
                send(new NickNameAction("Nickname already taken." + " Please choose another one:"));
        }
        if (input instanceof NumOfPlayersAction) {
            int num = ((NumOfPlayersAction) input).getPlayersNum();
            System.out.println("ho ricevuto: " +num);
            if (num < 0 || num > 3)
                send(new RequestNumOfPlayers("Number of Player not valid." + " Please type a valid number [0 to 3]"));
            else {
                Lobby lobby = new Lobby(clientID, server.generateLobbyId(), num, server);
                //TODO controllo valido clientID
                System.out.println("sono prima di insert");
                lobby.insertPlayer(clientID);
                System.out.println("ho fatto insert");
                send(new LobbyCreatedMessage("Lobby created. Wait for the other players to join!"));
                System.out.println("New lobby created, lobby ID: " + lobby.getLobbyID());
            }
        }
    }



    private boolean checkFirstPlayer(int id) {
        System.out.println("sei dentro check first player");
        if(server.getLobbyFromClientID().containsKey(id)) {
            System.out.println("client già in una lobby");
            return false;
        }
        ArrayList<Lobby> lobbies= (ArrayList<Lobby>) server.getLobbyFromClientID().values().stream().distinct().collect(Collectors.toList());
        for(Lobby lobby:lobbies) {
            if (!lobby.isLobbyFull()) {
                System.out.println("c'è una lobby libera");
                lobby.insertPlayer(id);
                return false;
            }
        }
        System.out.println("tutte le lobby sono piene, nuova lobby");
        return true;
    }

    //TODO gestione disconnessione

    private int checkNickName(NickNameAction message) {
        int ID;
        System.out.println("sei dentro checknickname con: " +message.getMessage());
        for (String name : server.getNameFromId().values()) {
            System.out.println(name);
            if (message.getMessage().equals(name)) {
                ID = server.getIDFromName().get(name);
                System.out.println("hai trovato nome uguale, prendi ID di quello già presente");
                if(server.getClientFromId().containsKey(ID)) {
                    System.out.println("quello già presente è collegato, cambia nome");
                    return -1;
                }else if (server.getLobbyFromClientID().containsKey(ID)) {
                    System.out.println("quello già presente non è collegato, ma esiste partita in cui giocava");
                    Lobby lobby = server.getLobbyFromClientID().get(ID);
                    if (lobby.getStateOfGame() == GameState.WAITING || lobby.getStateOfGame() == GameState.ONGOING) {
                        System.out.println("la partita è in corso, il giocatore può essere ricollegato");
                        //TODO domando client se vuole entrare in partita in corso
                        reconnectClient(ID, name);
                        //TODO ricorda modifica lista virtual client in lobby quando disconnessione, gestisco poi riconessione e aggiunta
                        lobby.sendAll(name + new ReconnessionMessage(" is back in the game"));
                    } else
                        reconnectClient(ID, name);
                    return ID;
                } else {
                    ID = connectClient(message.getMessage());
                    return ID;
                }
            }
        }
        ID = connectClient(message.getMessage());
        return ID;

}

    private int connectClient(String name) {
        int ID= server.getNameFromId().size() +1;
        server.getNameFromId().put(ID,name);
        server.getIDFromName().put(name,ID);
        VirtualClient newClient = new VirtualClient(ID, name, this.socket);
        server.getClientFromId().put(ID,newClient);
        return ID;
    }

    private int reconnectClient(int ID, String name) {
        VirtualClient newClient = new VirtualClient(ID, name, this.socket);
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

