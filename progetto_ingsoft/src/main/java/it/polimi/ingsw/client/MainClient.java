package it.polimi.ingsw.client;

import it.polimi.ingsw.Sender;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import it.polimi.ingsw.org.example.GUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class MainClient implements Runnable, Sender {



    private View view;
    private String ip;
    private int port;
    private PingObserver pingObserver;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private Socket socket;
    private static ClientInput keyboardReader;
    private ViewCLI viewCLI;
    private ClientCardParser parser;
    private Timer timer;
    private static final int timerPeriod = 30000; // time in milliseconds


    public MainClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        parser = new ClientCardParser(this);
        timer=new Timer();
        this.viewCLI = new ViewCLI();
        this.view=new CLI(parser,viewCLI);

    }

    public MainClient(String ip, int port,GUI gui) {
        this.ip = ip;
        this.port = port;
        parser = new ClientCardParser(this);
        timer=new Timer();
        this.view=gui;
        viewCLI = new ViewCLI();

    }

    public void run() {
        try {
            if(view instanceof CLI)
            {
                keyboardReader = new ClientInput(this);
                new Thread(keyboardReader).start();
            }
            socket = new Socket(ip, port);
            System.out.println("Connection established");
            socketIn = new ObjectInputStream(socket.getInputStream());
            socketOut = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("stream created");
        }catch (IOException | NullPointerException e) {
            System.err.println("Error in stream creation");
            disconnect();
        }

        pingObserver = new PingObserver(this);
        SerializedMessage input;
        try {
            while (true) {
                input = (SerializedMessage) socketIn.readObject();
                //System.out.println("ho letto un messaggio");
                actionHandler(input);
            }
        } catch (ClassNotFoundException | IOException | NullPointerException e) {
            System.err.println("Connection /closed");
            System.err.println(e.getMessage());
            e.printStackTrace();
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

    private synchronized void actionHandler(SerializedMessage input) {
        //1-gestisco la richiesta del nickname
        if (input instanceof NickNameAction) {
            view.nicknameHandler((NickNameAction) input);
        }

        //2-gestisco la richiesta del numero di giocatori che dovrà avere la lobby che sto creando
        else if (input instanceof RequestNumOfPlayers) {
            view.numOfPlayerHandler((RequestNumOfPlayers) input);
        }

        //3- gestisco la ricezione di messaggi di servizio all'interno della lobby
        else if (input instanceof LobbyMessage) {
            view.lobbyMessageHandler((LobbyMessage) input);
        }

       //3bis - gestisco waiting room message
        else if (input instanceof WaitingRoomAction) {
            view.waitingRoomHandler((WaitingRoomAction) input);
        }

        //4-gestione dei messaggio di ping
        else if (input instanceof PingMessage) {
            //System.out.println("ho ricevuto un ping");[Debug]
            pingObserver.setResponse(true);
            send(new PingMessage());
            //System.out.println("ho inviato la risposta");[Debug]
            if (!pingObserver.isStarted()) {
                //System.out.println("era il primo ping, faccio partire il pongObserver");[Debug]
                try{
                    timer.schedule(pingObserver, 0, timerPeriod);
                } catch (IllegalStateException e) {
                    System.out.println("timer scheduled yet");
                }
                //System.out.println("pongObserver partito");[Debug]
            }
        }

        //5-gestione della richiesta di scegliere la/le risorsa/e iniziale/
        else if (input instanceof GetInitialResourcesAction) {
            view.initialResourceHandler((GetInitialResourcesAction) input);
        }

        //6-gestione della richiesta di scegliere quali leader card mantenere
        else if (input instanceof LeaderCardDistribution) {
            view.leadCardHandler((LeaderCardDistribution) input);
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
            view.gameSetupHandler(viewCLI,input);
        }

        //8-gestione del salvataggio e della stampa della situazione della partta dopo la riconnessione
        else if(input instanceof ReconnectionMessage){
            viewCLI.setWarehouse(((ReconnectionMessage)input).getWarehouse());
            viewCLI.setStrongbox(((ReconnectionMessage)input).getStrongbox());
            int[][] devMatrix=((ReconnectionMessage)input).getDevMatrix();
            viewCLI.setDevMatrix(devMatrix);
            //CLI.matrixParser(devMatrix, parser);
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
            view.supplyHandler((ResourceInSupplyRequest) input);
            }

        //10-gestione dell'aggiurnamento del market
        else if(input instanceof MarketChangeMessage){
            view.marketHandler((MarketChangeMessage) input);
        }

        //11-gestione della modifica del warehouse
        else if (input instanceof WareHouseChangeMessage) {
            view.warehouseHandler((WareHouseChangeMessage) input);
            }

        //12-gestione della modifica delle cardsid
        else if(input instanceof CardIDChangeMessage){
           view.personalCardHandler((CardIDChangeMessage) input);
        }

        //13-gestione della modifica della matrice della development card
        else if (input instanceof DevMatrixChangeMessage){
            view.devMatrixHandler((DevMatrixChangeMessage) input);
        }

        //14-gestione della modifica dello strongbox
        else if (input instanceof StrongboxChangeMessage) {
            view.strongboxHandler((StrongboxChangeMessage) input);
        }

        //15-gestione della richiesta di scegliere risorse con whitemarble
        else if(input instanceof  ChangeChoosableResourceRequest){
            view.choosableResourceHandler((ChangeChoosableResourceRequest) input);
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
