package it.polimi.ingsw;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class MainClient implements Executor{
    private String ip;
    private int port;
    private PingObserver pongObserver;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private Socket socket;
    private static ClientInput keyboardReader;
    private ViewCLI viewCLI;
    private ClientCardParser parser;
    private static ExecutorService executors;

    public ObjectInputStream getSocketIn() {
        return socketIn;
    }

    public ObjectOutputStream getSocketOut() {
        return socketOut;
    }

    /*public MainClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        parser = new ClientCardParser(this);
        executors= Executors.newCachedThreadPool();
    }

     */

    public static void main(String[] args) {
        ClientCLI client = new ClientCLI("127.0.0.1", 1337);
        new Thread(client).start();
        /*executors= Executors.newCachedThreadPool();
        executors.submit(new Runnable() {
            @Override
            public void run() {
                client.run();
            }
        });

         */
    }
    /*
    private void run() {
        try {
            socket = new Socket(ip, port);
            System.out.println("Connection established");
            socketIn = new ObjectInputStream(socket.getInputStream());
            socketOut = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e) {
            disconnect();
        }
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
        } catch (ClassNotFoundException | NoSuchElementException | IOException e) {
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

        //4-gestione dei messaggio di ping
        else if (input instanceof PingMessage) {
            if (!pongObserver.isStarted()) {
                //System.out.println("era il primo ping, faccio partire il pongObserver");[Debug]
                new Thread(pongObserver).start();
                //System.out.println("pongObserver partito");[Debug]
            } else
                pongObserver.setResponse(true);
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
            viewCLI.setMarket(((MarketChangeMessage)input).getMarket());
        }

        //11-gestione della modifica della personal board
        else if (input instanceof PersonalBoardChangeMessage) {
            if (((PersonalBoardChangeMessage) input).getCardID().isPresent()) {
                ((PersonalBoardChangeMessage) input).getCardID().get().keySet().stream().filter(integer -> integer > 48 && integer < 65).forEach(card -> {if (!viewCLI.getCardsFromId().containsKey(card))
                    parser.takeLeadCardFromId(card);
                    if (!viewCLI.getLeadCardsId().get(card))viewCLI.getLeadCardsId().put(card, ((PersonalBoardChangeMessage) input).getCardID().get().get(card));
                });
                ((PersonalBoardChangeMessage) input).getCardID().get().keySet().stream().filter(integer -> integer >= 0 && integer <= 48).forEach(card -> {if (!viewCLI.getCardsFromId().containsKey(card))
                    parser.takeDevCardFromId(card);
                    if (!viewCLI.getDevCardsId().get(card))viewCLI.getDevCardsId().put(card, ((PersonalBoardChangeMessage) input).getCardID().get().get(card));
                });

            }
            if(((PersonalBoardChangeMessage)input).getFaithPosition().isPresent()){
                viewCLI.setFaithPosition(((PersonalBoardChangeMessage)input).getFaithPosition().get());
            }
            if (((PersonalBoardChangeMessage)input).getWarehouse().isPresent()){
                viewCLI.setWarehouse(((PersonalBoardChangeMessage)input).getWarehouse().get());
            }
        }
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
*/
    @Override
    public void execute(Runnable command) {

    }
}
