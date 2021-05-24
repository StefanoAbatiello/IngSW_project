package it.polimi.ingsw.server;


import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.gameActions.*;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.*;

import java.util.ArrayList;
import java.util.Optional;

public class Lobby {
    private final MainServer server;
    private final int lobbyID;
    private int seatsAvailable;
    //TODO ottimizzare id e virtual client negli usi
    private ArrayList<VirtualClient> actualPlayers;
    private GameState stateOfGame;
    private final Controller controller;
    private VirtualView virtualVew;

    public Lobby(int clientID, int lobbyID, int seatsAvailable, MainServer server){
        this.lobbyID=lobbyID;
        this.seatsAvailable = seatsAvailable;
        this.actualPlayers= new ArrayList<>();
        this.stateOfGame=GameState.WAITING;
        this.server=server;
        this.server.getLobbyFromClientID().put(clientID, this);
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
        for(VirtualClient player:actualPlayers){
            player.getClientHandler().send(message);
        }
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public ArrayList<VirtualClient> reinsertPlayer(int id){
        actualPlayers.add(server.getClientFromId().get(id));
        this.seatsAvailable--;
        server.getClientFromId().get(id).giveLobby(server.getLobbyFromClientID().get(id));
        return actualPlayers;
    }


    public ArrayList<VirtualClient> insertPlayer(int id) {
            actualPlayers.add(server.getClientFromId().get(id));
            server.getClientFromId().get(id).giveLobby(this);
            this.seatsAvailable--;
            if(isLobbyFull()) {
                System.out.println("numero di giocatori raggiunto, inizia la partita!!!");
                sendAll(new LobbyMessage("number of players reached, the game can start!!!"));
                controller.createGame();
            }
            return actualPlayers;
    }


    public ArrayList<VirtualClient> getPlayers() {
        return actualPlayers;
    }

    public ArrayList<VirtualClient> removePlayer(VirtualClient player) {
        actualPlayers.remove(player);
        seatsAvailable++;
        return actualPlayers;
    }

//TODO nel clientHandler stampo "azione giocatore n:" e il risultato di tale azione
    public void actionHandler(SerializedMessage input, int id) {
        //TODO ragiono su inizializzazione
        ActionAnswer result = null;
        //TODO ragiono su oggetti che passa il client
        Object gameObj;
        Object gameObj2;
        System.out.println("sono nell'handler della lobby");
        //1-gestisco la scelta del giocatore di quali leader card tenere
        if(input instanceof ChosenLeadMessage){
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
                    server.getClientFromId().get(id).getClientHandler().send(new GetInitialResourcesActions("You choose a not valid resource or shelf"));
                }

            }
        }

        //3- gestisco l'acquisto di una devCard da parte di un giocatore
        else if (input instanceof BuyCardAction) {
            if(stateOfGame==GameState.ONGOING) {
                gameObj = ((BuyCardAction) input).getCard();
                gameObj2 = ((BuyCardAction) input).getSlot();

                try {
                    if (controller.checkBuy((int) gameObj, id, (int) gameObj2)) {
                        result = new ActionAnswer("carta" + gameObj + "comprata");
                    }
                } catch (ActionAlreadySetException actionAlreadySetException) {
                    actualPlayers.get(id).getClientHandler().send(new ActionAlreadySet("Actions already set for this player"));
                } catch (InvalidSlotException e) {
                    //TODO send ask new slot
                } catch (ResourceNotValidException e) {
                    e.printStackTrace();
                } catch (CardNotOnTableException e) {
                    e.printStackTrace();
                }
            }
        }

        //4-gestisco l'acquisizione delle risorse dal market da parte di un giocatore
        else if (input instanceof MarketAction) {
            if(stateOfGame==GameState.ONGOING) {
                gameObj = ((MarketAction) input).getCoordinate();
                try {
                    if (controller.checkMarket((int) gameObj, id)) {
                        result = new ActionAnswer("mercato cambiato con successo (da coordinata: " + gameObj + " )");
                    }
                } catch (NotAcceptableSelectorException e) {
                    e.printStackTrace();
                } catch (FullSupplyException e) {
                    e.printStackTrace();
                } catch (ActionAlreadySetException actionAlreadySetException) {
                    actualPlayers.get(id).getClientHandler().send(new ActionAlreadySet("Actions already set for this player"));
                }
            }
        }

        //5-gestisco le produzioni scelte un giocatore
        else if (input instanceof ProductionAction) {
            if(stateOfGame==GameState.ONGOING) {
                ArrayList<Integer> cardProd= ((ProductionAction) input).getCardProductions();
                ArrayList<String> personalProdIn= ((ProductionAction) input).getPersProdIn();
                Optional<String> personalProdOut=((ProductionAction) input).getPersProdOut();
                Optional<String> leadProdOut= ((ProductionAction) input).getLeadProdOut();
                try {
                    if (controller.checkProduction(cardProd, personalProdIn, personalProdOut, leadProdOut, id)) {
                        result = new ActionAnswer("produzioni effettuate \n(carte: " + cardProd + "\npersonal:"+personalProdIn+" )");
                    }
                } catch (ActionAlreadySetException actionAlreadySetException) {
                    actualPlayers.get(id).getClientHandler().send(new ActionAlreadySet("Actions already set for this player"));
                } catch (CardNotOwnedByPlayerOrNotActiveException e) {
                    e.printStackTrace();
                } catch (ResourceNotValidException e) {
                    e.printStackTrace();
                }
            }
        }

        //6-gestisco l'attivazione di una leader card da parte di un giocatore
        else if (input instanceof ActiveLeadAction) {
            if (stateOfGame == GameState.ONGOING) {
                gameObj = ((ActiveLeadAction) input).getLead();
                if (controller.checkLeadActivation((int) gameObj, id)) {
                    result = new ActionAnswer("lead card attivata: " + gameObj);
                }
            }
        }

        //7-gestisco la scelta di scartare una leader card da parte di un giocatore
        else if (input instanceof DiscardLeadAction) {
            if (stateOfGame == GameState.ONGOING) {
                gameObj = ((DiscardLeadAction) input).getLead();
                if (controller.checkDiscardLead((int) gameObj, id)) {
                    result = new ActionAnswer("lead card scartata: " + gameObj);
                }
            }
        }

        //TODO sposta risorse negli scaffali
    }
}