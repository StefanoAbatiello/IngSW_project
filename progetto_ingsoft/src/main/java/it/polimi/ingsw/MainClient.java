package it.polimi.ingsw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
        System.out.println("insert nickname: ");
        ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());

        Scanner stdin = new Scanner(System.in);
        try {
            while (true) {
                String inputLine = stdin.nextLine();
                socketOut.writeObject(new NickNameAction(inputLine));
                socketOut.flush();
            }
        } catch(NoSuchElementException e) {
            System.out.println("Connection closed");
        } finally {
            stdin.close();
            socketIn.close();
            socketOut.close();
            socket.close();
        }
    }
}

