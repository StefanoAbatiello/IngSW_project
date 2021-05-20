package it.polimi.ingsw;

import it.polimi.ingsw.gameActions.ActiveLeadAction;
import it.polimi.ingsw.gameActions.DiscardLeadAction;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
        //1-close client
        if (input.equalsIgnoreCase("quit")) {
            client.disconnect();
        }

        //2-choose nickname
        else if (input.startsWith("Nickname:")) {
            try {
                System.out.println(input.replace("Nickname:", ""));
                socketOut.writeObject(new NickNameAction(input.replace("Nickname:", "")));
                socketOut.flush();
            } catch (java.io.IOException e) {
                System.out.println("Connection closed");
                client.disconnect();
            }
        }

        //3-choose number of player in lobby
        else if(input.startsWith("PlayersNumber:")){
            String string = input.replace("PlayersNumber:", "");
            try{
                int num = Integer.parseInt(string);
                if(num>=0 && num<=3) {
                    System.out.println("stai creando una lobby con altri " + num + " giocatori");
                    socketOut.writeObject(new NumOfPlayersAction(num));
                    socketOut.flush();
                }else
                    System.out.println("Number of player selected not valid. Please type again " +
                            "\"PlayersNumber:[num of player]\"");
            } catch (NumberFormatException e) {
                System.out.println("Command not valid. Please type again " +
                        "\"PlayersNumber:[num of player]\"");
            } catch (java.io.IOException e) {
                System.out.println("Connection closed");
                client.disconnect();
            }
        }

        //4-choose initial resource
        else if(input.startsWith("InitialResource:") && input.contains(" IN SHELF:")){
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
            try {
                int shelfNum = Integer.parseInt(input);
                if(shelfNum>=0 && shelfNum<=2) {
                    socketOut.writeObject(new InitialResourceMessage(resource, shelfNum));
                    socketOut.flush();
                }else
                    System.out.println("Index of shelf not valid. Please type again" +
                        "\"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
            } catch (NumberFormatException e){
                System.out.println("Command not valid. Please type again" +
                        "\"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
            }
            catch (IOException e) {
                System.out.println("Connection closed");
                client.disconnect();
            }
        }

        //TODO sistemare metodo con gli id delle carte
        //5-choose which leader cards hold
        else if(input.startsWith("ChosenLeads:")){
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
        }

        //6-ask for command's format
        else if(input.equals("ShowActions")){
            System.out.println("Type one of this command:");
            System.out.println("1) Buy a development card: "
                    + "\"BuyDevCard:[CardId - 0 to 48],[Board Slot - 0 to 2]\"");
            System.out.println("2) Take resources from market: "
                    + "\"BuyResources:[MarketTray's index - 0 to 6]\"");
            System.out.println("3) Do development production: "
                    + "\"DoProductions:[First card's Id],[Second card's id]...\"");
            //TODO gestione delle produzioni "speciali"
            System.out.println("4) Active leader card: " +
                    "\"ActiveLeadCard:[Card id]\"");
            System.out.println("5) Discard leader card: " +
                    "\"DiscardLeadCard:[Card id]\"");
            System.out.println("6) Reorganize warehouse depots:" +
                    "\"ReorganizeResources:\"");
            //TODO ideare comando di sistemazione delle risorse
            System.out.println("7) Shows my personal board:" +
                    "\"ShowPersonalBoard\"");
            System.out.println("8) Show market: " +
                    "\"ShowMarket\"");
            System.out.println("9) Show development card matrix: " +
                    "\"ShowDevCardMatrix\"");
        }

        //TODO ottimizzare la visione
        //7-request to show personal board
        else if(input.equals("ShowPersonalBoard")){
            client.getViewCLI().showView();
        }

        //8-request to buy a development card
        else if(input.startsWith("BuyDevCard")){
            input=input.replace("BuyDevCard:","");
            try {
                int id = Integer.parseInt(input);
                int[][] matrix = client.getViewCLI().getDevMatrix();
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 3; j++) {
                        if (matrix[i][j] == id) {
                            socketOut.writeObject(new BuyCardAction(id));
                            socketOut.flush();
                        }
                    }
            }catch (NumberFormatException e){
                System.out.println("Card id selected not valid. Please type again" +
                        "\"BuyDevCard:[CardId - 0 to 48],[Board Slot - 0 to 2]\"");
            }catch (IOException e) {
                client.disconnect();
            }
        }

        //9-request to take resorces from market
        else if(input.startsWith("BuyResources:")) {
            input = input.replace("BuyResources:", "");
            try {
                int selector = Integer.parseInt(input);
                if (selector >= 0 && selector <= 6) {
                    socketOut.writeObject(new MarketAction(selector));
                    socketOut.flush();
                } else
                    System.out.println("Index of matrix not valid. Type again" +
                            "\"BuyResources:[MarketTray's index - 0 to 6]\"");
            } catch (NumberFormatException e) {
                System.out.println("Command not valid. Please type again" +
                        "\"BuyResources:[MarketTray's index - 0 to 6]\"");
            }catch (IOException e) {
                    client.disconnect();
            }
        }

        //TODO ideare gestione della scelta delle produzioni
        //10-request to activate productions
        else if(input.startsWith("DoProductions")){

        }

        //11-request to activate a leader card
        else if(input.startsWith("ActiveLeadCard")){
            input=input.replace("ActiveLeadCard:","");
            try {
                int id = Integer.parseInt(input);
                socketOut.writeObject(new ActiveLeadAction(id));
                socketOut.flush();
            } catch (NumberFormatException e) {
                System.out.println("please insert a number. Type again" +
                "\"ActiveLeadCard:[Card id]\"");
            }catch (IOException e) {
                client.disconnect();
            }
        }

        //12-request to discard a leader card
        else if(input.startsWith("DiscardLeadCard")){
            input=input.replace("DiscardLeadCard:","");
            try {
                int id = Integer.parseInt(input);
                socketOut.writeObject(new DiscardLeadAction(id));
                socketOut.flush();
            } catch (NumberFormatException e) {
                System.out.println("please insert a number. Type again" +
                        "\"DiscardLeadCard:[Card id]\"");
            }catch (IOException e) {
                client.disconnect();
            }
        }

        else
            System.out.println("Input not valid, type again");
    }
}
