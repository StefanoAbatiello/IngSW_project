package it.polimi.ingsw.controller;

import it.polimi.ingsw.gameActions.ResourceInSupplyAction;
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
        LeadCard firstCard=LeadDeck.getCardFromId(card1);
        LeadCard secondCard=LeadDeck.getCardFromId(card2);
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
                        if (player.getPersonalBoard().removeProdResources(cardToBuy.getRequirements())) {
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
            getHandlerFromPlayer(id).send(new LobbyMessage("The player has already gone through with an action in their turn"));
        else if(gameObj <0||gameObj>6)
            getHandlerFromPlayer(id).send(new LobbyMessage("Selector out of range : "+ gameObj));
        else {
            //TODO check fullSupplyException
            player.setAction(Action.TAKEFROMMARKET);
            game.getMarket().buyResources(gameObj, player);
            ArrayList<Resource> resSupply = player.getResourceSupply().getResources();
            ArrayList<String> supply= (ArrayList<String>) resSupply.stream().map(resource-> Objects.toString(resource, null)).collect(Collectors.toList());
            getHandlerFromPlayer(id).send(new ResourceInSupplyAction("The following resources are ready in the supply: "+ supply));
            return true;
        }
        return false;
    }

    public boolean checkProduction(ArrayList<Integer> cardProd ,ArrayList<String> personalProdIn, Optional<String> personalProdOut, Optional<String> leadProdOut, int id) throws ActionAlreadySetException, ResourceNotValidException, CardNotOwnedByPlayerOrNotActiveException {
        //TODO cosa manda client, produzione personale e leader da controllare
        Player player = game.getPlayers().get(id);
        Optional<Action> playerAction = Optional.ofNullable(player.getAction());
        if (playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else if (checkOwnerCards(cardProd,player)) {
            ArrayList<Resource> totalProdIn = takeAllProdIn(cardProd, StringArrayToResArray(personalProdIn), player);
            ArrayList<Resource> totalProdOut;
            if(checkResourcePlayer(totalProdIn, player)) {
                player.setAction(Action.ACTIVATEPRODUCTION);
                player.getPersonalBoard().removeResources(totalProdIn);
                totalProdOut = takeAllProdOut(cardProd, StringArrayToResArray(personalProdIn), personalProdOut, leadProdOut, id);
                player.getPersonalBoard().getStrongBox().addInStrongbox(totalProdOut);
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

    private ArrayList<Resource> takeAllProdOut(ArrayList<Integer> cardProd ,ArrayList<Resource> personalProdIn, Optional<String> personalProdOut,Optional<String> leadProdOut, int id) {
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
        if(!(cardProd.stream().anyMatch(integer -> integer > 48)))
            if(leadProdOut.isPresent()) {
                totalProdOut.add(Resource.valueOf(leadProdOut.get()));
                game.getPlayers().get(id).getPersonalBoard().getFaithMarker().updatePosition();
            }else
                getHandlerFromPlayer(id).send(new LobbyMessage("Prod Out of the LeadCard requested missing"));
        if(!personalProdIn.isEmpty())
            if(personalProdOut.isPresent())
                totalProdOut.add(Resource.valueOf(personalProdOut.get()));
            else
                getHandlerFromPlayer(id).send(new LobbyMessage("Prod Out of the personal production requested missing"));

        return totalProdOut;
    }

    private boolean checkOwnerCards(ArrayList<Integer> cardsId,Player player){
        ArrayList<Integer> playerCards= new ArrayList<>();

        player.getPersonalBoard().getDevCardSlot().getDevCards().stream().forEach(card->{int id=card.getId();playerCards.add(id);});
        player.getLeadCards().stream().forEach(card->{int id=card.getId();playerCards.add(id);});

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

    private ArrayList<Resource> StringArrayToResArray(ArrayList<String> gameObj){
        ArrayList<Resource> allRes = new ArrayList<>();

        //me lo trasformo in un array di risorse
        for (String string : gameObj)
                allRes.add(Resource.valueOf(string));
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

    public boolean checkPositionOfResources(ArrayList<String>[] gameObj, int id){
        Player player = game.getPlayers().get(id);
        boolean result= false;
        if (gameObj.length <= 5){
            ResourceSupply supply= player.getResourceSupply();
            ArrayList<Resource> newRes= new ArrayList<>();

            for (ArrayList<String> strings : gameObj) newRes.addAll(StringArrayToResArray(strings));

            ArrayList<Resource> allResources=player.getPersonalBoard().getWarehouseDepots().getResources();

            if(!supply.getResources().isEmpty())
                allResources.addAll(supply.getResources());

            for(int i=0;i<2;i++)
                if (player.getPersonalBoard().getSpecialShelves().get(i).isPresent())
                    allResources.addAll(player.getPersonalBoard().getSpecialShelves().get(i).get().getSpecialSlots());

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
                    else
                       getHandlerFromPlayer(id).send(new LobbyMessage("Resources chosen out of bound, please verify you put the right resources in your board"));

                }else
                    getHandlerFromPlayer(id).send(new LobbyMessage("Resources chosen out of bound, please verify you put the right resources in your board"));
            else{
                if(checkShelfContent(gameObj,id)){
                    for(int i=0;i<4;i++) {
                        if(gameObj[i].isEmpty())
                            player.getPersonalBoard().getWarehouseDepots().getShelves()[i]=new Shelf(i+1);
                        else
                            player.getPersonalBoard().getWarehouseDepots().addinShelf(i,StringArrayToResArray(gameObj[i]));

                    }result = true;
                }else{
                    getHandlerFromPlayer(id).send(new LobbyMessage("Resources not valid in this disposition, please retry"));
                }
            }

        }
             return result;
    }

    private boolean checkShelfContent(ArrayList<String>[] gameObj, int id) {
        //ciclo su ogni mensola, la i corrisponde alla mesola da alto al basso
        for(int i=0; i<3;i++) {
            if (gameObj[i].size() <= i + 1) {
                for (int j = 0; j < gameObj[i].size() - 1; j++)
                    if (!gameObj[i].get(j).equals(gameObj[i].get(j + 1)))
                        return false;
            } else
                return false;
        }
        if(gameObj.length==4) {
            if (!checkSpecialShelf(StringArrayToResArray(gameObj[3]), id))
                return false;
        } else if(gameObj.length==5)
            for(int i=3;i<5;i++) {
                if (!checkSpecialShelf(StringArrayToResArray(gameObj[i]), id))
                    return false;
            }
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
