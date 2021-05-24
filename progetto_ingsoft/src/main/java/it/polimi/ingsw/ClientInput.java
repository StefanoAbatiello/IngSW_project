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
    private Boolean mainAction;

    public ClientInput(MainClient client, ObjectOutputStream socketOut) {
        this.client = client;
        this.socketOut = socketOut;
        this.mainAction=false;
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
            System.out.println(input.replace("Nickname:", ""));
            client.send(new NickNameAction(input.replace("Nickname:", "")));
        }

        //3-choose number of player in lobby
        else if(input.startsWith("PlayersNumber:")){
            String string = input.replace("PlayersNumber:", "");
            try{
                int num = Integer.parseInt(string);
                if(num>=0 && num<=3) {
                    System.out.println("stai creando una lobby con altri " + num + " giocatori");
                    client.send(new NumOfPlayersAction(num));
                }else
                    System.out.println("Number of player selected not valid. Please type again " +
                            "\"PlayersNumber:[num of player]\"");
            } catch (NumberFormatException e) {
                System.out.println("Command not valid. Please type again " +
                        "\"PlayersNumber:[num of player]\"");
            }
        }

        //4-choose initial resource
        else if(input.startsWith("InitialResource:") && input.contains(" in shelf:")){
            input= input.replace("InitialResource:","").toUpperCase();
            String resource= "";
            String[] words=input.split(" IN SHELF:");
            resource=words[0];
            try {
                int shelfNum = Integer.parseInt(words[1]);
                if(shelfNum>=0 && shelfNum<=2) {
                    client.send(new InitialResourceMessage(resource, shelfNum));
                }else
                    System.out.println("Index of shelf not valid. Please type again" +
                        "\"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
            } catch (NumberFormatException e){
                System.out.println("Command not valid. Please type again" +
                        "\"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
            }
        }

        //TODO sistemare metodo con gli id delle carte
        //5-choose which leader cards hold
        else if(input.startsWith("ChosenLeads:")){
            input=input.replace("ChosenLeads:","");
            ArrayList<Integer> chosenId = new ArrayList<>();
            String[] words= input.split(",");
            chosenId.add(Integer.parseInt(words[0]));
            chosenId.add(Integer.parseInt(words[1]));
            chosenId.forEach(System.out::println);
            client.send(new ChosenLeadMessage(chosenId));
            //System.out.println("Ho inviato il messaggio");[Debug]
        }

        //6-ask for command's format
        else if(input.equals("ShowActions")){
            System.out.println("Type one of this command:");
            if (!mainAction) {
                System.out.println("# Buy a development card: "
                        + "\"BuyDevCard:[CardId - 0 to 48],[Board Slot - 0 to 2]\"");
                System.out.println("# Take resources from market: "
                        + "\"BuyResources:[MarketTray's index - 0 to 6]\"");
                System.out.println("# Do development production: "
                        + "\"DoProductions:[First card's Id],[Second card's id]...\"");
                //TODO gestione delle produzioni "speciali"
            }else
                System.out.println("# End your turn: " +
                        "\"EndTurn\"");
            System.out.println("# Active leader card: " +
                    "\"ActiveLeadCard:[Card id]\"");
            System.out.println("# Discard leader card: " +
                    "\"DiscardLeadCard:[Card id]\"");
            System.out.println("# Reorganize warehouse depots:" +
                    "\"ReorganizeResources:\"");
            //TODO ideare comando di sistemazione delle risorse
            System.out.println("# Shows my personal board:" +
                    "\"ShowPersonalBoard\"");
            System.out.println("# Show market: " +
                    "\"ShowMarket\"");
            System.out.println("# Show development card matrix: " +
                    "\"ShowDevCardMatrix\"");
        }

        //TODO ottimizzare la visione
        //7-request to show personal board
        else if(input.equals("ShowPersonalBoard")){
            client.getViewCLI().showPersonalBoard();
        }

        //8-request to buy a development card
        else if(input.startsWith("BuyDevCard")){
            if(!mainAction) {
                mainAction=true;
                input = input.replace("BuyDevCard:", "");
                try {
                    int id = Integer.parseInt(input);
                    int[][] matrix = client.getViewCLI().getDevMatrix();
                    for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 3; j++) {
                            if (matrix[i][j] == id) {
                                client.send(new BuyCardAction(id));
                            }
                        }
                } catch (NumberFormatException e) {
                    System.out.println("Card id selected not valid. Please type again" +
                            "\"BuyDevCard:[CardId - 0 to 48],[Board Slot - 0 to 2]\"");
                }
            }
        }

        //9-request to take resorces from market
        else if(input.startsWith("BuyResources:")) {
            if(!mainAction) {
                mainAction=true;
                input = input.replace("BuyResources:", "");
                try {
                    int selector = Integer.parseInt(input);
                    if (selector >= 0 && selector <= 6) {
                        client.send(new MarketAction(selector));
                    } else
                        System.out.println("Index of matrix not valid. Type again" +
                                "\"BuyResources:[MarketTray's index - 0 to 6]\"");
                } catch (NumberFormatException e) {
                    System.out.println("Command not valid. Please type again" +
                            "\"BuyResources:[MarketTray's index - 0 to 6]\"");
                }
            }
        }

        //TODO ideare gestione della scelta delle produzioni
        //10-request to activate productions
        else if(input.startsWith("DoProductions")){
            if(!mainAction){
                mainAction=true;
            }
        }

        //11-request to activate a leader card
        else if(input.startsWith("ActiveLeadCard")){
            if (!mainAction) {
                mainAction=true;
                input = input.replace("ActiveLeadCard:", "");
                try {
                    int id = Integer.parseInt(input);
                    client.send(new ActiveLeadAction(id));
                } catch (NumberFormatException e) {
                    System.out.println("please insert a number. Type again" +
                            "\"ActiveLeadCard:[Card id]\"");
                }
            }
        }

        //12-request to discard a leader card
        else if(input.startsWith("DiscardLeadCard")){
            if (!mainAction) {
                mainAction=true;
                input = input.replace("DiscardLeadCard:", "");
                try {
                    int id = Integer.parseInt(input);
                    client.send(new DiscardLeadAction(id));
                } catch (NumberFormatException e) {
                    System.out.println("please insert a number. Type again" +
                            "\"DiscardLeadCard:[Card id]\"");
                }
            }
        }

        //13-show market
        else if(input.equals("ShowMarket")){
            System.out.println("this is the market:");
            client.getViewCLI().showMarket();
        }

        //14-show dev matrix
        else if(input.equals("ShowDevCardMatrix")){
            System.out.println("this are dev cards buyable:");
            client.getViewCLI().showDevMatrix();
        }

        else if(input.equals("EndTurn")){
            if(mainAction){
                client.send(new TurnChangeMessage());
                mainAction=false;
            }
        }
        else
            System.out.println("Input not valid, type again");
    }
}
