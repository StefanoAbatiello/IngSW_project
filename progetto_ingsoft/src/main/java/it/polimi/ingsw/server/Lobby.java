package it.polimi.ingsw.server;


import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.gameActions.*;
import it.polimi.ingsw.messages.LobbyMessage;
import it.polimi.ingsw.messages.SerializedMessage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.cardExceptions.CardNotOnTableException;

import java.util.ArrayList;

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

    public ArrayList<VirtualClient> insertPlayer(int id) {
            actualPlayers.add(server.getClientFromId().get(id));
            server.getClientFromId().get(id).giveLobby(this);
            this.seatsAvailable--;
            if(isLobbyFull()) {
                System.out.println("numero di giocatori raggiunto, inizia la partita!!!");
                sendAll((SerializedMessage) new LobbyMessage("number of players reached, the game can start!!!"));
                startGame();
                virtualVew=controller.startGame();
            }
            return actualPlayers;
    }

    private void startGame() {
        controller.startGame();
        for(VirtualClient player: actualPlayers) {
           //TODO direttamente nel controller senza return boolean
            //boolean initial= player.getClientHandler().askInitialResources();
            //TODO metodo che chiede id carte che vuole tenere, controller manda choose e controlla che abbia mandato 2 carte, id validi, e carte che appartengono al giocatore
            //boolean initialLeads= player.getClientHandler().askPlayerLeads();

            /*ObjectInputStream inputStream= player.getClientHandler().getInputStream();
            SerializedMessage input;
            if(player.getID()==1) {
                do {
                    player.getClientHandler().send(new GetInitialResourcesAction("Please choose 1 resource and its position (es." + ConsoleColors.RED + "COIN" + ConsoleColors.RESET + "in shelf " + ConsoleColors.RED + "1" + ConsoleColors.RESET));
                    try {
                        input = (SerializedMessage) inputStream.readObject();
                        actionHandler(input, player.getID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } while (!(input instanceof GetInitialResourcesAction));

            }else if (player.getID()==2) {
                player.getClientHandler().send(new GetInitialResourcesAction("Please choose 2 resources and their position, first resource: (es." + ConsoleColors.RED + "COIN" + ConsoleColors.RESET + "in shelf " + ConsoleColors.RED + "1" + ConsoleColors.RESET));

                player.getClientHandler().send(new GetInitialResourcesAction("Second resource: (es." +ConsoleColors.RED+ "COIN" +ConsoleColors.RESET+ "in shelf "+ConsoleColors.RED + "1" +ConsoleColors.RESET));
            }*/

        }
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
    public ActionAnswer actionHandler(GameMessage input, int id) {
        //TODO ragiono su inizializzazione
        ActionAnswer result = null;
        //TODO ragiono su oggetti che passa il client
        Object gameObj;
        Object gameObj2;

        if (input instanceof BuyCardAction) {
            gameObj = ((BuyCardAction) input).getCard();
            gameObj2 = ((BuyCardAction) input).getSlot();

            try {
                if (controller.checkBuy((int) gameObj, id, (int) gameObj2)) {
                    result = new ActionAnswer("carta" + gameObj + "comprata");
                }
            } catch (ActionAlreadySetException actionAlreadySetException) {
                    actionAlreadySetException.printStackTrace();
            }catch (InvalidSlotException e){
                //TODO send ask new slot
            } catch (ResourceNotValidException e) {
                e.printStackTrace();
            } catch (CardNotOnTableException e) {
                e.printStackTrace();
            }

        } else if (input instanceof MarketAction) {
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
                actionAlreadySetException.printStackTrace();
            }

        } else if (input instanceof ProductionAction) {
            gameObj = ((ProductionAction) input).getProductions();
            try {
                if (controller.checkProduction((ArrayList<Integer>) gameObj, id)) {
                    result = new ActionAnswer("mercato cambiato con successo (da coordinata: " + gameObj + " )");
                }
            } catch (ActionAlreadySetException actionAlreadySetException) {
                actualPlayers.get(id).getClientHandler().send(new ActionAlreadySet("Action already set for this player"));
            } catch (CardNotOwnedByPlayerOrNotActiveException e) {
                e.printStackTrace();
            } catch (ResourceNotValidException e) {
                e.printStackTrace();
            }

        }else if (input instanceof ActiveLeadAction) {
            gameObj = ((ActiveLeadAction) input).getLead();
            if (controller.checkLeadActivation((String) gameObj, id)) {
                result = new ActionAnswer("lead card attivata: " + gameObj);
            }

        } else if (input instanceof DiscardLeadAction) {
            gameObj = ((DiscardLeadAction) input).getLead();
            if (controller.checkDiscardLead((String) gameObj, id)) {
                result = new ActionAnswer("lead card scartata: " + gameObj);
            }
            //TODO sposta risorse negli scaffali
        }
        return result;
    }
}