package it.polimi.ingsw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MainClient {
    private String ip;
    private int port;

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
        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());

        Scanner stdin = new Scanner(System.in);
        SerializedMessage input;
        while (true){
            try {
                input = (SerializedMessage) socketIn.readObject();
                actionHandler(input, socketIn, socketOut);
            } catch (ClassNotFoundException | NoSuchElementException e ) {
                e.printStackTrace();
                System.out.println("Connection closed");
            }

            finally {
                stdin.close();
                socketIn.close();
                socketOut.close();
                socket.close();
        }
    }}

    private void actionHandler(SerializedMessage input,ObjectInputStream socketIn, ObjectOutputStream socketOut) {
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
                    } catch (NumberFormatException e) {
                        socketOut.writeObject(new NumOfPlayersAction(6));

                    }
                    socketOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input instanceof LobbyCreatedMessage) {
                System.out.println(((LobbyCreatedMessage) input).getMessage());

            }
        }
    }



