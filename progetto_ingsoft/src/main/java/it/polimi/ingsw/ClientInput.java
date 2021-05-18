package it.polimi.ingsw;

import it.polimi.ingsw.messages.ChosenLeadMessage;
import it.polimi.ingsw.messages.InitialResourceMessage;
import it.polimi.ingsw.messages.NickNameAction;
import it.polimi.ingsw.messages.NumOfPlayersAction;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
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
        } else if(input.startsWith("InitialResource:") && input.contains(" IN SHELF:")){
            input= input.replace("InitialResource:","").toUpperCase();
            String resource= "";
            if(input.contains("SHIELD")){
                input=input.replace("SHIELD","");
                resource="SHIELD";
            } else if(input.contains("COIN")){
                input=input.replace("COIN","");
                resource="COIN";
            } else if(input.contains("SERVANT")){
                input=input.replace("SERVANT","");
                resource="SERVANT";
            } else if(input.contains("STONE")){
                input=input.replace("STONE","");
                resource="STONE";
            }
            input = input.replace(" IN SHELF:", "");
            int shelfNum = Integer.parseInt(input);
            try {
                socketOut.writeObject(new InitialResourceMessage(resource, shelfNum));
                socketOut.flush();
            } catch (IOException e) {
                System.out.println("Connection closed");
                client.disconnect();
            }
        } else if(input.startsWith("ChosenLeads:")){
            ArrayList<Integer> chosenId = new ArrayList<>();
            if(input.contains("0")){
                chosenId.add(0);
            }if(input.contains("1")){
                chosenId.add(1);
            }if(input.contains("2")){
                chosenId.add(2);
            }if(input.contains("3")){
                chosenId.add(3);
            }
            if(chosenId.size()==2) {
                try {
                    socketOut.writeObject(new ChosenLeadMessage(chosenId));
                    socketOut.flush();
                } catch (IOException e) {
                    System.out.println("Connection closed");
                    client.disconnect();
                }
            }else
                System.out.println("Number of card chosen not correct. Please type again "
                        + "\"ChosenLeads:[first LeadId],[second LeadId]\"");
        } else if(input.equals("Show Actions")){
            System.out.println("Type one of this command:");
            System.out.println("1) Buy a development card: "
                    + "\"BuyDevCard:[CardId - 0 to 48]\"");
            System.out.println("2) Take resources from market: "
                    + "\"BuyResources:[MarketTray's index - 0 to 6]\"");
            System.out.println("3) Do development production: "
                    + "\"\"");

        }


        else
            System.out.println("Input not valid, type again");
    }
}
