package it.polimi.ingsw.client;

import it.polimi.ingsw.App;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI extends App implements View {
    private final ClientCardParser parser;
    private SimplifiedModel simplifiedModel;
    private static final Scanner scanner=new Scanner(System.in);
    private ClientInput keyboardReader;


    public CLI(ClientCardParser clientCardParser, ClientInput keyboardReader) {
        parser=clientCardParser;
        this.keyboardReader=keyboardReader;
    }

    public static void main() {
        System.out.println("Please insert IP and PORT");
        System.out.println("Type: \"[server IP],[PORT chosen]\"");
        String input=scanner.nextLine();
        input=input.replace(" ","");
        String[] words=input.split(",");
        try {
            int port = Integer.parseInt(words[1]);
            String ip = words[0];

            if((port>=1024&&port<=65535)&& !ip.equals("")){
                MainClient mainClient = new MainClient(ip, port);
                new Thread(mainClient).start();
            } else {
                System.out.println("PORT or ID is not valid");
            }
        }catch (NumberFormatException e) {
            System.out.println("PORT not valid");
        }
    }


    /**
     * this is the client handel of the nickname message
     * @param message is the message
     */
    @Override
    public void nicknameHandler(NickNameAction message) {
        System.out.println(message.getMessage());
        System.out.println("Type \"Name:[your name]\"");
    }

    /**
     * this is the client handler of the request of players number message
     * @param message is the message
     */
    @Override
    public void numOfPlayerHandler(RequestNumOfPlayers message) {
        System.out.println( message.getMessage());
        System.out.println("Type \"Number:[num of player]\"");
    }

    /**
     * this is the client handler of the lobby message
     * @param message is the message
     */
    @Override
    public void lobbyMessageHandler(LobbyMessage message) {
        System.out.println(message.getMessage());
    }

    /**
     * this is the client handler of the starting game message
     * @param simplifiedModel is the client simplified model
     * @param message is the message
     */
    @Override
    public void gameSetupHandler(SimplifiedModel simplifiedModel, StartingGameMessage message){
        System.out.println("The game can start!");
        System.out.println("Type \"ShowActions\" to see commands");
    }

    /**
     * this is the client handler of the initial resource message
     * @param message is the message
     */
    @Override
    public void initialResourceHandler(GetInitialResourcesAction message) {
        System.out.println(message.getMessage());
        int resNum=message.getNumRes();
        if(resNum==2)
            System.out.println("Type \"InitialRes:[COIN/SERVANT/SHIELD/STONE] in shelf[shelf number];[COIN/SERVANT/SHIELD/STONE] in shelf[shelf number]\"");
        else
            System.out.println("Type \"InitialRes:[COIN/SERVANT/SHIELD/STONE] in shelf[shelf number]\"");
    }

    /**
     * this the client handler of the waiting room message
     * @param message is the message
     */
    @Override
    public void waitingRoomHandler(WaitingRoomAction message) {
        System.out.println(message.getMessage());
    }

    /**
     * this is the client handler of the leader card distribution message
     * @param message is the message
     */
    @Override
    public void leadCardHandler(LeaderCardDistribution message) {
        System.out.println(message.getMessage() + "\n");
        ArrayList<Integer> leadCardsId = message.getLeadCardsId();
        //System.out.println("mi sono salvato gli id");[Debug]
        for (int id : leadCardsId) {
            parser.takeLeadCardFromId(id);
            //System.out.println("mi sono parsato la carta "+id);
            simplifiedModel.showLeadCard(id);
        }
        if(leadCardsId.size()>2) {
            System.out.println("\nType \"Leads:[first LeadId],[second LeadId]\"");
        }
    }

    /**
     * this is the client handler of the initial resource message
     * @param message is the message
     */
    @Override
    public void supplyHandler(ResourceInSupplyRequest message) {
        System.out.println("Now you have to place these new resources in warehouse:");
        System.out.println(message.getResources());
        System.out.println("Please Type: " +
                "\"PutNewRes:[first Resource] in shelf [Shelf num - 0 to 2], " +
                "[second Resource] in shelf [Shelf num - 0 to 2]...\"");
        System.out.println("You can also leave Resource in supply to discard them and give a faith point to all other players");
    }

    /**
     * this is the client handler of the the market change message
     * @param message is the message
     */
    @Override
    public void marketHandler(MarketChangeMessage message) {
        System.out.println("The market is changed");
        simplifiedModel.setMarket(message.getMarket());
    }

    /**
     * this is the client handler of the warehouse change message
     * @param message is the message
     */
    @Override
    public void warehouseHandler(WareHouseChangeMessage message) {
        System.out.println("Your warehouse is changed");
        simplifiedModel.setWarehouse(message.getWarehouse());
    }

    /**
     * this is the client handler of the personal cards id change message
     * @param message is the message
     */
    @Override
    public void personalCardHandler(CardIDChangeMessage message) {
        System.out.println("Your cards are changed");
        message.getCardID().keySet().stream().filter(integer -> integer > 48 && integer < 65).forEach(cardID -> {
            if (!simplifiedModel.getCardsFromId().containsKey(cardID)) {
                parser.takeLeadCardFromId(cardID);
            }
            if (simplifiedModel.getLeadCardsId().get(cardID) != message.getCardID().get(cardID)) {
                simplifiedModel.getLeadCardsId().remove(cardID);
            }
            if (!simplifiedModel.getLeadCardsId().containsKey(cardID)) {
                simplifiedModel.addLeadCardsId(cardID, message.getCardID().get(cardID));
            }
            for(int id: simplifiedModel.getLeadCardsId().keySet())
                if(!message.getCardID().containsKey(id))
                    simplifiedModel.getLeadCardsId().remove(id);
        });
        message.getCardID().keySet().stream().filter(integer -> integer >= 0 && integer <= 48).forEach(cardID -> {
            if (!simplifiedModel.getCardsFromId().containsKey(cardID)) {
                parser.takeDevCardFromId(cardID);
            }
            if(simplifiedModel.getDevCardsId().get(cardID)!= message.getCardID().get(cardID)){
                simplifiedModel.getDevCardsId().remove(cardID);
            }
            if (!simplifiedModel.getDevCardsId().containsKey(cardID)) {
                simplifiedModel.addDevCardId(cardID, message.getCardID().get(cardID));
            }
        });
        simplifiedModel.setDevPositions(message.getCardPosition());
    }

    /**
     * this is the client handler of the development matrix change message
     * @param message is the message
     */
    @Override
    public void devMatrixHandler(DevMatrixChangeMessage message) {
        System.out.println("Development cards matrix is changed");
        int[][] devMatrix= message.getDevMatrix();
        simplifiedModel.matrixParser(devMatrix, parser);
        simplifiedModel.setDevMatrix(devMatrix);
    }

    /**
     * this is the client handler of the strongbox change message
     * @param message is the message
     */
    @Override
    public void strongboxHandler(StrongboxChangeMessage message) {
        System.out.println("Your strongbox is changed");
        simplifiedModel.setStrongbox(message.getStrongbox());
    }

    /**
     * this is the client handler of the request of change the choosable resources
     * @param message is the message
     */
    @Override
    public void choosableResourceHandler(ChangeChoosableResourceRequest message) {
        System.out.println("You have "+ message.getNum()+" CHOOSABLE resources");
        System.out.println(message.getMessage());
        System.out.println("Type \"ChangeRes:[first resource],[second resource]...\"");
    }


    @Override
    public void setViewCLI(SimplifiedModel simplifiedModel) {
        this.simplifiedModel = simplifiedModel;
    }

    /**
     * this is the client handler of the change of the faith position
     * @param message is the message
     */
    @Override
    public void faithPositionHandler(FaithPositionChangeMessage message) {
        simplifiedModel.setFaithPosition(message.getFaithPosition());
        System.out.println("Your Faith marker has changed his position");
    }

    /**
     * this is the client handler of the activation of a pope meeting
     * @param message is the message
     */
    @Override
    public void activePopeMeetingHandler(ActivePopeMeetingMessage message) {
        System.out.println("You joined the Pope meeting number: "+message.getMeetingNumber());
    }

    @Override
    public void shelfAbilityActiveHandler(ShelfAbilityActiveMessage message) {
    }

    /**
     * this is the client handler of the change of the black cross position
     * @param message is the message
     */
    @Override
    public void lorenzoActionHandler(LorenzoActionMessage message) {
        if (message.getPosition() >= 0) {
            System.out.println("Lorenzo's cross is in position:"+message.getPosition());
        }
    }
}
