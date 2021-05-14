package it.polimi.ingsw;

import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MainClient {
    private String ip;
    private int port;
    private PongObserver pongObserver;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private Socket socket;
    private static ClientInput keyboardReader;

    public ObjectInputStream getSocketIn() {
        return socketIn;
    }

    public ObjectOutputStream getSocketOut() {
        return socketOut;
    }

    public MainClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) {
        MainClient client = new MainClient("127.0.0.1", 1337);
        try {
            client.startClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void startClient() throws IOException {
        socket = new Socket(ip, port);
        System.out.println("Connection established");
        socketIn = new ObjectInputStream(socket.getInputStream());
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        keyboardReader=new ClientInput(this, socketOut);
        new Thread(keyboardReader).start();
        pongObserver = new PongObserver(this, socketOut);
        SerializedMessage input;
        try {
            while (true) {
                input = (SerializedMessage) socketIn.readObject();
                actionHandler(input, socketIn, socketOut);
            }
        } catch (ClassNotFoundException | NoSuchElementException e) {
            System.out.println("Connection closed");
            disconnect();
        } finally {
            disconnect();
        }
    }

    private void actionHandler(SerializedMessage input, ObjectInputStream socketIn, ObjectOutputStream socketOut) {
        if (input instanceof NickNameAction) {
            System.out.println(((NickNameAction) input).getMessage());
            System.out.println("Type \"Nickname: [your nickname]\"");
        }
        if (input instanceof RequestNumOfPlayers) {
            System.out.println(((RequestNumOfPlayers) input).getMessage());
            System.out.println("Type \"PlayersNumber: [num of player]\"");
        }
        if (input instanceof LobbyMessage) {
            System.out.println(((LobbyMessage) input).getMessage());
        }
        if(input instanceof PingMessage) {
            if(!pongObserver.isStarted()) {
                System.out.println("era il primo ping, faccio partire il pongObserver");
                new Thread(pongObserver).start();
                System.out.println("pongObserver partito");
            } else
                pongObserver.setResponse(true);
        }
    }

    public void disconnect() {
        System.out.println("sto chiudendo il socket");
        try {
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
