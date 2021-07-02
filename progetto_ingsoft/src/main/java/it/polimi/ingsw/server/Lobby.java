package it.polimi.ingsw.server;


import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lobby {
    private final MainServer server;
    private final int lobbyID;
    private int seatsAvailable;
    private final Map<VirtualClient,Integer> positionFromClient;
    private final Map<Integer,VirtualClient> clientFromPosition;
    private GameState stateOfGame;
    private final Controller controller;

    public Lobby(int lobbyID, int seatsAvailable, MainServer server){
        this.stateOfGame=GameState.WAITING;
        this.lobbyID=lobbyID;
        this.seatsAvailable = seatsAvailable;
        this.positionFromClient = new HashMap<>();
        this.clientFromPosition =new HashMap<>();
        this.server=server;
        controller=new Controller(this,this.server);
    }

    public int getLobbyID() {
        return lobbyID;
    }

    /**
     * it sets the phase of the game
     * @param stateOfGame the phase of game
     */
    public void setStateOfGame(GameState stateOfGame) {
        this.stateOfGame = stateOfGame;
    }

    /**
     *
     * @return if the lobby is full
     */
    public boolean isLobbyFull(){
        return seatsAvailable == 0;
    }

    /**
     *
     * @return the phase of the game
     */
    public GameState getStateOfGame() {
        return stateOfGame;
    }

    /**
     * this sends a message to all the players of a lobby
     * @param message the message to send
     */
    public void sendAll(SerializedMessage message) {
        for(VirtualClient player: clientFromPosition.values()){
            player.getClientHandler().send(message);
        }
    }

    /**
     *
     * @return the map of the clients and their positions
     */
    public synchronized Map<Integer, VirtualClient> getClientFromPosition() {
        return clientFromPosition;
    }


    /**
     *
     * @return the names of the players in the lobby
     */
    public ArrayList<String> getPlayersName(){
        ArrayList<String> playersName = new ArrayList<>();
        for (VirtualClient client : clientFromPosition.values()) {
            playersName.add(client.getNickName());
        }
        return  playersName;
    }

    /**
     *
     * @return the available seats in a lobby
     */
    public synchronized int getSeatsAvailable() {
        return seatsAvailable;
    }

    /**
     *
     * @param id id of the player
     * @return the map in which there is the client and their positions
     */
    public synchronized Map<VirtualClient,Integer> reinsertPlayer(int id) {
        System.out.println("sto reinserendo il player nella lobby");
        String name=server.getNameFromId().get(id);
        if(getStateOfGame()==GameState.PREPARATION1||getStateOfGame()==GameState.PREPARATION2||getStateOfGame()==GameState.ONGOING){
            System.out.println("la partita è già iniziata");
            controller.insertPlayerInOrder(id,name);
            this.seatsAvailable--;
            controller.sendInfoAfterReconnection(id);
        } else{
            System.out.println("la partita non è ancora iniziata. Inserisco il giocatore con ultimo");
            int position=positionFromClient.size();
            positionFromClient.put(server.getClientFromId().get(id), position);
            clientFromPosition.put(position,server.getClientFromId().get(id));
            this.seatsAvailable--;
            if (isLobbyFull())
                controller.startGame();
        }
        server.getClientFromId().get(id).giveLobby(server.getLobbyFromClientID().get(id));
        System.out.println("ho inserito il giocatore nella lobby");
        return positionFromClient;
    }

    /**
     *
     * @param id
     * @return
     */
    public synchronized Map<VirtualClient,Integer> insertPlayer(int id) {
        int position=positionFromClient.size();
        positionFromClient.put(server.getClientFromId().get(id), position);
        clientFromPosition.put(position,server.getClientFromId().get(id));
        server.getClientFromId().get(id).giveLobby(this);
        server.getLobbyFromClientID().put(id,this);
        this.seatsAvailable--;
        if(isLobbyFull()) {
            System.out.println("numero di giocatori raggiunto, inizia la partita!!!");
            controller.createGame();
        }
        return positionFromClient;
    }


    public synchronized Map<VirtualClient,Integer> getPositionFromClient() {
        return positionFromClient;
    }

    public synchronized Map<VirtualClient,Integer> removePlayer(VirtualClient player) {
        clientFromPosition.remove(positionFromClient.get(player));
        positionFromClient.remove(player);
        if (playersOnline()>0)
            sendAll(new LobbyMessage(player.getNickName() +" left the game"));
        seatsAvailable++;
        if(stateOfGame==GameState.ONGOING) {
            if (player.equals(controller.getActualPlayerTurn())) {
                controller.actionForDisconnection(player.getID());
            }
        }
        return positionFromClient;
    }

    public synchronized void actionHandler(SerializedMessage input, int id) {
        Object gameObj;
        System.out.println("sono nell'handler della lobby");
        //1-gestisco la scelta del giocatore di quali leader card tenere
        if(input instanceof ChosenLeadMessage){
            System.out.println("sto leggendo il ChosenLeadMessage");
            if(stateOfGame==GameState.PREPARATION1) {
                int firstId, secondId;
                firstId = ((ChosenLeadMessage) input).getChosenId().get(0);
                secondId = ((ChosenLeadMessage) input).getChosenId().get(1);
                controller.check2Leads(id, firstId, secondId);
            }
        }

        //2-gestisco le risorse iniziali scelte dal giocatore
        else if(input instanceof InitialResourceMessage){
            if(stateOfGame==GameState.PREPARATION2){
                System.out.println("sto leggendo il InitialResourceMessage");
                Map<Integer,String> resources=((InitialResourceMessage)input).getResource();
                Map<Integer,Integer> shelves=((InitialResourceMessage)input).getShelfNum();
                controller.checkInsertResourcePosition(id, shelves, resources);
            }
        }

        //3- gestisco l'acquisto di una devCard da parte di un giocatore
        else if (input instanceof BuyCardAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())) {
            if(stateOfGame==GameState.ONGOING) {
                System.out.println("è un buycardaction");
                int cardID = ((BuyCardAction) input).getCard();
                int slot = ((BuyCardAction) input).getSlot();
                System.out.println("card "+ cardID + " in slot "+slot);
                try {
                     controller.checkBuy(cardID, slot);
                } catch (ActionAlreadySetException | InvalidSlotException | CardNotOnTableException | ResourceNotValidException e) {
                    controller.getActualPlayerTurn().getClientHandler().send(new LobbyMessage(e.getMessage()));
                }
            }
        }

        //4-gestisco l'acquisizione delle risorse dal market da parte di un giocatore
        else if (input instanceof MarketAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())) {
            if(stateOfGame==GameState.ONGOING) {
                System.out.println("è una richiesta di acquisire nuove risorse");
                int selector = ((MarketAction) input).getCoordinate();
                try {
                    System.out.println("controllo se è possibile eseguire la richiesta");
                    controller.checkMarket(selector, id);
                } catch (ActionAlreadySetException | NotAcceptableSelectorException e) {
                    server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage(e.getMessage()));
                }
            }
        }

        //6-gestisco il posizionamento delle nuove risorse prese dal supply
        else if(input instanceof ResourceInSupplyAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())){
            System.out.println("è una richiesta di posizionare le nuove risorse");
            if(stateOfGame==GameState.MARKET){
               controller.checkPositionOfResources(((ResourceInSupplyAction)input).getWarehouse(),id);
            }
        }
        else if(input instanceof WareHouseDisposition && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())){
            System.out.println("ho ricevuto un new disposition");
            if (stateOfGame==GameState.ONGOING)
                controller.checkPositionOfResources(((WareHouseDisposition)input).getWarehouse(),id);
        }

        //5-gestisco le produzioni scelte un giocatore
        else if (input instanceof ProductionAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())) {
            if(stateOfGame==GameState.ONGOING) {
                ArrayList<Integer> cardProd= ((ProductionAction) input).getCardProductions();
                ArrayList<String> personalProdIn= ((ProductionAction) input).getPersProdIn();
                String personalProdOut=((ProductionAction) input).getPersProdOut();
                ArrayList<String> leadProdOut= ((ProductionAction) input).getLeadProdOut();
                try {
                     controller.checkProduction(cardProd, personalProdIn, personalProdOut, leadProdOut);

                } catch (ActionAlreadySetException e) {
                    controller.getActualPlayerTurn().getClientHandler().send(new LobbyMessage(e.getMessage()));
                }
            }
        }

        //6-gestisco l'attivazione di una leader card da parte di un giocatore
        else if (input instanceof ActiveLeadAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())) {
            if (stateOfGame == GameState.ONGOING) {
                System.out.println("è una lead activation");
                int cardID = ((ActiveLeadAction) input).getLead();
                controller.checkLeadActivation(cardID, id);
            }
        }

        //7-gestisco la scelta di scartare una leader card da parte di un giocatore
        else if (input instanceof DiscardLeadAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())) {
            if (stateOfGame == GameState.ONGOING) {
                gameObj = ((DiscardLeadAction) input).getLead();
                controller.checkDiscardLead((int) gameObj, id);
            }
        }

        //8-gestisco il cambio del turno dei giocatori
        else if(input instanceof TurnChangeMessage && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())){
            if (stateOfGame==GameState.ONGOING){
                controller.turnUpdate();
            }
        }
        //9-gestisco il cambio delle white marble
        else if (input instanceof ChangeChoosableAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())){
            if(stateOfGame==GameState.MARKET){
                controller.checkChangeChoosable(id,((ChangeChoosableAction) input).getNewRes());
            }
        }
        else if(!server.getClientFromId().get(id).equals(controller.getActualPlayerTurn()))
            server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage("Wait for your turn!"));
    }

    /**
     * @return the number of online players belonging to this lobby
     */
    public int playersOnline() {
        return (int) clientFromPosition.values().stream().filter(client -> server.isClientOnline(client.getID())).count();
    }

    /**
     *
     * @return the controller of the game
     */
    public Controller getController() {
        return controller;
    }
}