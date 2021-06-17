package it.polimi.ingsw.client;

import it.polimi.ingsw.App;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CLI extends App implements View {
    private ClientCardParser parser;
    private ViewCLI viewCLI;
    private static final Scanner scanner=new Scanner(System.in);


    public CLI(ClientCardParser clientCardParser,ViewCLI viewCLI) {
        parser=clientCardParser;
        this.viewCLI=viewCLI;
    }



    @Override
    public void nicknameHandler(NickNameAction input) {
        System.out.println(input.getMessage());
        System.out.println("Type \"Nickname:[your nickname]\"");
    }

    @Override
    public void numOfPlayerHandler(RequestNumOfPlayers input) {
        System.out.println( input.getMessage());
        System.out.println("Type \"PlayersNumber:[num of player]\"");
    }

    @Override
    public void lobbyMessageHandler(LobbyMessage input) {
        System.out.println(input.getMessage());

    }

    @Override
    public void initialResourceHandler(GetInitialResourcesAction input) {
        System.out.println(input.getMessage());
        System.out.println("Type \"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
    }

    @Override
    public void leadCardHandler(LeaderCardDistribution input) {

        System.out.println(input.getMessage() + "\n");
        ArrayList<Integer> leadCardsId = input.getLeadCardsId();
        //System.out.println("mi sono salvato gli id");[Debug]
        for (int id : leadCardsId) {
            parser.takeLeadCardFromId(id);
            //System.out.println("mi sono parsato la carta "+id);
            viewCLI.showLeadCard(id);
        }
        if(leadCardsId.size()>2) {
            System.out.println("\nType \"ChosenLeads:[first LeadId],[second LeadId]\"");
        }
    }

    @Override
    public void supplyHandler(ResourceInSupplyRequest input) {
        System.out.println("Now you have to place these new resources in warehouse:");
        (input).getResources().forEach(System.out::println);
        System.out.println("Please Type: " +
                "\"PutNewResources:[first Resource] in shelf [Shelf num - 0 to 2], " +
                "[second Resource] in shelf [Shelf num - 0 to 2]...\"");
        System.out.println("You can alse leave Resource in supply to discard them and give a faith point to all other players");
        System.out.println("You can also leave Resource in supply to discard them and give a faith point to all other players");
        System.out.println("If you have a ");

    }

    @Override
    public void marketHandler(MarketChangeMessage input) {
        System.out.println("Market is changed:");
        viewCLI.setMarket(input.getMarket());
    }

    @Override
    public void warehouseHandler(WareHouseChangeMessage input) {
        System.out.println("Your warehouse is changed:");
            /*
            if(((PersonalBoardChangeMessage)input).getFaithPosition().isPresent()){
                viewCLI.setFaithPosition(((PersonalBoardChangeMessage)input).getFaithPosition().get());
            }
            */
        viewCLI.setWarehouse((input).getWarehouse());
    }

    @Override
    public void personalCardHandler(CardIDChangeMessage input) {
        System.out.println("Your cards are changed:");
        ((CardIDChangeMessage) input).getCardID().keySet().stream().filter(integer -> integer > 48 && integer < 65).forEach(cardID -> {
            if (!viewCLI.getCardsFromId().containsKey(cardID)) {
                parser.takeLeadCardFromId(cardID);
                System.out.println("sto parsando la carta " + cardID);
            }
            if (viewCLI.getLeadCardsId().get(cardID) != ((CardIDChangeMessage) input).getCardID().get(cardID)) {
                viewCLI.getLeadCardsId().remove(cardID);
            }
            if (!viewCLI.getLeadCardsId().containsKey(cardID)) {
                viewCLI.addLeadCardsId(cardID, ((CardIDChangeMessage) input).getCardID().get(cardID));
                System.out.println("mi sto salvando la carta " + cardID);
            }
        });
        ((CardIDChangeMessage) input).getCardID().keySet().stream().filter(integer -> integer >= 0 && integer <= 48).forEach(cardID -> {
            if (!viewCLI.getCardsFromId().containsKey(cardID)) {
                parser.takeDevCardFromId(cardID);
                System.out.println("sto parsando la carta " + cardID);
            }
            if(viewCLI.getDevCardsId().get(cardID)!=((CardIDChangeMessage) input).getCardID().get(cardID)){
                viewCLI.getDevCardsId().remove(cardID);
            }
            if (!viewCLI.getDevCardsId().containsKey(cardID)) {
                viewCLI.addDevCardId(cardID, ((CardIDChangeMessage) input).getCardID().get(cardID));
                System.out.println("mi sto salvando la carta "+cardID);
            }
        });
    }

    @Override
    public void devMatrixHandler(DevMatrixChangeMessage input) {
        System.out.println("Development cards matrix is changed:");
        int[][] devMatrix= input.getDevMatrix();
        matrixParser(devMatrix, parser);
        viewCLI.setDevMatrix(devMatrix);
    }

    private void matrixParser(int[][] devMatrix, ClientCardParser parser) {
        Arrays.stream(devMatrix[0]).forEach(parser::takeDevCardFromId);
        Arrays.stream(devMatrix[1]).forEach(parser::takeDevCardFromId);
        Arrays.stream(devMatrix[2]).forEach(parser::takeDevCardFromId);
        Arrays.stream(devMatrix[3]).forEach(parser::takeDevCardFromId);
    }

    @Override
    public void strongboxHandler(StrongboxChangeMessage input) {
        System.out.println("Your strongbox is changed:");
        viewCLI.setStrongbox(input.getStrongbox());
    }

    @Override
    public void choosableResourceHandler(ChangeChoosableResourceRequest input) {
        System.out.println("You have "+ input.getNum()+" CHOOSABLE resources");
        System.out.println(input.getMessage());
        System.out.println("Type \"ChangeWhiteMarble:[first resource],[second resource]\"");
    }

    public static void main() {
        System.out.println("insert port,ip");
        String input=scanner.nextLine();
        input=input.replace(" ","");
        String[] words=input.split(",");

        try {
            int port = Integer.parseInt(words[1]);
            String ip = words[0];

            if((port>=1024&&port<=65535)&&ip!=""){
                MainClient mainClient = new MainClient(ip, port);
                new Thread(mainClient).start();
            }
            else
            {
                System.out.println("port or id is not valid");
            }
        }catch (NumberFormatException e) {
            System.out.println("NumberFormatException");
        }
    }
}
