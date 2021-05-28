package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.ResourceInSupplyRequest;
import it.polimi.ingsw.messages.answerMessages.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Market.*;
import it.polimi.ingsw.model.personalboard.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.*;
import it.polimi.ingsw.messages.*;
import java.util.*;
import java.util.stream.Collectors;

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
        lobby.sendAll(new LobbyMessage("The game is starting"));
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
    /*private VirtualView createVirtualView() {
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
     */

    public boolean checkAllPlayersChooseLeads(){
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

    public boolean check2Leads(int id, int card1, int card2){
        System.out.println("controllo gli id");
        int playerPosition = lobby.getPlayers().indexOf(server.getClientFromId().get(id));
        Player player = game.getPlayers().get(playerPosition);
        if(player.getLeadCards().size()==2) {
            System.out.println("il client " +id+" ha già scelto le carte");
            server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage("You have chosen yours leader cards yet"));
            return false;
        }else if(player.getLeadCards().stream().anyMatch(leadCard -> leadCard.getId()==card1) &&
                player.getLeadCards().stream().anyMatch(leadCard -> leadCard.getId()==card1) && card1!=card2) {
            System.out.println("gli id scelti vanno bene");
            return  player.choose2Leads(card1, card2);
        }else {
            System.out.println("gli id scelti non vanno bene");
            ArrayList<Integer> leaderId = new ArrayList<>();
            player.getLeadCards().forEach(leadCard -> leaderId.add(leadCard.getId()));
            System.out.println("mi sono salvato gli id delle carte");
            server.getClientFromId().get(id).getClientHandler().send(new LeaderCardDistribution(leaderId,
                    "You choose not valid leader cards "));
            System.out.println("ho inviato il messaggio");
            return false;
        }
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

    public boolean checkInsertResourcePosition(int id, int position, Resource resource) throws ResourceNotValidException {
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

    //TODO methods actions
    //TODO creo mappa
    public boolean checkBuy(int card, int id, int position) throws CardNotOnTableException, ResourceNotValidException, InvalidSlotException, ActionAlreadySetException {
        System.out.println("controllo se può comprare la carta");
        String name=getActualPlayerTurn().getNickName();
        Player player=game.getPlayers().get(0);
        for(Player p:game.getPlayers()){
            if (p.getName().equals(name))
                player=p;
        }
        Optional<Action> playerAction= Optional.ofNullable(player.getAction());
        if(playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else if(position<0||position>2)
            throw new InvalidSlotException();
        else {
            System.out.println("ho controllato l'azione e la posizione scelta");
            DevCard[][] upper;
            if(playerCardLevel(player, card)) {
                upper = DevDeckMatrix.getUpperDevCardsOnTable();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        System.out.println(upper[i][j].getId());
                    }
                }
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (upper[i][j].getId() == card) {
                            System.out.println("ho trovato la carta");
                            DevCard cardToBuy = game.getDevDeck().getCardFromId(card);
                            System.out.println("mi sono preso la carta");
                            if (player.getPersonalBoard().removeResourcesfromBuy(cardToBuy.getRequirements())) {
                                System.out.println("ha le risorse necessarie");
                                player.setAction(Action.BUYCARD);
                                DevDeckMatrix.buyCard(cardToBuy);
                                System.out.println("ha comprato la carta, invio la nuova dev matrix");
                                lobby.sendAll(new DevMatrixChangeMessage(getDevMatrix()));
                                System.out.println("dev matrix inviata");
                                player.getPersonalBoard().removeResources(cardToBuy.getRequirements());
                                getHandlerFromPlayer(id).send(new WareHouseChangeMessage(getSimplifiedWarehouse(player)));
                                getHandlerFromPlayer(id).send(new StrongboxChangeMessage(getSimplifiedStrongbox(player)));
                                System.out.println("ho rimosso le risorse usate");
                                player.getPersonalBoard().getDevCardSlot().overlap(cardToBuy, position);
                                System.out.println("ho posizionato la carta");
                                getHandlerFromPlayer(id).send(new CardIDChangeMessage(getCardsId(player)));
                                System.out.println("messaggio delle carte inviato");
                                return true;
                            } else
                                throw new ResourceNotValidException("The player does not have enough resources to go through with the action");
                        }
                    }
                }
            }
                throw new CardNotOnTableException("Error: card not found on table");
        }
    }

    private boolean playerCardLevel(Player player, int card) {
        DevCard devCard=game.getDevDeck().getCardFromId(card);
        int level=devCard.getLevel();
        if(level==1) {
            if (player.getPersonalBoard().getDevCardSlot().getActiveCards().size() < 3)
                return true;
        }else {
                for (DevCard card1 : player.getPersonalBoard().getDevCardSlot().getActiveCards())
                    if (card1.getLevel() == level - 1)
                        return true;
       }
        return false;
    }

    public void checkMarket(int gameObj, int id) throws NotAcceptableSelectorException, FullSupplyException, ActionAlreadySetException {
        String name=getActualPlayerTurn().getNickName();
        Player player=game.getPlayers().get(0);
        for(Player p:game.getPlayers()){
            if (p.getName().equals(name))
                player=p;
        }
        System.out.println("controllo se il giocatore ha già eseguito un'azione principale");
        Optional<Action> playerAction= Optional.ofNullable(player.getAction());
        if(playerAction.isPresent()){
            System.out.println("azione principale già eseguita");
            getHandlerFromPlayer(id).send(new LobbyMessage("The player has already gone through with an action in their turn"));
        }
        else if(gameObj <0||gameObj>6) {
            System.out.println("Coordinata non valida");
            getHandlerFromPlayer(id).send(new LobbyMessage("Selector out of range : " + gameObj));
        }
        else {
            //TODO check fullSupplyException
            System.out.println("azione eseguibile");
            player.setAction(Action.TAKEFROMMARKET);
            game.getMarket().buyResources(gameObj, player);
            String[][] simplifiedMarket = getSimplifiedMarket();
            lobby.sendAll(new MarketChangeMessage(simplifiedMarket));
            System.out.println("risorse messe nel supply");
            ArrayList<Resource> resSupply = player.getResourceSupply().getResources();
            ArrayList<String> supply= (ArrayList<String>) resSupply.stream().map(resource-> Objects.toString(resource, null)).collect(Collectors.toList());
            System.out.println("invio la richiesta di sistemare le risorse");
            getHandlerFromPlayer(id).send(new ResourceInSupplyRequest(supply));
        }
    }

    public boolean checkProduction(ArrayList<Integer> cardProd , ArrayList<String> personalProdIn, String personalProdOut, ArrayList<String> leadProdOut, int id) throws ActionAlreadySetException, ResourceNotValidException, CardNotOwnedByPlayerOrNotActiveException {
        //TODO cosa manda client, produzione personale e leader da controllare
        String name=getActualPlayerTurn().getNickName();
        Player player=game.getPlayers().get(0);
        for(Player p:game.getPlayers()){
            if (p.getName().equals(name))
                player=p;
        }
        ArrayList<Resource> resourceArrayList=player.getPersonalBoard().getStrongBox().getStrongboxContent();
        resourceArrayList.addAll(player.getPersonalBoard().getWarehouseDepots().getResources());
        if(!player.getPersonalBoard().getSpecialShelves().isEmpty()) {
            resourceArrayList.addAll(player.getPersonalBoard().getSpecialShelves().get(0).get().getSpecialSlots());
            resourceArrayList.addAll(player.getPersonalBoard().getSpecialShelves().get(1).get().getSpecialSlots());
        }

        Optional<Action> playerAction = Optional.ofNullable(player.getAction());
        if (playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else if (checkOwnerCards(cardProd,player)) {
            ArrayList<Resource> totalProdIn = takeAllProdIn(cardProd, stringArrayToResArray(personalProdIn), player);
            ArrayList<Resource> totalProdOut;
            if(checkResourcePlayer(totalProdIn, player)) {
                player.setAction(Action.ACTIVATEPRODUCTION);
                totalProdOut = takeAllProdOut(cardProd, stringArrayToResArray(personalProdIn), personalProdOut, leadProdOut, id);
                player.getPersonalBoard().getStrongBox().addInStrongbox(totalProdOut);
                getHandlerFromPlayer(id).send(new StrongboxChangeMessage(getSimplifiedStrongbox(player)));
                getHandlerFromPlayer(id).send(new WareHouseChangeMessage(getSimplifiedWarehouse(player)));
                return true;
            }

        }

        return false;
    }


    private boolean checkResourcePlayer(ArrayList<Resource> totalProdIn, Player player) {
       player.getPersonalBoard().removeResources(totalProdIn);
            return true;
    }

    private ArrayList<Resource> takeAllProdIn(ArrayList<Integer> cardProd ,ArrayList<Resource> personalProdIn, Player player) {
        ArrayList<Resource> totalProdIn = new ArrayList<>();
        ArrayList<DevCard> prodDevs = new ArrayList<>();
        ArrayList<LeadCard> prodLeads = new ArrayList<>();
        cardProd.stream().filter(integer -> integer > 0 && integer < 49).forEach(integer -> {
            DevCard dev = game.getDevDeck().getCardFromId(integer);
            prodDevs.add(dev);
        });
        cardProd.stream().filter(integer -> integer > 48 && integer < 65).forEach(integer -> {
            LeadCard lead = player.getCardFromId(integer);
            prodLeads.add(lead);
        });
        if (!prodDevs.isEmpty())
            prodDevs.forEach(card -> {
                ArrayList<Resource> prodIn = card.getProdIn();
                totalProdIn.addAll(prodIn);
            });
        if (!prodLeads.isEmpty())
            prodLeads.forEach(card -> {
                Resource prodIn = card.getAbility().getAbilityResource();
                totalProdIn.add(prodIn);
            });
        if (!personalProdIn.isEmpty())
            totalProdIn.addAll(personalProdIn);

       return totalProdIn;
    }

    private ArrayList<Resource> takeAllProdOut(ArrayList<Integer> cardProd ,ArrayList<Resource> personalProdIn, String personalProdOut,ArrayList<String> leadProdOut, int id) {
        ArrayList<Resource> totalProdOut = new ArrayList<>();
        ArrayList<DevCard> prodDevs = new ArrayList<>();
        cardProd.stream().filter(integer -> integer > 0 && integer < 49).forEach(integer -> {
            DevCard dev = game.getDevDeck().getCardFromId(integer);
            prodDevs.add(dev);
        });
        prodDevs.forEach(card -> {
                ArrayList<Resource> prodOut = card.getProdOut();
                totalProdOut.addAll(prodOut);
            });
        int numofLead=cardProd.stream().filter(integer -> integer > 48).collect(Collectors.toList()).size();
        if(numofLead==leadProdOut.size()){
            leadProdOut.forEach(resource -> totalProdOut.add(Resource.valueOf(resource)));
            game.getPlayers().get(id).getPersonalBoard().getFaithMarker().updatePosition();
        }else
            getHandlerFromPlayer(id).send(new LobbyMessage("Prod Out of the LeadCard requested missing"));
        if(!personalProdIn.isEmpty())
            if(personalProdOut!=null)
                totalProdOut.add(Resource.valueOf(personalProdOut));
            else
                getHandlerFromPlayer(id).send(new LobbyMessage("Prod Out of the personal production requested missing"));

        return totalProdOut;
    }

    private boolean checkOwnerCards(ArrayList<Integer> cardsId,Player player){
        ArrayList<Integer> playerCards= new ArrayList<>();

        player.getPersonalBoard().getDevCardSlot().getDevCards().stream().forEach(card->{int id=card.getId();playerCards.add(id);});
        player.getLeadCards().stream().forEach(card->{int id=card.getId();
            if(card.isActive()&&(card.getAbility() instanceof LeadAbilityProduction))
                    playerCards.add(id);});

        if(playerCards.containsAll(cardsId))
            return true;
        else
            return false;



    }

    public boolean checkLeadActivation(int gameObj, int id) {
        Player player = game.getPlayers().get(id);
        LeadCard card;
        String username = player.getName();
        boolean result= false;
        if (gameObj < 48 || gameObj > 64) {
            getHandlerFromPlayer(username).send(new LobbyMessage("LeadCard ID not valid"));
        } else {
            card = LeadDeck.getCardFromId(gameObj);
            if (!player.getLeadCards().contains(card)) {
                getHandlerFromPlayer(username).send(new LobbyMessage("You do not own the leadCard chosen"));
            } else if (card.isActive())
                getHandlerFromPlayer(username).send(new LobbyMessage("This leadCard is already active"));
            else {
                result= player.activateAbility(card);
                //
            }
        }
        return result;
    }

    public boolean checkDiscardLead(int gameObj, int id) {
        Player player = game.getPlayers().get(id);
        LeadCard card;
        String username = player.getName();
        boolean result= false;
        if (gameObj < 48 || gameObj > 64) {
            getHandlerFromPlayer(username).send(new LobbyMessage("LeadCard ID not valid"));
        } else {
            card = LeadDeck.getCardFromId(gameObj);
            if (!player.getLeadCards().contains(card)) {
                getHandlerFromPlayer(username).send(new LobbyMessage("You do not own the leadCard chosen"));
            } else if (card.isActive())
                getHandlerFromPlayer(username).send(new LobbyMessage("This leadCard is already active"));
            else {
                result= player.discardLead(card);
            }
        }
        return result;
    }

    //ogni posizione dell'array indica un piano
    //TODO anche con special shelf

    private ArrayList<Resource> stringArrayToResArray(ArrayList<String> gameObj){
        ArrayList<Resource> allRes = new ArrayList<>();
        //me lo trasformo in un array di risorse
        gameObj.forEach(res->allRes.add(Resource.valueOf(res)));
        return allRes;
    }

    private boolean checkSpecialShelf(ArrayList<Resource> specialRes, int id) {
        Player player = game.getPlayers().get(id);
        boolean result= false;
        if (specialRes.size() > 2) {
            getHandlerFromPlayer(id).send(new LobbyMessage("Too many resources for the shelf"));

        } else if (!specialRes.get(0).equals(specialRes.get(1))){
            getHandlerFromPlayer(id).send(new LobbyMessage("Two different resources cannot be in the same special shelf"));

        }else {
            SpecialShelf shelf;
            for (int i = 0; i < 2; i++) {
                if (player.getPersonalBoard().getSpecialShelves().get(i).isPresent()) {
                    shelf = player.getPersonalBoard().getSpecialShelves().get(i).get();
                    if (shelf.getResourceType().equals(specialRes.get(0))) {
                        shelf.getSpecialSlots().clear();
                        shelf.getSpecialSlots().addAll(specialRes);
                        result = true;
                    }
                }else if(i==0) {
                    getHandlerFromPlayer(id).send(new LobbyMessage("The shelves are not active"));
                }else if(!result){
                    getHandlerFromPlayer(id).send(new LobbyMessage("The shelf requested is not active"));
                }
            }
        }

        return result;
    }


    public void checkPositionOfResources(ArrayList<String>[] gameObj, int id){
        String name=getActualPlayerTurn().getNickName();
        Player player=game.getPlayers().get(0);
        for(Player p:game.getPlayers()){
            if (p.getName().equals(name))
                player=p;
        }
        System.out.println("mi sono salvato il player");

        if (gameObj.length <= 5){
            System.out.println("DEBUG 1");
            ResourceSupply supply= player.getResourceSupply();
            ArrayList<Resource> newRes= new ArrayList<>();

            for (ArrayList<String> strings : gameObj) newRes.addAll(stringArrayToResArray(strings));

            ArrayList<Resource> allResources=player.getPersonalBoard().getWarehouseDepots().getResources();

            if(!supply.getResources().isEmpty())
                allResources.addAll(supply.getResources());
            System.out.println("DEBUG 2");
            if(!player.getPersonalBoard().getSpecialShelves().isEmpty()) {
                for (int i = 0; i < 2; i++)
                    if (player.getPersonalBoard().getSpecialShelves().get(i).isPresent())
                        allResources.addAll(player.getPersonalBoard().getSpecialShelves().get(i).get().getSpecialSlots());
            }
            System.out.println("DEBUG 3");
            allResources.removeIf(newRes::contains);

            if(!allResources.isEmpty())
                if(!supply.getResources().isEmpty()) {
                    int num = 0;
                    for (Resource res : allResources)
                        if (supply.getResources().contains(res)) {
                            supply.getResources().remove(res);
                            num++;
                        }
                    if(supply.getResources().isEmpty())
                        for (int i = 0; i < game.getPlayers().size(); i++)
                            if (game.getPlayers().get(i) != player) {
                                game.getPlayers().get(i).getPersonalBoard().getFaithMarker().updatePosition();
                                lobby.getPlayers().get(i).getClientHandler().send(new LobbyMessage(player.getName() + " has discard " + num + "resources, you gained " + num + "faithpoints"));
                            }
                            else {
                                getHandlerFromPlayer(id).send(new LobbyMessage("Resources chosen out of bound, please verify you put the right resources in your board"));
                            }
                }else{
                    getHandlerFromPlayer(id).send(new LobbyMessage("Resources chosen out of bound, please verify you put the right resources in your board"));
                }

            else{
                if(checkShelfContent(gameObj,id)){
                    for(int i=0;i<3;i++) {
                        if(gameObj[i].isEmpty()) {
                            System.out.println("DEBUG 3.1");
                            player.getPersonalBoard().getWarehouseDepots().getShelves()[i] = new Shelf(i + 1);
                            System.out.println("DEBUG 3.2");
                        }else
                            player.getPersonalBoard().getWarehouseDepots().addinShelf(i, stringArrayToResArray(gameObj[i]));
                    }
                    if(!player.getPersonalBoard().getSpecialShelves().isEmpty()){
                        for(int i=3;i<5;i++) {
                            Resource resource=player.getPersonalBoard().getSpecialShelves().get(i-3).get().getResourceType();
                            player.getPersonalBoard().getSpecialShelves().remove(i-3);
                            System.out.println("DEBUG 3.3");
                            player.getPersonalBoard().getSpecialShelves().add(i-3, Optional.of(new SpecialShelf(resource)));
                            if(!gameObj[i].isEmpty()) {
                                System.out.println("DEBUG 3.4");
                                player.getPersonalBoard().getWarehouseDepots().addinShelf(i, stringArrayToResArray(gameObj[i]));
                                System.out.println("DEBUG 3.5");
                            }
                        }
                    }
                    System.out.println("DEBUG 3.6");
                    getHandlerFromPlayer(id).send( new WareHouseChangeMessage(getSimplifiedWarehouse(player)));
                    System.out.println("DEBUG 3.7");
                }else{
                    getHandlerFromPlayer(id).send(new LobbyMessage("Resources not valid in this disposition, please retry"));
                }
            }
        }
        System.out.println("DEBUG 4");
    }


/*
    public void discardResourcesManager(ArrayList<Resource> discardedResources, Player player, int id) {
        player.getResourceSupply().discardResources(discardedResources);
            game.getPlayers().forEach(p->{if (!p.equals(player)) p.getPersonalBoard().getFaithMarker().updatePosition();});
        for(Player p:game.getPlayers()) {
            if (!p.equals(player)) {
                for (Resource res : discardedResources) {
                    p.getPersonalBoard().getFaithMarker().updatePosition();
                }
                int pos=p.getPersonalBoard().getFaithMarker().getFaithPosition();
                server.getClientFromId().get(server.getIDFromName().get(p.getName())).getClientHandler().send((SerializedMessage) new PersonalBoardChangeMessage(pos));
            }
        }
    }
*/

    private boolean checkShelfContent(ArrayList<String>[] gameObj, int id) {
        System.out.println("controllo la disposione scelta");
        //ciclo su ogni mensola, la i corrisponde alla mesola da alto al basso
        for(int i=0; i<3;i++) {
            if (gameObj[i].size() <= i + 1) {
                for (int j = 0; j < gameObj[i].size() - 1; j++)
                    if (!gameObj[i].get(j).equals(gameObj[i].get(j + 1))) {
                        System.out.println("risorse diverse su uno stesso scaffale");
                        return false;
                    }
            } else {
                System.out.println("struttura non mantenuta");
                return false;
            }
        }
        System.out.println("struttura mantenuta, controllo gli special shelf");
        if(!gameObj[3].isEmpty()) {
            System.out.println("controllo primo special shelf");
            if (!checkSpecialShelf(stringArrayToResArray(gameObj[3]), id))
                return false;
        }
        if(!gameObj[4].isEmpty()){
                System.out.println("controllo secondo special shelf");
                if (!checkSpecialShelf(stringArrayToResArray(gameObj[4]), id))
                    return false;
            }
        System.out.println("special shelf vuoti");
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

    private ClientHandler getHandlerFromPlayer(String name){
        int id = server.getIDFromName().get(name);
        return server.getClientFromId().get(id).getClientHandler();
    }

    private ClientHandler getHandlerFromPlayer(int id){
        return server.getClientFromId().get(id).getClientHandler();
    }

    public void startGame() {
        lobby.setStateOfGame(GameState.ONGOING);
        actualPlayerTurn=lobby.getPlayers().get(0);
        System.out.println("sto creando il startingGameMessage");
        String[][] simplifiedMarket =getSimplifiedMarket();
        System.out.println("market salvato");
        int[][] devMatrix=getDevMatrix();
        System.out.println("devMatrix salvata");
        for(Player p:game.getPlayers()){
            String name=p.getName();
            System.out.println(name);
            ArrayList<String>[] warehouse = getSimplifiedWarehouse(p);

            Map<Integer,Boolean> cardsId =getCardsId(p);
            System.out.println("warehouse di " + name + " salvato");
            int faithPosition = p.getPersonalBoard().getFaithMarker().getFaithPosition();
            System.out.println("messaggio costruito per " + name);
            getHandlerFromPlayer(name).send(new StartingGameMessage(cardsId,warehouse,faithPosition, simplifiedMarket,devMatrix,"We are ready to start. it's turn of " +
                    server.getNameFromId().get(actualPlayerTurn.getID()), getSimplifiedStrongbox(p)));
            System.out.println("messaggio inviato al player "+name);
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
        actualIndex=(actualIndex+1)%(game.getPlayers().size());
        String name=game.getPlayers().get(actualIndex).getName();
        id=server.getIDFromName().get(name);
        actualPlayerTurn=server.getClientFromId().get(id);
        String s =game.draw();
        if(s.isEmpty())
            lobby.sendAll(new LobbyMessage("è il turno di " +server.getNameFromId().get(actualPlayerTurn.getID())));
        else if(s.equalsIgnoreCase("finished")) {
            //TODO gestione fine gioco
        }
        else
            lobby.sendAll(new LobbyMessage(s+", è di nuovo il tuo turno"));
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
            String[][] simplifiedMarket = getSimplifiedMarket();
            System.out.println("market salvato");
            int[][] devMatrix=getDevMatrix();
            System.out.println("devMatrix salvata");
            for (Player p : game.getPlayers()) {
                if (p.getName().equals(name)) {
                    ArrayList<String>[] warehouse = getSimplifiedWarehouse(p);
                    Map<Integer,Boolean> cardsId = getCardsId(p);
                    System.out.println("warehouse di " + name + " salvato");
                    int faithPosition = p.getPersonalBoard().getFaithMarker().getFaithPosition();
                    System.out.println("messaggio costruito per " + name);
                    server.getClientFromId().get(id).getClientHandler().send(new ReconnectionMessage(cardsId, warehouse, faithPosition, simplifiedMarket, devMatrix, getSimplifiedStrongbox(p)));
                }
            }
        }
    }

    private int[] getSimplifiedStrongbox(Player p){
        int[] strongbox=new int[4];
        ArrayList<Resource> resources=p.getPersonalBoard().getStrongBox().getStrongboxContent();
        for (Resource resource:resources) {
            switch (resource) {
                case COIN:
                    strongbox[0]++;
                    break;
                case SERVANT:
                    strongbox[1]++;
                    break;
                case SHIELD:
                    strongbox[2]++;
                    break;
                case STONE:
                    strongbox[3]++;
                    break;
            }
        }
        return strongbox;
    }

    private String[][] getSimplifiedMarket() {
        Market market = game.getMarket();
        String[][] simplifiedMarket = new String[3][4];
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 4; k++) {
                simplifiedMarket[j][k] = market.getMarketBoard()[j][k].getColor();
            }
        }
        return simplifiedMarket;
    }

    private  int[][] getDevMatrix(){
        int[][] devMatrix = new int[4][3];
        DevCard[][] matrix = DevDeckMatrix.getUpperDevCardsOnTable();
        System.out.println("mi sono salvato le carte acquistabili");
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 3; k++) {
                devMatrix[j][k] = matrix[j][k].getId();
            }
        }
        return devMatrix;
    }

    private ArrayList<String>[] getSimplifiedWarehouse(Player p) {
        ArrayList<String>[] warehouse = new ArrayList[5];
        warehouse[0]=new ArrayList<>();
        warehouse[1]=new ArrayList<>();
        warehouse[2]=new ArrayList<>();
        warehouse[3]=new ArrayList<>();
        warehouse[4]=new ArrayList<>();
        p.getPersonalBoard().getWarehouseDepots().getShelves()[0].getSlots().forEach(resource -> warehouse[0].add(String.valueOf(resource)));
        p.getPersonalBoard().getWarehouseDepots().getShelves()[1].getSlots().forEach(resource -> warehouse[1].add(String.valueOf(resource)));
        p.getPersonalBoard().getWarehouseDepots().getShelves()[2].getSlots().forEach(resource -> warehouse[2].add(String.valueOf(resource)));
        if(!p.getPersonalBoard().getSpecialShelves().isEmpty()) {
            p.getPersonalBoard().getSpecialShelves().get(0).ifPresent(specialShelf -> {
                specialShelf.getSpecialSlots().forEach(resource -> warehouse[3].add(String.valueOf(resource)));
            });
            p.getPersonalBoard().getSpecialShelves().get(1).ifPresent(specialShelf -> {
                specialShelf.getSpecialSlots().forEach(resource -> warehouse[4].add(String.valueOf(resource)));
            });
        }
        return warehouse;
    }

    private Map<Integer,Boolean> getCardsId(Player p) {
        Map<Integer, Boolean> cardsId = new HashMap<>();
        System.out.println("ho creato la mappa");
        p.getLeadCards().forEach(leadCard -> cardsId.put(leadCard.getId(), leadCard.isActive()));
        System.out.println("mi sono salvato gli id delle lead card");
        p.getPersonalBoard().getDevCardSlot().getDevCards().forEach(devCard -> cardsId.put(devCard.getId(), devCard.isActive()));
        System.out.println("mi sono salvato gli id delle dev card");
        return cardsId;
    }

}
