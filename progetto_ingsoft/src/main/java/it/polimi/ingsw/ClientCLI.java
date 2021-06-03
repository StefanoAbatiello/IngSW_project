package it.polimi.ingsw;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientCLI implements Runnable{

    private String ip;
    private int port;
    private PingObserver pingObserver;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private Socket socket;
    private static ClientInput keyboardReader;
    private ViewCLI viewCLI;
    private ClientCardParser parser;
    //private ExecutorService executors;
    private Timer timer;
    private static final int timerPeriod = 30000; // time in milliseconds


    public ClientCLI(String ip, int port) {
        this.ip = ip;
        this.port = port;
        parser = new ClientCardParser(this);
        //executors= Executors.newCachedThreadPool();
        timer=new Timer();
    }

    public void run() {
        try {
            socket = new Socket(ip, port);
            System.out.println("Connection established");
            socketIn = new ObjectInputStream(socket.getInputStream());
            socketOut = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException | NullPointerException e) {
            disconnect();
        }
        keyboardReader = new ClientInput(this);
        //executors.submit(keyboardReader);
        new Thread(keyboardReader).start();
        pingObserver = new PingObserver(this);
        viewCLI = new ViewCLI();
        SerializedMessage input;
        try {
            while (true) {
                input = (SerializedMessage) socketIn.readObject();
                //System.out.println("ho letto un messaggio");
                actionHandler(input);
            }
        } catch (ClassNotFoundException | IOException | NullPointerException e) {
            System.err.println("Connection closed");
            disconnect();
        }
    }

    public synchronized void send(SerializedMessage message){
        try {
            socketOut.writeObject(message);
            socketOut.flush();
        } catch (IOException e) {
            System.err.println("server not reachable");
            disconnect();
        }
    }

    public void asyncSend(PingMessage pingMessage) {
        //executors.submit
        new Thread(new Runnable() {
            @Override
            public void run() {
                send(pingMessage);
            }
        });
    }

    private synchronized void actionHandler(SerializedMessage input) {
        //1-gestisco la richiesta del nickname
        if (input instanceof NickNameAction) {
            System.out.println(((NickNameAction) input).getMessage());
            System.out.println("Type \"Nickname:[your nickname]\"");
        }

        //2-gestisco la richiesta del numero di giocatori che dovrà avere la lobby che sto creando
        else if (input instanceof RequestNumOfPlayers) {
            System.out.println(((RequestNumOfPlayers) input).getMessage());
            System.out.println("Type \"PlayersNumber:[num of player]\"");
        }

        //3- gestisco la ricezione di messaggi di servizio all'interno della lobby
        else if (input instanceof LobbyMessage) {
            System.out.println(((LobbyMessage) input).getMessage());
        }

        //4-gestione dei messaggio di ping
        else if (input instanceof PingMessage) {
            //System.out.println("ho ricevuto un ping");//[Debug]
            pingObserver.setResponse(true);
            send(new PingMessage());
            //System.out.println("ho inviato la risposta");
            if (!pingObserver.isStarted()) {
                //System.out.println("era il primo ping, faccio partire il pongObserver");//[Debug]
                timer.schedule(pingObserver, 0, timerPeriod);
                //System.out.println("pongObserver partito");//[Debug]
            }
        }

        //5-gestione della richiesta di scegliere la/le risorsa/e iniziale/
        else if (input instanceof GetInitialResourcesAction) {
            System.out.println(((GetInitialResourcesAction) input).getMessage());
            System.out.println("Type \"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
        }

        //6-gestione della richiesta di scegliere quali leader card mantenere
        else if (input instanceof LeaderCardDistribution) {
            System.out.println(((LeaderCardDistribution)input).getMessage() + "\n");
            ArrayList<Integer> leadCardsId = ((LeaderCardDistribution) input).getLeadCardsId();
            for (int id : leadCardsId) {
                parser.takeLeadCardFromId(id);
                viewCLI.showLeadCard(id);
            }
            if(leadCardsId.size()>2) {
                System.out.println("\nType \"ChosenLeads:[first LeadId],[second LeadId]\"");
            }
        }

        //7-gestione del salvataggio e della stampa della situazione iniziale della partita
        else if(input instanceof StartingGameMessage){
            ArrayList<String>[] warehouse=((StartingGameMessage)input).getWarehouse();
            viewCLI.setWarehouse(warehouse);
            viewCLI.setStrongbox(((StartingGameMessage)input).getStrongbox());
            int[][] devMatrix=((StartingGameMessage)input).getDevMatrix();
            viewCLI.setDevMatrix(devMatrix);
            for(int i=0;i<4;i++) {
                for (int j = 0; j < 3; j++) {
                    parser.takeDevCardFromId(devMatrix[i][j]);
                }
            }
            viewCLI.setLeadCardsId(((StartingGameMessage)input).getPersonalCardId());
            viewCLI.setFaithPosition(((StartingGameMessage)input).getFaithPosition());
            viewCLI.setMarket(((StartingGameMessage)input).getMarket());
            System.out.println("this is your personal board:");
            viewCLI.showPersonalBoard();
            System.out.println("\n \nthis is the market: ");
            viewCLI.showMarket();
            System.out.println("\n \nthis are the buyable development cards: ");
            viewCLI.showDevMatrix();
            System.out.println(((StartingGameMessage)input).getMessage());
            System.out.println("Type \"ShowActions\" to see commands");
        }

        //8-gestione del salvataggio e della stampa della situazione della partta dopo la riconnessione
        else if(input instanceof ReconnectionMessage){
            viewCLI.setWarehouse(((ReconnectionMessage)input).getWarehouse());
            viewCLI.setStrongbox(((ReconnectionMessage)input).getStrongbox());
            int[][] devMatrix=((ReconnectionMessage)input).getDevMatrix();
            viewCLI.setDevMatrix(devMatrix);
            Arrays.stream(devMatrix[0]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[1]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[2]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[3]).forEach(id-> parser.takeDevCardFromId(id));
            Map<Integer,Boolean> leadcardsId = new HashMap<>();
            ((ReconnectionMessage)input).getCardsId().keySet().stream().filter(integer -> integer>48 && integer<65).forEach(card->{parser.takeLeadCardFromId(card);
                leadcardsId.put(card,((ReconnectionMessage)input).getCardsId().get(card));});
            viewCLI.setLeadCardsId(leadcardsId);
            Map<Integer,Boolean> devcardsId= new HashMap<>();
            ((ReconnectionMessage)input).getCardsId().keySet().stream().filter(integer -> integer>0 && integer<49).forEach(card->{parser.takeDevCardFromId(card);
                devcardsId.put(card,((ReconnectionMessage)input).getCardsId().get(card));});
            viewCLI.setDevCardsId(devcardsId);
            viewCLI.setFaithPosition(((ReconnectionMessage)input).getFaithposition());
            viewCLI.setMarket(((ReconnectionMessage)input).getSimplifiedMarket());
            System.out.println("this is your personal board:");
            viewCLI.showPersonalBoard();
            System.out.println("\n \nthis is the market: ");
            viewCLI.showMarket();
            System.out.println("\n \nthis are the development cards buyable: ");
            viewCLI.showDevMatrix();
            System.out.println("Type \"ShowActions\" to see commands");
        }

        //9-gestione della richiesta di sistemazione delle nuove risorse nel supply
        else if(input instanceof ResourceInSupplyRequest){
            System.out.println("Now you have to place these new resources in warehouse:");
            ((ResourceInSupplyRequest)input).getResources().forEach(System.out::println);
            System.out.println("Please Type: " +
                    "\"PutNewResources:[first Resource] in shelf [Shelf num - 0 to 2], " +
                    "[second Resource] in shelf [Shelf num - 0 to 2]...\"");
            System.out.println("You can alse leave Resource in supply to discard them and give a faith point to all other players");
        }

        //10-gestione dell'aggiurnamento del market
        else if(input instanceof MarketChangeMessage){
            System.out.println("Market is changed:");
            viewCLI.setMarket(((MarketChangeMessage)input).getMarket());
        }

        //11-gestione della modifica del warehouse
        else if (input instanceof WareHouseChangeMessage) {
            System.out.println("Your warehouse is changed:");
            /*
            if(((PersonalBoardChangeMessage)input).getFaithPosition().isPresent()){
                viewCLI.setFaithPosition(((PersonalBoardChangeMessage)input).getFaithPosition().get());
            }
            */
                viewCLI.setWarehouse(((WareHouseChangeMessage)input).getWarehouse());
            }

        //12-gestione della modifica delle cardsid
        else if(input instanceof CardIDChangeMessage){
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

        //13-gestione della modifica della matrice della development card
        else if (input instanceof DevMatrixChangeMessage){
            System.out.println("Development cards matrix is changed:");
            int[][] devMatrix=((DevMatrixChangeMessage)input).getDevMatrix();
            Arrays.stream(devMatrix[0]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[1]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[2]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[3]).forEach(id-> parser.takeDevCardFromId(id));
            viewCLI.setDevMatrix(devMatrix);
        }

        //14-gestione della modifica dello strongbox
        else if (input instanceof StrongboxChangeMessage) {
            System.out.println("Your strongbox is changed:");
            viewCLI.setStrongbox(((StrongboxChangeMessage)input).getStrongbox());
        }

    }

    public void disconnect() {
        System.out.println("blocco i timer dei ping");
        timer.cancel();
        timer.purge();
        System.out.println("sto chiudendo il socket");
        try {
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException | NullPointerException e) {
            System.err.println("Socket closed yet");
        }
    }

    public ViewCLI getViewCLI() {
        return viewCLI;
    }
}
