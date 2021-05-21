package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.personalboard.Shelf;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.*;
import it.polimi.ingsw.server.GameState;
import it.polimi.ingsw.messages.*;
import java.util.ArrayList;
import java.util.Optional;

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
        lobby.sendAll(new StartingGameMessage());
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
                    lobby.getPlayers().get(i).getClientHandler().send(new GetInitialResourcesActions("You have more resources than the ones permitted, please resend your initial resources:  "));
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
                    lobby.getPlayers().get(i).getClientHandler().send(new GetInitialResourcesActions("You have more resources than the ones permitted, please resend your initial resources:  "));
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

    //ogni posizione dell'array indica un piano
    public boolean checkPositionOfResourcesAfterReorder(ArrayList<String>[] newdisposition, int id) {

        Player player = game.getPlayers().get(id);

        ArrayList<Resource> allres = new ArrayList<>();

        //me lo trasformo in un array di risorse
        for (ArrayList<String> strings : newdisposition)
            for(String s:strings)
                allres.add(Resource.valueOf(s));

        if (newdisposition.length > 5)
            return false;
        else {
            ArrayList<Resource> warehouseResources=player.getPersonalBoard().getWarehouseDepots().getResources();
            allres.removeIf(warehouseResources::contains);
            }
            if(!allres.isEmpty()) {
                if (player.getPersonalBoard().getSpecialShelves().get(0).isPresent()) {
                    ArrayList<Resource> specialShelfresources = player.getPersonalBoard().getSpecialShelves().get(0).get().getSpecialSlots();
                    if (player.getPersonalBoard().getSpecialShelves().get(1).isPresent())
                        specialShelfresources.addAll(player.getPersonalBoard().getSpecialShelves().get(1).get().getSpecialSlots());
                    for (Resource s : allres) {
                        if (!specialShelfresources.contains(s)) {
                            return false;
                        }
                    }
                    allres.removeIf(specialShelfresources::contains);
                    if(!allres.isEmpty())
                        return false;
                }
            }
            //devo controllare che per ogni scaffale abbia risorse dello stesso tipo
                for(ArrayList<String> stringArrayList:newdisposition)
                    for(int i=0;i<stringArrayList.size()-1;i++)
                        if(!stringArrayList.get(i).equals(stringArrayList.get(i+1)))
                            return false;
            //controllo che quello che metto nello special shelf corrisponda al tipo effettivo accettato. X es ho 3 nel warehouse e 1 nello SS e voglio fare 2 e 2
        return true;
    }

    public boolean checkTakeResFromSupply(String gameObj, int id) {
        Player player = game.getPlayers().get(id);

        return true;
    }

    public void askInitialResources() {
        lobby.setStateOfGame(GameState.PREPARATION2);
        int i=0;
        for(VirtualClient player:lobby.getPlayers()){
            if(i==0)
                player.getClientHandler().send(new LobbyMessage("Wait until other players have chosen initial resources"));
            else if(i==1)
                player.getClientHandler().send(new GetInitialResourcesActions("You can choose 1 initial resource"));
            else if(i==2)
                player.getClientHandler().send(new GetInitialResourcesActions(
                        "You can choose 1 initial resource, you will receive a faith point also"));
            else
                player.getClientHandler().send(new GetInitialResourcesActions(
                        "You can choose 2 initial resources, you will receive a faith point also"));
            i++;
        }
    }

    public void startGame() {
        lobby.setStateOfGame(GameState.ONGOING);
        actualPlayerTurn=lobby.getPlayers().get(0);
        lobby.sendAll(new LobbyMessage("We are ready to start. it's turn of " +
                server.getNameFromId().get(actualPlayerTurn.getID())));
    }
}
