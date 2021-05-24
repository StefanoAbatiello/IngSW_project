package it.polimi.ingsw;

import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

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
        keyboardReader = new ClientInput(this, socketOut);
        new Thread(keyboardReader).start();
        pongObserver = new PongObserver(this, socketOut);
        viewCLI = new ViewCLI();
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

    public void send(SerializedMessage message){
        try {
            socketOut.writeObject(message);
            socketOut.flush();
        } catch (IOException e) {
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
        else if (input instanceof CreatingGameMessage) {
            System.out.println(((CreatingGameMessage) input).getMessage());
        }

        //5-gestione dei messaggio di ping
        else if (input instanceof PingMessage) {
            if (!pongObserver.isStarted()) {
                //System.out.println("era il primo ping, faccio partire il pongObserver");[Debug]
                new Thread(pongObserver).start();
                //System.out.println("pongObserver partito");[Debug]
            } else
                pongObserver.setResponse(true);
        }

        //6-gestione della richiesta di scegliere la/le risorsa/e iniziale/
        else if (input instanceof GetInitialResourcesAction) {
            System.out.println(((GetInitialResourcesAction) input).getMessage());
            System.out.println("Type \"InitialResource:[COIN/SERVANT/SHIELD/STONE] in shelf:[shef number]\"");
        }

        //7-gestione della richiesta di scegliere quali leader card mantenere
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

        //8-gestione del salcataggio e della stampa della situazione iniziale della partita
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
            System.out.println("\n \nthis are the development cards buyable: ");
            viewCLI.showDevMatrix();
            System.out.println(((StartingGameMessage)input).getMessage());
            System.out.println("Type \"ShowActions\" to see commands");
        }

        //9-gestione del salvataggio e della stampa della situazione della partta dopo la riconnessione
        else if(input instanceof ReconnectionMessage){
            viewCLI.setWarehouse(((ReconnectionMessage)input).getWarehouse());
            int[][] devMatrix=((ReconnectionMessage)input).getDevMatrix();
            viewCLI.setDevMatrix(devMatrix);
            Arrays.stream(devMatrix[0]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[1]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[2]).forEach(id-> parser.takeDevCardFromId(id));
            Arrays.stream(devMatrix[3]).forEach(id-> parser.takeDevCardFromId(id));
            Map<Integer,Boolean> leadcardsId = new HashMap<>();
            ((ReconnectionMessage)input).getCardsId().keySet().stream().filter(integer -> integer>48 && integer<65).forEach(card->parser.takeLeadCardFromId(card));
            ((ReconnectionMessage)input).getCardsId().keySet().stream().filter(integer -> integer>48 && integer<65).forEach(card->leadcardsId.put(card,((ReconnectionMessage)input).getCardsId().get(card)));
            viewCLI.setLeadCardsId(leadcardsId);
            Map<Integer,Boolean> devcardsId= new HashMap<>();
            ((ReconnectionMessage)input).getCardsId().keySet().stream().filter(integer -> integer>0 && integer<49).forEach(card->parser.takeDevCardFromId(card));
            ((ReconnectionMessage)input).getCardsId().keySet().stream().filter(integer -> integer>0 && integer<49).forEach(card->devcardsId.put(card,((ReconnectionMessage)input).getCardsId().get(card)));
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
