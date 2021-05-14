package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private Socket socket;
    private MainServer server;
    private ObjectInputStream inputStreamObj;
    private ObjectOutputStream outputStreamObj;
    private int clientID;
    private boolean active;
    private PingObserver pingObserver;

    public MainServer getServer() {
        return server;
    }

    public void setActive(boolean active) {
        this.active = active;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectInputStream getInputStream() {
        return inputStreamObj;
    }

    public ObjectOutputStream getOutputStreamObj() {
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
               actionHandler(input);
            }while(isActive() && !(input instanceof NickNameAction));
            while (isActive()) {
                 input = (SerializedMessage) inputStreamObj.readObject();
                 if(!pingHandler(input))
                    actionHandler(input);
            }
            outputStreamObj.close();
            inputStreamObj.close();
            socket.close();
        }
        //TODO tolgo client dal server e nel caso di partita in atto, lo tolgo
        catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean pingHandler(SerializedMessage input) {
        if(input instanceof PongMessage) {
            pingObserver.setResponse(true);
            return true;
        }
        return false;
    }

    private synchronized void actionHandler(SerializedMessage input) {
        if (input instanceof NickNameAction) {
            clientID = checkNickName((NickNameAction) input);
            //TODO altri casi di ritorno clientID non validi
            System.out.println("sto creando il pingObserver");
            pingObserver = new PingObserver(this);
            System.out.println("sto salvando il pingObserver");
            MainServer.getConnectionServer().addPingObserver(pingObserver);
            System.out.println("ho salvato il pingObserver");
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
                send(new RequestNumOfPlayers("Number of Player not valid. Please type a valid number [0 to 3]"));
            else {
                Lobby lobby = new Lobby(clientID, server.generateLobbyId(), num+1, server);
                //TODO controllo valido clientID
                System.out.println("Lobby di" + num + "giocatori creata con id: " + lobby.getLobbyID() + "." +
                        "inserisco l'host");
                lobby.insertPlayer(clientID);
                System.out.println("host inserito");
                send(new LobbyMessage("Lobby created. Wait for the other players to join!"));
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
                lobby.sendAll((SerializedMessage) new LobbyMessage(server.getNameFromId().get(id)+" is entered in the lobby"));
                lobby.insertPlayer(id);//TODO aggiungo nuovo client nella mappa ID-lobby
                return false;
            }
        }
        System.out.println("tutte le lobby sono piene, creo una nuova lobby");
        return true;
    }

    //TODO gestione disconnessione

    private int checkNickName(NickNameAction message) {
        int ID;
        System.out.println("sei dentro checknickname con: " + message.getMessage());
        for (String name : server.getNameFromId().values()) {
            System.out.println(name);
            if (message.getMessage().equals(name)) {
                ID = server.getIDFromName().get(name);
                System.out.println("nickname già scelto dall'utente: "+ID);
                if(server.getClientFromId().containsKey(ID)) {
                    System.out.println("l'utente " + ID + "è online. Il nuovo utente deve cambiare nickname");
                    return -1;
                }else if (server.getLobbyFromClientID().containsKey(ID)) {
                    System.out.println("l'utente" + ID + "non è collegato, ma esiste partita in cui giocava");
                    Lobby lobby = server.getLobbyFromClientID().get(ID);
                    if (lobby.getStateOfGame() == GameState.WAITING || lobby.getStateOfGame() == GameState.ONGOING) {
                        System.out.println("la partita è in corso, il giocatore può essere ricollegato");
                        //TODO domando client se vuole entrare in partita in corso
                        reconnectClient(ID, name);
                        lobby.insertPlayer(ID);
                        //TODO ricorda modifica lista virtual client in lobby quando disconnessione, gestisco poi riconessione e aggiunta
                        lobby.sendAll((SerializedMessage) new LobbyMessage(name + " is back in the game"));
                        return ID;
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

    public Socket getSocket() {
        return this.socket;
    }

    public int getClientId() {
        return clientID;
    }

    public PingObserver getPingObserver() {
        return pingObserver;
    }
}

