package it.polimi.ingsw;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private int ClientID;
    private boolean active;


    public ClientHandler(Socket socket){
        this.socket = socket;
        try {
            inputStream=new ObjectInputStream(socket.getInputStream());
            outputStream=new ObjectOutputStream(socket.getOutputStream());
            active=true;
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
            while (isActive()){
                SerializedMessage input= (SerializedMessage) inputStream.readObject();
                actionHandler(input);
            }
        }
        //TODO tolgo client dal server e nel caso di partita in atto, lo tolgo
        catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void actionHandler(SerializedMessage input) {
        //TODO gestione di tutte le azioni
    }

    //TODO gestione disconnessione

}

