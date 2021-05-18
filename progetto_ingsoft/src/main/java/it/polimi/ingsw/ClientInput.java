package it.polimi.ingsw;

import it.polimi.ingsw.messages.NickNameAction;
import it.polimi.ingsw.messages.NumOfPlayersAction;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientInput implements Runnable{

    private MainClient client;
    private final ObjectOutputStream socketOut;

    public ClientInput(MainClient client, ObjectOutputStream socketOut) {
        this.client = client;
        this.socketOut = socketOut;
    }

    @Override
    public void run() {
        System.out.println("sto leggendo da tastiera");
        Scanner scanner=new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            inputHandler(input);
        }
    }

    private void inputHandler(String input) {
        if (input.equalsIgnoreCase("quit")) {
            client.disconnect();
        } else if (input.startsWith("Nickname:")) {
            try {
                System.out.println(input.replace("Nickname:", ""));
                socketOut.writeObject(new NickNameAction(input.replace("Nickname:", "")));
                socketOut.flush();
            } catch (java.io.IOException e) {
                System.out.println("Connection closed");
                client.disconnect();
            }
        } else if(input.startsWith("PlayersNumber:")){
            try {
                String string = input.replace("PlayersNumber:", "");
                try{
                    int num = Integer.parseInt(string);
                    System.out.println("stai creando una lobby con altri "+num+" giocatori");
                    socketOut.writeObject(new NumOfPlayersAction(num));
                    socketOut.flush();
                } catch (NumberFormatException e) {
                    socketOut.writeObject(new NumOfPlayersAction(6));
                    socketOut.flush();
                }
            } catch (java.io.IOException e) {
                System.out.println("Connection closed");
                client.disconnect();
            }
        } else if(input.startsWith("InitialResource:")){
                String resource= input.replace("InitialResource:","");
                System.out.println("In which shelf do you want to put your Resource? [0 to 2]");
                System.out.println("Type: \"SelectShelf:[Shelf Number]");
                //String string = scanner.nextLine();
        }
            System.out.println("Input not valid, type again");
    }
}
