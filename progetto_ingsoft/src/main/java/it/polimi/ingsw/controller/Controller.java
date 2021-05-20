package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.DevCard;
import it.polimi.ingsw.model.cards.DevDeckMatrix;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.cards.cardExceptions.CardNotOnTableException;
import it.polimi.ingsw.model.personalboard.Shelf;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.messages.LobbyMessage;
import it.polimi.ingsw.messages.SerializedMessage;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;

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

    public VirtualView startGame() {
        lobby.sendAll((SerializedMessage) new LobbyMessage("The game is starting..."));
        lobby.setStateOfGame(GameState.ONGOING);
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
        actualPlayerTurn=lobby.getPlayers().get(0);
        lobby.sendAll(new LobbyMessage("is the turn of "+server.getNameFromId().get(actualPlayerTurn.getID())));
        return createVirtualView();
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
    }

    public boolean check2Leads(){
        for(Player player:game.getPlayers())
            if(player.getLeadCards().size()!=2)
                return false;
        return true;
    }

    //TODO popeSpace control


    public boolean checkInitialResources() {
        boolean result= true;
        for(int i=1;i<=game.getPlayers().size();i++) {
            if (!game.getPlayers().get(i).getPersonalBoard().getStrongBox().getStrongboxContent().isEmpty())
                game.getPlayers().get(i).getPersonalBoard().getStrongBox().getStrongboxContent().clear();
            if(i==1) {
                if (!game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().isEmpty())
                    game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().clear();
            }else if (i == 2 || i == 3) {
                if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() > 1) {
                    game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().clear();
                    lobby.getPlayers().get(i).getClientHandler().send(new GetInitialResourcesAction("You have more resources than the ones permitted, please resend your initial resources:  "));
                    result = false;
                }else if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() == 0)
                    result= false;
            } else if (i == 4) {
                if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() > 2){
                    game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().clear();
                    lobby.getPlayers().get(i).getClientHandler().send(new GetInitialResourcesAction("You have more resources than the ones permitted, please resend your initial resources:  "));
                    result= false;
                }else if (game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().getResources().size() < 2)
                    result = false;
                }
            if(i==1||i==2){
                if(game.getPlayers().get(i).getPersonalBoard().getFaithMarker().getFaithPosition()>0)
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().reset();
            }
            if (i == 3 || i == 4) {
                if (game.getPlayers().get(i).getPersonalBoard().getFaithMarker().getFaithPosition() == 0)
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().updatePosition();
                else if(game.getPlayers().get(i).getPersonalBoard().getFaithMarker().getFaithPosition()>1) {
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().reset();
                    game.getPlayers().get(i).getPersonalBoard().getFaithMarker().updatePosition();
                }
            }
        }
        return result;
    }

    public boolean checkResourcePosition(int id, int position, Resource resource) throws ResourceNotValidException {
        Player player = game.getPlayers().get(id);
        Shelf shelf= player.getPersonalBoard().getWarehouseDepots().getShelves()[position];
        if(((shelf.isShelfAvailability()) && (resource.equals(shelf.getResourceType()))) || shelf.getSlots().isEmpty()) {
            player.getPersonalBoard().getWarehouseDepots().addinShelf(position, resource);
            return true;
        //TODO eccezione se tutte sono piene
        }else
             throw new ResourceNotValidException("Cannot put the resource in the chosen shelf");
    }

    public boolean check2Leads(int id, int card1, int card2) throws CardChosenNotValidException, LeadsAlreadyChosenException {
        Player player = game.getPlayers().get(id);
        if(player.getLeadCards().size()==2)
            throw new LeadsAlreadyChosenException("This player has already chosen their cards");
        else
        if(player.getLeadCards().contains(card1) && player.getLeadCards().contains(card2) && card1!=card2) {
           return player.choose2Leads(card1, card2);
        }
        else
            throw new CardChosenNotValidException("One or both card chosen are not present in the player's leadCards available");
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
}
