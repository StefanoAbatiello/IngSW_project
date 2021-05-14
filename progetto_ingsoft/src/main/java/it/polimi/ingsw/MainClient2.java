package it.polimi.ingsw;

import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MainClient2 {
    private String ip;
    private int port;
    private PongObserver pongObserver;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private Socket socket;
    private Scanner stdin;


    public ObjectInputStream getSocketIn() {
        return socketIn;
    }

    public ObjectOutputStream getSocketOut() {
        return socketOut;
    }

    public MainClient2(String ip, int port) {
        this.ip = ip;
        this.port = port;
        pongObserver = new PongObserver(this);
    }

    public static void main(String[] args) {
        MainClient2 client = new MainClient2("127.0.0.1", 1337);
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
        stdin = new Scanner(System.in);
        SerializedMessage input;
        try {
            while (true) {
                input = (SerializedMessage) socketIn.readObject();
                actionHandler(input, socketIn, socketOut);
            }
        } catch (ClassNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            System.out.println("Connection closed");
        } finally {
            stdin.close();
            socketIn.close();
            socketOut.close();
            socket.close();
        }
    }

    private void actionHandler(SerializedMessage input, ObjectInputStream socketIn, ObjectOutputStream socketOut) {
        Scanner stdin = new Scanner(System.in);
        if (input instanceof NickNameAction) {
            System.out.println(((NickNameAction) input).getMessage());
            try {
                String inputLine = stdin.nextLine();
                socketOut.writeObject(new NickNameAction(inputLine));
                socketOut.flush();

            } catch (java.io.IOException e) {
                System.out.println("Connection closed");
            }
        }
        if (input instanceof RequestNumOfPlayers) {
            System.out.println(((RequestNumOfPlayers) input).getMessage());
            String inputLine = stdin.next();
            try {
                try {
                    socketOut.writeObject(new NumOfPlayersAction(Integer.parseInt(inputLine)));
                    socketOut.flush();
                } catch (NumberFormatException e) {
                    socketOut.writeObject(new NumOfPlayersAction(6));
                    socketOut.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (input instanceof LobbyMessage) {
            System.out.println(((LobbyMessage) input).getMessage());
        }
        if(input instanceof PingMessage) {
            try {
                System.out.println("ho ricevuto il ping");
                socketOut.writeObject(new PongMessage());
                System.out.println("ho inviato il pong");
                socketOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!pongObserver.isStarted()) {
                System.out.println("era il primo ping, faccio partire il pongObserver");
                new Thread(pongObserver).start();
                System.out.println("pongObserver partito");
                pongObserver.setResponse(false);
            } else
                pongObserver.setResponse(true);
        }
    }

    public void disconnect() {
        System.out.println("sto chiudendo il socket");
        stdin.close();
        try {
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
