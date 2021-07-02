package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.ActiveLeadAction;
import it.polimi.ingsw.messages.DiscardLeadAction;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.NumOfPlayersAnswer;

import java.util.*;

public class ClientInput implements Runnable {

    private final MainClient client;
    private Boolean active;

    public ClientInput(MainClient client) {
        this.client = client;
        this.active = true;
    }

    /**
     * try to read from keyboard and analise it
     */
    @Override
    public void run() {
        //System.out.println("sto leggendo da tastiera");
        Scanner scanner = new Scanner(System.in);
        while (active) {
            String input = scanner.nextLine().toUpperCase();
            inputHandler(input.replace(" ", ""));
        }
    }

    /**
     * this method handles the command written by the player and in case communicates with the server
     * @param input is the string written by the user
     */
    private void inputHandler(String input) {

        if (input.equalsIgnoreCase("QUIT")) {
            client.disconnect();
        } else if (input.startsWith("NAME:")) {
            sendNickname(input.replace("NAME:", ""));
        } else if (input.startsWith("NUMBER:")) {
            sendPlayersNumber(input.replace("NUMBER:", ""));
        } else if (input.startsWith("INITIALRES:") && input.contains("INSHELF")) {
            sendInitialResource(input.replace("INITIALRES:", ""));
        } else if (input.startsWith("LEADS:") && input.contains(",")) {
            sendChosenLeads(input.replace("LEADS:", ""));
        } else if (input.equals("SHOWACTIONS")) {
            System.out.println("Type one of this command:");
            System.out.println("# Buy a development card: "
                    + "\"BuyCard:[CardId - 0 to 48],[Board Slot - 0 to 2]\"");
            System.out.println("# Take resources from market: "
                    + "\"BuyRes:[MarketTray's index - 0 to 6]\"");
            System.out.println("# Do development production: "
                    + "\"DoProd:Cards:[card1ID,card2ID,...];personalIn:[Resource1,Resource2];" +
                    "personalOut:[Resource];LeadOut:[Resource...]\"");
            System.out.println("# Active leader card: " +
                    "\"ActiveLead:[Card id]\"");
            System.out.println("# Discard leader card: " +
                    "\"DiscardLead:[Card id]\"");
            System.out.println("# Shows my personal board:" +
                    "\"ShowBoard\"");
            System.out.println("# Show market: " +
                    "\"ShowMarket\"");
            System.out.println("# Show development card matrix: " +
                    "\"ShowCardMatrix\"");
            System.out.println("# End your turn: " +
                    "\"EndTurn\"");
        } else if (input.equals("SHOWBOARD")) {
            client.getViewCLI().showPersonalBoard();
        } else if (input.startsWith("BUYCARD") && input.contains(",")) {
            sendDevCardPurchased(input.replace("BUYCARD:", ""));
        } else if (input.startsWith("BUYRES:")) {
            sendMarketIndex(input.replace("BUYRES:", ""));
        } else if (input.startsWith("DOPROD") && input.contains("CARDS") && input.contains("PERSONALIN") && input.contains("PERSONALOUT") && input.contains("LEADOUT" )&& input.contains(";")) {
            sendProductions(input.replace("DOPROD:", ""));
        } else if (input.startsWith("ACTIVELEAD:")) {
            sendActiveLeader(input.replace("ACTIVELEAD:", ""));
        } else if (input.startsWith("DISCARDLEAD")) {
            sendDiscardLeader(input.replace("DISCARDLEAD:", ""));
        } else if (input.startsWith("PUTNEWRES")) {
            sendNewWarehouseOrganization(input.replace("PUTNEWRES:", ""));
        } else if (input.startsWith("CHANGERES")) {
            sendChangeChoosable(input.replace("CHANGERES:", ""));
        } else if (input.equals("SHOWMARKET")) {
            client.getViewCLI().showMarket();
        } else if (input.equals("SHOWCARDMATRIX")) {
            client.getViewCLI().showDevMatrix();
        } else if (input.equals("ENDTURN")) {
            client.send(new TurnChangeMessage());
        } else
            System.out.println("Input not valid, type again");
    }

    /**
     * this method analise the string and the send the ChangeChoosableMessage
     * @param command is the string written by the user
     */
    private void sendChangeChoosable(String command) {
        String[] resources;
        if (command.contains(","))
            resources = command.split(",");
        else{
            resources=new String[1];
            resources[0]=command;
        }
        ArrayList<String> newRes = new ArrayList<>();
        Collections.addAll(newRes, resources);
        client.send(new ChangeChoosableAction(newRes));
    }

    /**
     * this method analise the string and the send the ResourceInSupplyAction
     * @param string is the string written by the user
     */
    private void sendNewWarehouseOrganization(String string) {
        String[] commands;
        if (string.contains(","))
            commands = string.split(",");
        else {
            commands = new String[1];
            commands[0] = string;
        }
        ArrayList<String>[] newWarehouse = new ArrayList[5];
        for (int i = 0; i < 5; i++) {
            newWarehouse[i] = new ArrayList<>();
        }
        for (int i = 0; i < 5; i++) {
            newWarehouse[i].addAll(client.getViewCLI().getWarehouse()[i]);
        }
        for (String command : commands) {
            if (command.contains("INSHELF")) {
                String[] word = command.split("INSHELF");
                try {
                    int shelfNum = Integer.parseInt(word[1]);
                    if ((word[0].equalsIgnoreCase("COIN") ||
                            word[0].equalsIgnoreCase("SERVANT") ||
                            word[0].equalsIgnoreCase("SHIELD") ||
                            word[0].equalsIgnoreCase("STONE")) && shelfNum < 5 && shelfNum >= 0) {
                        newWarehouse[shelfNum].add(word[0]);
                    } else {
                        System.out.println("Input not valid, please type again");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input not valid, please type again");
                }
            } else {
                System.out.println("Input not valid, please type again");
                return;
            }
        }
        System.out.println("You want this disposition:");
        for (int i=0;i<5;i++){
            System.out.println("on shelf "+i);
            System.out.println(newWarehouse[i]);
        }
        client.send(new ResourceInSupplyAction(newWarehouse));
    }

    /**
     * this method analise the string written by the user and then send the DiscardLeaderAction
     * @param cardId is the string written by the user
     */
    private void sendDiscardLeader(String cardId) {
        try {
            int id = Integer.parseInt(cardId);
            client.send(new DiscardLeadAction(id));
        } catch (NumberFormatException e) {
            System.out.println("please insert a number. Type again");
        }
    }

    /**
     * this method analise the string written by the user and then send the ActiveLeaderAction
     * @param cardID is the string written by the user
     */
    private void sendActiveLeader(String cardID) {
        try {
            int id = Integer.parseInt(cardID);
            client.send(new ActiveLeadAction(id));
        } catch (NumberFormatException e) {
            System.out.println("please insert a number. Type again");
        }
    }

    /**
     * this method analise the string written by the user and then send the ProductionAction
     * @param string is the string written by the user
     */
    private void sendProductions(String string) {
        String[] commands = string.split(";");

        ArrayList<Integer> intId = new ArrayList<>();
        ArrayList<String> personalIn = new ArrayList<>();
        String personalOut = null;
        ArrayList<String> leadOut = new ArrayList<>();


        for (String s : commands) {
            if (s.startsWith("CARDS")&&!s.contains("PERSONALOUT")&&!s.contains("PERSONALIN")&&!s.contains("LEADOUT")) {
                s = s.replace("CARDS:", "");
                String[] words;
                if (s.contains(","))
                    words = s.split(",");
                else {
                    words = new String[1];
                    words[0] = s;
                }
                for (String word : words) {
                    if (word != null) {
                        try {
                            intId.add(Integer.parseInt(word));
                        } catch (NumberFormatException e) {
                            System.out.println("Input not valid, please type again");
                        }
                    }
                }
            } else if (s.startsWith("PERSONALIN")&&!s.contains("PERSONALOUT")&&!s.contains("LEADOUT")&&!s.contains("CARDS")) {
                s = s.replace("PERSONALIN:", "");
                String[] words;
                if (s.contains(","))
                    words = s.split(",");
                else {
                    words = new String[1];
                    words[0] = s;
                }
                for (String word : words) {
                    if (word != null) {
                        personalIn.add(word);
                    }
                }
            } else if (s.startsWith("PERSONALOUT")&&!s.contains("LEADOUT")&&!s.contains("PERSONALIN")&&!s.contains("CARDS")) {
                s = s.replace("PERSONALOUT:", "");
                personalOut = s;
            } else if (s.startsWith("LEADOUT")&&!s.contains("PERSONALOUT")&&!s.contains("PERSONALIN")&&!s.contains("CARDS")) {
                s = s.replace("LEADOUT:", "");
                String[] words = s.split(",");
                for (String word : words) {
                    if (word != null) {
                        leadOut.add(word);
                    }
                }
            } else
                System.out.println("Input not valid, please type again");
        }
        client.send(new ProductionAction(intId, personalIn, personalOut, leadOut));
    }

    /**
     * this method analise the string written by the user and then send the MarketAction
     * @param index is the string written by the user
     */
    private void sendMarketIndex(String index) {
        try {
            int selector = Integer.parseInt(index);
            client.send(new MarketAction(selector));
        } catch (NumberFormatException e) {
            System.out.println("Command not valid. Please type again");
        }
    }

    /**
     * this method analise the string written by the user and then send the BuyCardAction
     * @param string is the string written by the user
     */
    private void sendDevCardPurchased(String string) {
        try {
            String[] word=string.split(",");
            int id = Integer.parseInt(word[0]);
            int slot=Integer.parseInt(word[1]);
            if(id>=0 && id<=48 && slot>=0 && slot<=2) {
                client.send(new BuyCardAction(id, slot));
            }else {
                System.out.println("Input not valid, please type again");
            }
        } catch (NumberFormatException e) {
            System.out.println("Card id selected not valid. Please type again");
        }
    }

    /**
     * this method analise the string written by the user and then send the ChosenLeadMessage
     * @param string is the string written by the user
     */
    private void sendChosenLeads(String string) {
        ArrayList<Integer> chosenId = new ArrayList<>();
        String[] words= string.split(",");
        if (words.length==2) {
            chosenId.add(Integer.parseInt(words[0]));
            chosenId.add(Integer.parseInt(words[1]));
            client.send(new ChosenLeadMessage(chosenId));
        } else
            System.out.println("Number of leader chosen not valid, please type again");
    }

    /**
     * this method analise the string written by the user and then send the InitialResourceMessage
     * @param string is the string written by the user
     */
    private void sendInitialResource(String string) {
        Map<Integer,Integer> shelves=new HashMap<>();
        Map<Integer,String> resources=new HashMap<>();
        String[] commands;
        if (string.contains(";"))
        commands=string.split(";");
        else{
            commands=new String[1];
            commands[0]=string;
        }
        for(int i=0;i<commands.length;i++) {
            String[] words = commands[i].split("INSHELF");
            String resource = words[0];
            try {
                int shelfNum = Integer.parseInt(words[1]);
                if (shelfNum >= 0 && shelfNum <= 2) {
                    shelves.put(i,shelfNum);
                    resources.put(i,resource);
                } else {
                    System.out.println("Index of shelf not valid. Please type again");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Command not valid. Please type again");
            }
        }
        client.send(new InitialResourceMessage(resources, shelves));

    }

    /**
     * this method analise the string written by the user and then send the NumOfPlayersAnswer
     * @param string is the string written by the user
     */
    private void sendPlayersNumber(String string) {
        try{
            int num = Integer.parseInt(string);
            if(num>=0 && num<=3) {
                client.send(new NumOfPlayersAnswer(num));
            }else
                System.out.println("Number of player selected not valid. Please type again");
        } catch (NumberFormatException e) {
            System.out.println("Command not valid. Please type again");
        }
    }

    /**
     * this method analise the string written by the user and then send the NickNameAction
     * @param name is the string written by the user
     */
    private void sendNickname(String name) {
        client.send(new NickNameAction(name.replace("NICKNAME:", "")));
    }

}
