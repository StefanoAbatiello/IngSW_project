package it.polimi.ingsw;

import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class MainClient {
    private String ip;
    private int port;
    private PongObserver pongObserver;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private Socket socket;
    private static ClientInput keyboardReader;
    private ViewCLI viewCLI;
    private ClientCardParser parser;

    public ObjectInputStream getSocketIn() {
        return socketIn;
    }

    public ObjectOutputStream getSocketOut() {
        return socketOut;
    }

    public MainClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        parser = new ClientCardParser(this);
    }
    public static void main(String[] args) {
        MainClient client = new MainClient("127.0.0.1", 1337);
        try {
            client.startClient();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void startClient() throws IOException {
        socket = new Socket(ip, port);
        System.out.println("Connection established");
        socketIn = new ObjectInputStream(socket.getInputStream());
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        keyboardReader=new ClientInput(this, socketOut);
        new Thread(keyboardReader).start();
        pongObserver = new PongObserver(this, socketOut);
        SerializedMessage input;
        try {
            while (true) {
                input = (SerializedMessage) socketIn.readObject();
                actionHandler(input, socketIn, socketOut);
            }
        } catch (ClassNotFoundException | NoSuchElementException e) {
            System.out.println("Connection closed");
            disconnect();
        } finally {
            disconnect();
        }
    }

    private void actionHandler(SerializedMessage input, ObjectInputStream socketIn, ObjectOutputStream socketOut) {
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

        //4-gestione dell'avviso di inizio partita
        else if(input instanceof CreatingGameMessage){
            System.out.println(((CreatingGameMessage) input).getMessage());
            viewCLI = new ViewCLI();
        }

        //5-gestione dei messaggio di ping
        else if(input instanceof PingMessage) {
            if(!pongObserver.isStarted()) {
                //System.out.println("era il primo ping, faccio partire il pongObserver");[Debug]
                new Thread(pongObserver).start();
                //System.out.println("pongObserver partito");[Debug]
            } else
                pongObserver.setResponse(true);
        }

        //6-gestione della richiesta di scegliere la/le risorsa/e iniziale/
        else if(input instanceof GetInitialResourcesActions){
            System.out.println(((GetInitialResourcesActions)input).getMessage());
            System.out.println("Type \"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
        }

        //7-gestione della richiesta di scegliere quali leader card mantenere
        else if(input instanceof LeaderCardDistribution){
            ArrayList<Integer> leadCardsId = ((LeaderCardDistribution)input).getLeadCardsId();
            System.out.println("Leader cards:");
            for(int id:leadCardsId){
                parser.takeLeadCardFromId(id);
                ArrayList<String> [] cardValues = viewCLI.getCardsFromId().get(id);
                System.out.println("ID: " + id );
                System.out.println("    Ability: " + cardValues[0].get(0));
                System.out.println("    Resource: " + cardValues[1].get(0));
                if(!cardValues[2].isEmpty() && !cardValues[3].isEmpty())
                    System.out.println("    Requirements: " + cardValues[2].get(0)/*num of resources required*/
                            + cardValues[3].get(0)/*type of resources required*/);
                else {
                    if(cardValues[5].size()==1)
                        System.out.println("    Requirements: a " + cardValues[5].get(0)/*color of dev card*/
                                + " devCard of level " + cardValues[4].get(0)/*level of devCArd*/);
                    else{
                        System.out.println("    Requirements: devCards of color ");
                        cardValues[5].forEach(s-> System.out.print(s +", "));
                        System.out.println("");
                    }
                }
            }
            System.out.println("");
            System.out.println("Type \"ChosenLeads:[first LeadId],[second LeadId]\"");
        }

        else if(input instanceof StartingGameMessage){
            viewCLI.setWarehouse(((StartingGameMessage)input).getWarehouse());
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
            System.out.println("this are dev card buyable: ");
            viewCLI.showDevMatrix();
            System.out.println(((StartingGameMessage)input).getMessage());
            System.out.println("Type \"ShowActions\" to see commands");
        }
        //TODO
    }

    public void disconnect() {
        System.out.println("sto chiudendo il socket");
        try {
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ViewCLI getViewCLI() {
        return viewCLI;
    }
}
