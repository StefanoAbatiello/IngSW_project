package it.polimi.ingsw.server;

import it.polimi.ingsw.Sender;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable, Sender {
    private final Socket socket;
    private final MainServer server;
    private ObjectInputStream inputStreamObj;
    private ObjectOutputStream outputStreamObj;
    private int clientID;
    private boolean active;
    private PongObserver pingObserver;

    public MainServer getServer() {
        return server;
    }

    /**
     * @param active sets the active connection field of this SocketServer object
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *Constructor of Client Handler which generates output and input stream of the socket
     */
    public ClientHandler(Socket socket, MainServer server) {
        //System.out.println("Collegamento socket creato");[Debug]
        this.socket = socket;
        this.server = server;
        this.clientID = 0;
        try {
            OutputStream output= socket.getOutputStream();
            outputStreamObj = new ObjectOutputStream(output);
            InputStream input =socket.getInputStream();
            inputStreamObj = new ObjectInputStream(input) ;
            //System.out.println("ho creato gli stream");[Debug]
            active = true;
        } catch (IOException e) {
            System.err.println("Error during socket creation");
        }
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Send a message to client and read the output stream as long as the socket is open
     */
    @Override
    public void run() {
        try {
            SerializedMessage message;
            do{
                send(new NickNameAction("Please choose a nickname"));
                message = streamReader();
                if (message instanceof NickNameAction) {
                    nickNameHandler( message);
                }
            }while(isActive() && !(clientID > 0));
            while (isActive()) {
                System.out.println("aspetto un messaggio");//[Debug]
                message = streamReader();
                System.out.println("ho ricevuto un messaggio da "+ server.getNameFromId().get(clientID));//[Debug]
                if(message instanceof NickNameAction)
                    send(new LobbyMessage("Action not valid now"));
                else if(!pingHandler(message) && !playersNumberHandler(message)){
                    if (!server.getLobbyFromClientID().containsKey(clientID) || server.getLobbyFromClientID().get(clientID).getStateOfGame()==GameState.WAITING || server.getLobbyFromClientID().get(clientID).getStateOfGame()==GameState.ENDED)
                        send(new LobbyMessage("Action not valid now"));
                    else
                        server.getClientFromId().get(clientID).getLobby().actionHandler(message, clientID);
                }
            }
            outputStreamObj.close();
            inputStreamObj.close();
            socket.close();
        }
        catch (IOException e) {
            System.err.println("Client not reachable");
        }
    }

    /**
     * @return try to read a message from input stream of the socket
     */
    private SerializedMessage streamReader(){
        SerializedMessage message;
        try {
             message = (SerializedMessage) inputStreamObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client not reachable");
            active=false;
            message = new quitMessage();
        }
        return message;
    }

    /**
     * @param message is message sent by the client
     * @return false if message is not a ping
     */
    private synchronized boolean pingHandler(SerializedMessage message) {
        if(message instanceof PingMessage) {
            System.out.println("ping message client "+clientID);//[Debug]
            this.pingObserver.setResponse();
            return true;
        }
        return false;
    }
    /**
     * This method handles messages related to the client Nickname
     * @param message is message sent by the client
     * @return true if the message received is a NicknameAction
     */
    private synchronized boolean nickNameHandler(SerializedMessage message) {
        if (message instanceof NickNameAction) {
                String nickname = ((NickNameAction) message).getMessage();
                if (nickname != null) {
                    clientID = server.checkNickName(nickname, this);
                    if (clientID > 0) {
                        pingObserver = new PongObserver(this);
                        server.getConnectionServer().addPingObserver(pingObserver);
                        //System.out.println("Ho creato il pongObserver");[Debug]
                        if (!server.getLobbyFromClientID().containsKey(clientID) && !server.findEmptyLobby(clientID)) {
                            send(new RequestNumOfPlayers("YOU ARE THE HOST OF A NEW LOBBY"
                                    + " CHOOSE HOW MANY PLAYERS YOU WANT TO CHALLENGE [0 to 3]"));
                        }
                    } else if (clientID == -2) {
                        send(new NickNameAction("Nickname already taken." + " Please choose another one:"));
                    }
                } else
                    send(new LobbyMessage("Nickname not valid"));
                return true;
            }
        return false;
    }

    /**
     * This method handles messages related to the lobby's dimension at its creation
     * @param message is message sent by the client
     * @return true if the message received is a PlayersNumberAnswer
     */
    private synchronized boolean playersNumberHandler(SerializedMessage message) {
        if (message instanceof NumOfPlayersAnswer) {
            if (!server.getLobbyFromClientID().containsKey(clientID)) {
                int num = ((NumOfPlayersAnswer) message).getPlayersNum();
                System.out.println("Il client " + clientID + " vuole creare una nuova lobby di " + (num + 1) + " giocatori");//[Debug]
                if (num < 0 || num > 3)
                    send(new RequestNumOfPlayers("Number of Player not valid. Please type a valid number [0 to 3]"));
                else {
                    if (num != 0) {
                        send(new LobbyMessage("You are creating a new lobby with other " + num + " players"));
                    } else
                        send(new LobbyMessage("You are creating a new single player game"));
                    Lobby lobby = new Lobby(server.generateLobbyId(), num + 1, server);
                    send(new LobbyMessage("Lobby created. Wait for the other players to join!"));
                    System.out.println("Lobby di" + num + "giocatori creata con id: " + lobby.getLobbyID() + "." +
                            "inserisco l'host");//[Debug]
                    lobby.insertPlayer(clientID);
                }
                return true;
            }
            send(new LobbyMessage("Action not valid now"));
            return true;
        }
        return false;
    }



    /**
     * @param message to send to the client from output stream of the socket
     */
    public synchronized void send(SerializedMessage message){
        try {
            //System.out.println("sto inviando il messaggio");[Debug]
            outputStreamObj.writeObject(message);
            outputStreamObj.flush();
            //System.out.println("messaggio inviato");[Debug]
        } catch (IOException e) {
            System.err.println("client not reachable(send)");
            server.disconnectClient(clientID);
            e.printStackTrace();
        }
    }

    public int getClientId() {
        return clientID;
    }

    public PongObserver getPingObserver() {
        return pingObserver;
    }

    public Socket getSocket() {
        return this.socket;
    }
}

