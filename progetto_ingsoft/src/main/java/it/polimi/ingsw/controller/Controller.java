package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Market.Market;
import it.polimi.ingsw.model.personalboard.Shelf;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.*;
import it.polimi.ingsw.server.GameState;
import it.polimi.ingsw.messages.*;

import java.util.*;

public class Controller {

    private MainServer server;
    private final Lobby lobby;
    private Game game;
    private VirtualClient actualPlayerTurn;

    public Controller(Lobby lobby, MainServer server) {
        this.lobby=lobby;
        this.server=server;
    }

    public void createGame() {
        lobby.sendAll(new CreatingGameMessage());
        lobby.setStateOfGame(GameState.PREPARATION1);
        int id;
        if(lobby.getPlayers().size()==1) {
            id = lobby.getPlayers().get(0).getID();
            try {
                System.out.println("creo partita singlePlayer");
                game = new SinglePlayer(server.getNameFromId().get(id));
                System.out.println("partita singlePlayer creata");
            } catch (playerLeadsNotEmptyException e) {
                e.printStackTrace();
            }
        }
        else{
            ArrayList<String> playersName=new ArrayList<>();
            for(VirtualClient player: lobby.getPlayers()) {
                id = player.getID();
                playersName.add(server.getNameFromId().get(id));
            }
            for (String name:playersName)
                System.out.println(name);
            try {
                System.out.println("creo partita multiPlayer");
                game=new MultiPlayer(playersName, lobby.getPlayers().size());
                //TODO creo mappa id-player
                System.out.println("partita multiPlayer creata");
           } catch (playerLeadsNotEmptyException e) {
                e.printStackTrace();
            }
        }
        int i=0;
        for(VirtualClient client: lobby.getPlayers()) {
            ArrayList<Integer> leaderId = new ArrayList<>();
            for(LeadCard card:game.getPlayers().get(i).getLeadCards())
                leaderId.add(card.getId());
            client.getClientHandler().send(new LeaderCardDistribution(leaderId, "Please choose 2 leader card to hold"));
            i++;
        }
        //return createVirtualView();
    }

    //TODO
    private VirtualView createVirtualView() {
        String[][] virtualMarket=game.getMarket().viewMarketBoard();
        int[][] virtualDevCards=new int[4][3];
        int k=0;
        for(int i=0;i<4;i++)
            for(int j=0;j<3;j++) {
                virtualDevCards[i][j] = k;
                k++;
            }
        ArrayList<Integer> virtualFaithPos=new ArrayList<>();
        for(Player player:game.getPlayers())
            virtualFaithPos.add(player.getPersonalBoard().getFaithMarker().getFaithPosition());
        return new VirtualView(virtualMarket,virtualDevCards,virtualFaithPos);
        }//TODO dai le risorse e le 4 carte + scelta 2 carte

    public boolean checkPlayersLeads(){
        System.out.println("controllo se tutti hanno scelto le leads");
        int i=0;
        for(Player player:game.getPlayers()) {
            if (player.getLeadCards().size() != 2) {
                System.out.println("player "+i + " non ha ancora scelto le leads");
                return false;
            }
            i++;
        }
        System.out.println("tutti i giocatori hanno già scelto le leads");
        return true;
    }

    //TODO popeSpace control


    public boolean checkInitialResources() {
        System.out.println("sto controllando se gli altri giocatori hanno scelto le risorse");
        boolean result = true;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if (!game.getPlayers().get(0).getPersonalBoard().getStrongBox().getStrongboxContent().isEmpty()) {
                System.out.println("il giocatore " + i + " aveva lo strongbox pieno");
                game.getPlayers().get(i).getPersonalBoard().getStrongBox().getStrongboxContent().clear();
                System.out.println("strongbox pulita");
            }
            if (i == 0) {
                if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size()>0) {
                    System.out.println("il primo giocatore ha il warehouse pieno");
                    game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().clear();
                    System.out.println("warehouse pulita");
                }
            } else if (i == 1 || i == 2) {
                if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() > 1) {
                    System.out.println("il giocatore " + i + "aveva il warehouse pieno");
                    game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().clear();
                    System.out.println("warehouse pulita, invio nuovamente la richiesta della risorse iniziale");
                    lobby.getPlayers().get(i).getClientHandler().send(new GetInitialResourcesAction("You have more resources than the ones permitted, please resend your initial resources:  "));
                    System.out.println("messaggio inviato");
                    result = false;
                } else if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() == 0) {
                    System.out.println("il giocatore " + i + " deve ancora scegliere la risorsa iniziale");
                    result = false;
                }
            } else if (i == 3) {
                if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() > 2) {
                    System.out.println("il giocatore " + i + "aveva il warehouse pieno");
                    game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().clear();
                    System.out.println("warehouse pulita, invio nuovamente la richiesta della risorse iniziale");
                    lobby.getPlayers().get(i).getClientHandler().send(new GetInitialResourcesAction("You have more resources than the ones permitted, please resend your initial resources:  "));
                    System.out.println("messaggio inviato");
                    result = false;
                } else if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() < 2) {
                    System.out.println("il giocatore " + i + " deve ancora scegliere la risorsa iniziale");
                    result = false;
                }
            }
            if (i == 0 || i == 1) {
                if (game.getPlayers().get(i).getPersonalBoard().getFaithMarker().getFaithPosition() > 0) {
                    System.out.println("il giocatore " + i + " ha punti fede che non dovrebbe avere");
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().reset();
                    System.out.println("punti fede tolti");
                }
            }
            if (i == 2 || i == 3) {
                if (game.getPlayers().get(i).getPersonalBoard().getFaithMarker().getFaithPosition() == 0) {
                    System.out.println("il giocatore " + i + " non ha ancora ricevuto il suo punto iniziale");
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().updatePosition();
                    System.out.println("ho assegnato al giocatore " + i + "il suo punto fede iniziale");
                } else if (game.getPlayers().get(i).getPersonalBoard().getFaithMarker().getFaithPosition() > 1) {
                    System.out.println("il giocatore " + i + " ha punti fede che non dovrebbe avere");
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().reset();
                    System.out.println("punti fede tolti");
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().updatePosition();
                    System.out.println("ho assegnato al giocatore " + i + "il suo punto fede iniziale");
                }
            }
        }
        return result;
    }

    public boolean checkResourcePosition(int id, int position, Resource resource) throws ResourceNotValidException {
        System.out.println("sto controllando se il giocatore può mettere la risorse nello shelf richiesto");
        int playerPosition = lobby.getPlayers().indexOf(server.getClientFromId().get(id));
        Player player = game.getPlayers().get(playerPosition);
        Shelf shelf= player.getPersonalBoard().getWarehouseDepots().getShelves()[position];
        System.out.println("mi sono salvato lo shelf richiesto");
        if(((shelf.isShelfAvailability()) && (resource.equals(shelf.getResourceType()))) || shelf.getSlots().isEmpty()) {
            System.out.println("è possibile inserire la risorsa nello shef");
            player.getPersonalBoard().getWarehouseDepots().addinShelf(position, resource);
            return true;
        //TODO eccezione se tutte sono piene
        }else {
            System.out.println("NON è possibile inserire la risorsa nello shelf richiesto");
            throw new ResourceNotValidException("Cannot put the resource in the chosen shelf");
        }
    }

    public boolean check2Leads(int id, int card1, int card2){
        System.out.println("controllo gli id");
        int playerPosition = lobby.getPlayers().indexOf(server.getClientFromId().get(id));
        Player player = game.getPlayers().get(playerPosition);
        LeadCard firstCard=LeadDeck.getCardFromId().get(card1);
        LeadCard secondCard=LeadDeck.getCardFromId().get(card2);
        if(player.getLeadCards().size()==2) {
            System.out.println("il client " +id+" ha già scelto le carte");
            server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage("You have chosen yours leader cards yet"));
            return false;
        }else if(player.getLeadCards().contains(firstCard) && player.getLeadCards().contains(secondCard) && card1!=card2) {
            System.out.println("gli id scelti vanno bene");
            return  player.choose2Leads(card1, card2);
        }else {
            System.out.println("gli id scelti non vanno bene");
            ArrayList<Integer> leaderId = new ArrayList<>();
            for (LeadCard card : player.getLeadCards())
                leaderId.add(card.getId());
            System.out.println("mi sono salvato gli id delle carte");
            server.getClientFromId().get(id).getClientHandler().send(new LeaderCardDistribution(leaderId,
                    "You choose not valid leader cards "));
            System.out.println("ho inviato il messaggio");
            return false;
        }
    }


    //TODO methods actions
    //TODO creo mappa
    public boolean checkBuy(int card, int id, int position) throws CardNotOnTableException, ResourceNotValidException, InvalidSlotException, ActionAlreadySetException {
        Player player = game.getPlayers().get(id);
        Optional<Action> playerAction= Optional.ofNullable(player.getAction());
        if(playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else if(position<0||position>2)
            throw new InvalidSlotException();
        else {
            DevCard[][] upper = DevDeckMatrix.getUpperDevCardsOnTable();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    if (upper[i][j].getId() == card) {
                        DevCard cardToBuy = game.getDevDeck().getCardFromId(card);
                        if (player.getPersonalBoard().checkUseProd(cardToBuy.getRequirements())) {
                            player.setAction(Action.BUYCARD);
                            DevDeckMatrix.buyCard(cardToBuy);
                            player.getPersonalBoard().removeResources(cardToBuy.getRequirements());
                            player.getPersonalBoard().getDevCardSlot().overlap(cardToBuy, position);
                            return true;
                        } else
                            //TODO da resource
                            throw new ResourceNotValidException("The player does not have enough resources to go through with the action");
                    }
                }
            } throw new CardNotOnTableException("Error: card not found on table");
        }
    }

    public boolean checkMarket(int gameObj, int id) throws NotAcceptableSelectorException, FullSupplyException, ActionAlreadySetException {
        Player player = game.getPlayers().get(id);
        Optional<Action> playerAction= Optional.ofNullable(player.getAction());
        if(playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        if(gameObj <0||gameObj>6)
            throw new NotAcceptableSelectorException("Selector out of range : "+ gameObj);
        else {
            //TODO check fullSupplyException
            game.getMarket().buyResources(gameObj, player);
            return true;
        }
    }

    public boolean checkProduction(ArrayList<Integer> gameObj, int id) throws ActionAlreadySetException, ResourceNotValidException, CardNotOwnedByPlayerOrNotActiveException {
        //TODO cosa manda client, produzione personale e leader da controllare
        Player player = game.getPlayers().get(id);
        Optional<Action> playerAction= Optional.ofNullable(player.getAction());
        if(playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else{
            ArrayList<Resource> totalProdIn= new ArrayList<>();
            boolean found;
            ArrayList<DevCard> prodCards=new ArrayList<>();
            for(Integer idCard:gameObj) {
                found = false;
                DevCard card = game.getDevDeck().getCardFromId(idCard);
                prodCards.add(card);
                for (DevCard playerCard : player.getPersonalBoard().getDevCardSlot().getActiveCards()) {
                    if (playerCard.equals(card)) {
                        found = true;
                        totalProdIn.addAll(card.getProdIn());
                    }
                } if (!found)
                    throw new CardNotOwnedByPlayerOrNotActiveException("The card with id: "+idCard + " is not owned by the player or it is not active");
            }
            if(player.getPersonalBoard().checkUseProd(totalProdIn)) {
                player.setAction(Action.ACTIVATEPRODUCTION);
                for (DevCard card : prodCards)
                    player.getPersonalBoard().getStrongBox().addInStrongbox(card.getProdOut());
            }else
                throw new ResourceNotValidException("The player does not have enough resources to abilitate the productions");
        }
        return true;
    }

    public boolean checkLeadActivation(String gameObj, int id) {
        Player player = game.getPlayers().get(id);

        return true;
    }

    public boolean checkDiscardLead(String gameObj, int id) {
        Player player = game.getPlayers().get(id);

        return true;
    }

    public boolean checkPositionOfResources(String gameObj, int id) {
        Player player = game.getPlayers().get(id);

        return true;
    }

    public boolean checkTakeResFromSupply(String gameObj, int id) {
        Player player = game.getPlayers().get(id);

        return true;
    }

    public void askInitialResources() {
        if(game.getPlayers().size()>1) {
            lobby.setStateOfGame(GameState.PREPARATION2);
            lobby.getPlayers().get(0).getClientHandler().send(new LobbyMessage("Wait until other players have chosen initial resources"));
            lobby.getPlayers().get(1).getClientHandler().send(new GetInitialResourcesAction("You can choose 1 initial resource"));
            lobby.getPlayers().get(2).getClientHandler().send(new GetInitialResourcesAction(
                    "You can choose 1 initial resource, you will receive a faith point also"));
            lobby.getPlayers().get(3).getClientHandler().send(new GetInitialResourcesAction(
                    "You can choose 2 initial resources, you will receive a faith point also"));
        }else
            startGame();
        }

    public void startGame() {
        lobby.setStateOfGame(GameState.ONGOING);
        actualPlayerTurn=lobby.getPlayers().get(0);
        System.out.println("sto creando il startingGameMessage");
        Market market = game.getMarket();
        String[][] simplifiedMarket =new String[3][4];
        for(int j=0;j<3;j++) {
            for (int k = 0; k < 4; k++) {
                simplifiedMarket[j][k] = market.getMarketBoard()[j][k].getColor();
            }
        }
        System.out.println("market salvato");
        int[][] devMatrix=new int[4][3];
        DevCard[][] matrix =DevDeckMatrix.getUpperDevCardsOnTable();
        System.out.println("mi sono salvato le carte acquistabili");
        for(int j=0;j<4;j++) {
            for (int k = 0; k < 3; k++) {
                devMatrix[j][k] = matrix[j][k].getId();
            }
        }
        System.out.println("devMatrix salvata");
        int i=0;
        for(VirtualClient client:lobby.getPlayers()){
            Player p=game.getPlayers().get(i);
            Map<Integer,Boolean> cardsId = new HashMap<>();
            p.getLeadCards().forEach(leadCard -> cardsId.put(leadCard.getId(),leadCard.isActive()));
            p.getPersonalBoard().getDevCardSlot().getDevCards().forEach(devCard -> cardsId.put(devCard.getId(),devCard.isActive()));
            System.out.println("card di " + p.getName() + " salvate");
            ArrayList<String>[] warehouse = new ArrayList[3];
            for(ArrayList<String> shelf:warehouse)
                shelf=new ArrayList<>();
            //Arrays.stream(warehouse).forEach(strings -> new ArrayList<>());
            p.getPersonalBoard().getWarehouseDepots().getShelves()[0].getSlots().forEach(resource -> warehouse[0].add(String.valueOf(resource)));
            p.getPersonalBoard().getWarehouseDepots().getShelves()[1].getSlots().forEach(resource -> warehouse[1].add(String.valueOf(resource)));
            p.getPersonalBoard().getWarehouseDepots().getShelves()[2].getSlots().forEach(resource -> warehouse[2].add(String.valueOf(resource)));
            System.out.println("warehouse di " + p.getName() + " salvato");
            int faithPosition = p.getPersonalBoard().getFaithMarker().getFaithPosition();
            System.out.println("messaggio costruito per " + p.getName());
            client.getClientHandler().send(new StartingGameMessage(cardsId,warehouse,faithPosition, simplifiedMarket,devMatrix,"We are ready to start. it's turn of " +
                    server.getNameFromId().get(actualPlayerTurn.getID())));
            System.out.println("messaggio inviato al player "+i);
            i++;
        }

    }

    public VirtualClient getActualPlayerTurn() {
        return actualPlayerTurn;
    }

    public void turnUpdate() {
        int id=actualPlayerTurn.getID();
        int actualIndex=0;
        for(Player player:game.getPlayers()){
            if (server.getNameFromId().get(id).equals(player.getName())){
                actualIndex=game.getPlayers().indexOf(player);
                break;
            }
        }
        actualIndex=(actualIndex+1)/(game.getPlayers().size());
        String name=game.getPlayers().get(actualIndex).getName();
        id=server.getIDFromName().get(name);
        actualPlayerTurn=server.getClientFromId().get(id);
        lobby.sendAll(new LobbyMessage("è il turno di " +server.getNameFromId().get(actualPlayerTurn.getID())));
    }

    public void insertPlayerInOrder(int id, String name) {
        for(Player player:game.getPlayers()) {
            if (name.equals(player.getName())) {
                lobby.getPlayers().add(game.getPlayers().indexOf(player),server.getClientFromId().get(id));
                return;
            }
        }
    }

    public void sendInfoOfgame(int id, String name) {
        if(lobby.getStateOfGame()==GameState.WAITING){
            server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage("Welcome back. " +
                    "We are waiting for other " + lobby.getSeatsAvailable() + " players"));
        }else if(lobby.getStateOfGame()==GameState.PREPARATION1) {
            for (Player p : game.getPlayers()) {
                if (p.getName().equals(name)) {
                    ArrayList<Integer> cardsId = new ArrayList<>();
                    p.getLeadCards().forEach(leadCard -> cardsId.add(leadCard.getId()));
                    if (cardsId.size() > 2)
                        server.getClientFromId().get(id).getClientHandler().send(new LeaderCardDistribution(cardsId,
                                "Please choose 2 leader card to hold"));
                    else
                        server.getClientFromId().get(id).getClientHandler().send(new LeaderCardDistribution(cardsId,
                                "You have chosen this cards"));
                }
            }
        }else if(lobby.getStateOfGame()==GameState.PREPARATION2){
            for (Player p : game.getPlayers()) {
                if (p.getName().equals(name)) {
                    int index = game.getPlayers().indexOf(p);
                    if (index == 0)
                        lobby.getPlayers().get(0).getClientHandler().send(new LobbyMessage("Wait until other players have chosen initial resources"));
                    else if (index == 1)
                        lobby.getPlayers().get(1).getClientHandler().send(new GetInitialResourcesAction("You can choose 1 initial resource"));
                    else if (index == 2)
                        lobby.getPlayers().get(2).getClientHandler().send(new GetInitialResourcesAction(
                                "You can choose 1 initial resource, you will receive a faith point also"));
                    else
                        lobby.getPlayers().get(3).getClientHandler().send(new GetInitialResourcesAction(
                                "You can choose 2 initial resources, you will receive a faith point also"));
                }
            }
        }
        else if(lobby.getStateOfGame()==GameState.ONGOING) {
            Market market = game.getMarket();
            String[][] simplifiedMarket = new String[3][4];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 4; k++) {
                    simplifiedMarket[j][k] = market.getMarketBoard()[j][k].getColor();
                }
            }
            System.out.println("market salvato");
            int[][] devMatrix = new int[4][3];
            DevCard[][] matrix = DevDeckMatrix.getUpperDevCardsOnTable();
            System.out.println("mi sono salvato le carte acquistabili");
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 3; k++) {
                    devMatrix[j][k] = matrix[j][k].getId();
                }
            }
            System.out.println("devMatrix salvata");
            for (Player p : game.getPlayers()) {
                if (p.getName().equals(name)) {
                    Map<Integer,Boolean> cardsId = new HashMap<>();
                    p.getLeadCards().forEach(leadCard -> cardsId.put(leadCard.getId(),leadCard.isActive()));
                    p.getPersonalBoard().getDevCardSlot().getDevCards().forEach(devCard -> cardsId.put(devCard.getId(),devCard.isActive()));
                    System.out.println("card di " + name + " salvate");
                    ArrayList<String>[] warehouse = new ArrayList[3];
                    Arrays.stream(warehouse).forEach(strings -> new ArrayList<>());
                    p.getPersonalBoard().getWarehouseDepots().getShelves()[0].getSlots().forEach(resource -> warehouse[0].add(String.valueOf(resource)));
                    p.getPersonalBoard().getWarehouseDepots().getShelves()[1].getSlots().forEach(resource -> warehouse[1].add(String.valueOf(resource)));
                    p.getPersonalBoard().getWarehouseDepots().getShelves()[2].getSlots().forEach(resource -> warehouse[2].add(String.valueOf(resource)));
                    System.out.println("warehouse di " + name + " salvato");
                    int faithPosition = p.getPersonalBoard().getFaithMarker().getFaithPosition();
                    System.out.println("messaggio costruito per " + name);
                    server.getClientFromId().get(id).getClientHandler().send(new ReconnectionMessage(cardsId, warehouse, faithPosition, simplifiedMarket, devMatrix));
                }
            }
        }
    }


}
