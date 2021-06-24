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
    //TODO ottimizzare id e virtual client negli usi
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

    public void setStateOfGame(GameState stateOfGame) {
        this.stateOfGame = stateOfGame;
    }

    public boolean isLobbyFull(){
        return seatsAvailable == 0;
    }

    public GameState getStateOfGame() {
        return stateOfGame;
    }

    public void sendAll(SerializedMessage message) {
        for(VirtualClient player: positionFromClient.keySet()){
            player.getClientHandler().send(message);
        }
    }

    public Map<Integer, VirtualClient> getClientFromPosition() {
        return clientFromPosition;
    }


    public ArrayList<String> getPlayersName(){
        System.out.println("mi sto salvando i nomi dei giocatori");
        ArrayList<String> playersName = new ArrayList<>();
        for (VirtualClient client : clientFromPosition.values()) {
            playersName.add(client.getNickName());
        }
        return  playersName;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public Map<VirtualClient,Integer> reinsertPlayer(int id) {
        System.out.println("sto reinserendo il player nella lobby");
        String name=server.getNameFromId().get(id);
        if(getStateOfGame()==GameState.PREPARATION1||getStateOfGame()==GameState.PREPARATION2||getStateOfGame()==GameState.ONGOING){
            System.out.println("la partita è già iniziata");
            controller.insertPlayerInOrder(id,name);
            this.seatsAvailable--;
            controller.sendInfoOfgame(id,name);
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

    public Map<VirtualClient,Integer> insertPlayer(int id) {
        int position=positionFromClient.size();
        positionFromClient.put(server.getClientFromId().get(id), position);
        clientFromPosition.put(position,server.getClientFromId().get(id));
        server.getClientFromId().get(id).giveLobby(this);
        server.getLobbyFromClientID().put(id,this);
        this.seatsAvailable--;
        if(isLobbyFull()) {
            System.out.println("numero di giocatori raggiunto, inizia la partita!!!");
            sendAll(new LobbyMessage("number of players reached, the game can start!!!"));
            controller.createGame();
        }
        return positionFromClient;
    }


    public Map<VirtualClient,Integer> getPositionFromClient() {
        return positionFromClient;
    }

    public Map<VirtualClient,Integer> removePlayer(VirtualClient player) {
        if(stateOfGame==GameState.ONGOING) {
            if (player.equals(controller.getActualPlayerTurn())) {
                controller.turnUpdate();
            }
        }
        clientFromPosition.remove(positionFromClient.get(player));
        positionFromClient.remove(player);
        seatsAvailable++;
        return positionFromClient;
    }

//TODO nel clientHandler stampo "azione giocatore n:" e il risultato di tale azione
    public synchronized void actionHandler(SerializedMessage input, int id) {
        Object gameObj;
        System.out.println("sono nell'handler della lobby");
        //1-gestisco la scelta del giocatore di quali leader card tenere
        if(input instanceof ChosenLeadMessage){
            //TODO check che siano solo due nel messaggio CLI
            System.out.println("sto leggendo il ChosenLeadMessage");
            if(stateOfGame==GameState.PREPARATION1) {
                int firstId, secondId;
                firstId = ((ChosenLeadMessage) input).getChosenId().get(0);
                secondId = ((ChosenLeadMessage) input).getChosenId().get(1);
                System.out.println("ho letto gli id");
                if(controller.check2Leads(id, firstId, secondId)) {
                    System.out.println("carte scelte");
                    if (controller.checkAllPlayersChooseLeads()) {
                        System.out.println("tutti hanno scelto le lead cards");
                        controller.askInitialResources();
                    }
                }
            }
        }

        //2-gestisco le risorse iniziali scelte dal giocatore
        else if(input instanceof InitialResourceMessage){
            if(stateOfGame==GameState.PREPARATION2){
                System.out.println("sto leggendo il InitialResourceMessage");
                Resource resource=Resource.valueOf(((InitialResourceMessage)input).getResource());
                int shelfNum=((InitialResourceMessage)input).getShelfNum();
                System.out.println("ho letto il messaggio");
                try {
                    controller.checkInsertResourcePosition(id, shelfNum, resource);
                    System.out.println("la risorsa è stata depositata");
                    if(controller.checkInitialResources()){
                        System.out.println("tutti i giocatori hanno scelto le risorse iniziali");
                        controller.startGame();
                    }
                } catch (ResourceNotValidException e) {
                    VirtualClient client =server.getClientFromId().get(id);
                    server.getClientFromId().get(id).getClientHandler().send(new GetInitialResourcesAction("You choose a not valid resource or shelf", controller.playerInitialResources(positionFromClient.get(client))));
                }

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
                    if (controller.checkBuy(cardID, id, slot)) {
                        //result = new ActionAnswer("carta" + gameObj + "comprata");
                    }
                } catch (ActionAlreadySetException e) {
                    controller.getActualPlayerTurn().getClientHandler().send(new LobbyMessage("Actions already set for this player"));
                } catch (InvalidSlotException e) {
                    controller.getActualPlayerTurn().getClientHandler().send(new LobbyMessage("Invalid Slot"));
                } catch (ResourceNotValidException e) {
                    e.printStackTrace();
                } catch (CardNotOnTableException e) {
                    e.printStackTrace();
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
                } catch (NotAcceptableSelectorException e) {
                    server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage("Coordinate not valid, please chose another one"));
                } catch (FullSupplyException e) {
                    e.printStackTrace();
                } catch (ActionAlreadySetException actionAlreadySetException) {
                    server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage("Main action done yet"));                }
            }
        }

        //6-gestisco il posizionamento delle nuove risorse prese dal supply
        else if(input instanceof ResourceInSupplyAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())){
            System.out.println("è una richiesta di posizionare le nuove risorse");
            if(stateOfGame==GameState.ONGOING){
               controller.checkPositionOfResources(((ResourceInSupplyAction)input).getWarehouse(),id);
            }
        }

        //5-gestisco le produzioni scelte un giocatore
        else if (input instanceof ProductionAction && server.getClientFromId().get(id).equals(controller.getActualPlayerTurn())) {
            if(stateOfGame==GameState.ONGOING) {
                ArrayList<Integer> cardProd= ((ProductionAction) input).getCardProductions();
                ArrayList<String> personalProdIn= ((ProductionAction) input).getPersProdIn();
                String personalProdOut=((ProductionAction) input).getPersProdOut();
                ArrayList<String> leadProdOut= ((ProductionAction) input).getLeadProdOut();
                try {
                    if (controller.checkProduction(cardProd, personalProdIn, personalProdOut, leadProdOut, id)) {
                        //result = new ActionAnswer("produzioni effettuate \n(carte: " + cardProd + "\npersonal:"+personalProdIn+" )");
                    }
                } catch (ActionAlreadySetException actionAlreadySetException) {
                    controller.getActualPlayerTurn().getClientHandler().send(new LobbyMessage("Actions already set for this player"));
                } catch (CardNotOwnedByPlayerOrNotActiveException e) {
                    e.printStackTrace();
                } catch (ResourceNotValidException e) {
                    e.printStackTrace();
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
                controller.checkChangeChooosable(id,((ChangeChoosableAction) input).getNewRes());
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
}