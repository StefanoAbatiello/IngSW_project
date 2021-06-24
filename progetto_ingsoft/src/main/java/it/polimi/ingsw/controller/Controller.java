package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Market.*;
import it.polimi.ingsw.model.personalboard.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Controller {

    /**
     * this is the Server of the game
     */
    private final MainServer server;

    /**
     * this is the Lobby of this game
     */
    private final Lobby lobby;

    /**
     * this is the model of this game
     */
    private Game game;

    /**
     * this is the client whose turn it is
     */
    private VirtualClient actualPlayerTurn;

    public Controller(Lobby lobby, MainServer server) {
        this.lobby=lobby;
        this.server=server;
    }

    /**
     * @return the player who is playing his turn
     */
    public Player getPlayerInTurn(){
        String name=getActualPlayerTurn().getNickName();
        return game.getPlayerFromName(name);
    }

    /**
     * @param id is the id of the client whose corresponding Player is the looked for
     * @return the Player looked for
     */
    public Player getPlayerFromId(int id) {
        int playerPosition = lobby.getPositionFromClient().get(server.getClientFromId().get(id));
        return game.getPlayers().get(playerPosition);
    }

    /**
     * @param position is the position of the Client whose handler is looked for in the turn order
     * @return the Client handler looked for
     */
    private ClientHandler getHandlerFromPlayerPosition(int position) {
        return lobby.getClientFromPosition().get(position).getClientHandler();
    }

    /**
     * @param id is the id of the client whose handler is looked for
     * @return the Client handler looked for
     */
    private ClientHandler getHandlerFromPlayer(int id){
        return server.getClientFromId().get(id).getClientHandler();
    }

    /**
     * @param name is the name of the client whose handler is looked for
     * @return the Client handler looked for
     */
    private ClientHandler getHandlerFromPlayer(String name){
        int id=server.getIDFromName().get(name);
        return server.getClientFromId().get(id).getClientHandler();
    }

    /**
     * this method creates the game,
     * if there is only one player in lobby it will be a Single player mode
     * otherwise a multi player mode
     */
    public void createGame(){
        lobby.sendAll(new LobbyMessage("The game is starting"));
        lobby.setStateOfGame(GameState.PREPARATION1);
        try{
            if(lobby.getPositionFromClient().size()==1) {
                System.out.println("creo partita singlePlayer");//[Debug]
                game = new SinglePlayer(lobby.getPlayersName().get(0));
                System.out.println("partita singlePlayer creata");//[Debug]
            }else {
                //System.out.println("creo partita multiPlayer");[Debug]
                game=new MultiPlayer(lobby.getPlayersName());
                //System.out.println("partita multiPlayer creata");[Debug]
                //TODO gestisco le eccezioni
            }
            notifyLeadCardDistributed();
        } catch (playerLeadsNotEmptyException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * advise each Player of the Leader cards he received
     */
    private void notifyLeadCardDistributed(){
        int i=0;
        for(VirtualClient client: lobby.getClientFromPosition().values()) {
            ArrayList<Integer> leaderId = new ArrayList<>();
            for (LeadCard card : game.getPlayers().get(i).getLeadCards())
                leaderId.add(card.getId());
            client.getClientHandler().send(new LeaderCardDistribution(leaderId, "Please choose 2 leader card to hold"));
            i++;
        }
    }


    /**
     * @return true if all the Players in game have chosen their Leader cards
     */
    public boolean checkAllPlayersChooseLeads(){
        //System.out.println("controllo se tutti hanno scelto le leads");[Debug]
        for(Player player:game.getPlayers()) {
            if (player.getLeadCards().size() != 2) {
                //System.out.println("player "+i + " non ha ancora scelto le leads");[Debug]
                return false;
            }
        }
        //System.out.println("tutti i giocatori hanno già scelto le leads");[Debug]
        return true;
    }

    /**
     * @param id is the id of the client who choose this cards
     * @param card1 is the id of the first Leader card
     * @param card2 is the id of the second Leader card
     * @return true if the chosen is done correctly
     */
    public boolean check2Leads(int id, int card1, int card2){
        //System.out.println("controllo gli id");[Debug]
        Player player = getPlayerFromId(id);
        if(!LeaderCardChosenYet(player)) {
            if (checkLeadsIdChosen(player,card1,card2)){
                getHandlerFromPlayer(id).send(new LobbyMessage("Leader cards picked correctly, now wait the other players"));
                //System.out.println("gli id scelti vanno bene");[Debug]
                return  player.choose2Leads(card1, card2);
            }else {
                //System.out.println("gli id scelti non vanno bene");[Debug]
                ArrayList<Integer> leaderId = player.getLeadCardsId();
                //System.out.println("mi sono salvato gli id delle carte");[Debug]
                server.getClientFromId().get(id).getClientHandler().send(new LeaderCardDistribution(leaderId,
                        "You choose not valid leader cards "));
                return false;
            }
        }else{
            //System.out.println("il client " +id+" ha già scelto le carte");[Debug]
            getHandlerFromPlayer(id).send(new LobbyMessage("You have chosen yours leader cards yet"));
            return false;
        }
    }

    /**
     * @param player is the player to whom I have to check the number of Leader cards
     * @return true if he has only two Leader cards
     */
    private boolean LeaderCardChosenYet(Player player){
        return player.getLeadCards().size()==2;
    }

    /**
     * @param player is the player to whom I have to check if the id chosen are valid
     * @param card1 is the id of the first Leader card
     * @param card2 is the id of the second Leader card
     * @return true if the id chosen are valid(he holds the card chosen)
     */
    private boolean checkLeadsIdChosen(Player player, int card1, int card2) {
        return player.getLeadCards().stream().anyMatch(leadCard -> leadCard.getId()==card1) &&
                player.getLeadCards().stream().anyMatch(leadCard -> leadCard.getId()==card1) &&
                card1!=card2;
    }
    //TODO popeSpace control

    /**
     * if the game is a single player mode this method start the game,
     * otherwise advise the players that they can take some initial Resources
     */
    public void askInitialResources() {
        ArrayList<Player> players = game.getPlayers();
        if(players.size()>1 && lobby.playersOnline()>1) {
            lobby.setStateOfGame(GameState.PREPARATION2);
            if(server.isClientOnline(players.get(0).getName())) {
                getHandlerFromPlayerPosition(0).send(new LobbyMessage("Wait until other players have chosen initial resources"));
            }if(server.isClientOnline(players.get(1).getName())) {
                getHandlerFromPlayerPosition(1).send(new GetInitialResourcesAction("You can choose 1 initial resource", 1));
            }if(players.size()>2 && server.isClientOnline(players.get(2).getName())) {
                getHandlerFromPlayerPosition(2).send(new GetInitialResourcesAction(
                        "You can choose 1 initial resource, you will receive a faith point also", 1));
                players.get(2).getPersonalBoard().getFaithMarker().updatePosition();
            }if(players.size()>3 && server.isClientOnline(players.get(3).getName())) {
                getHandlerFromPlayerPosition(3).send(new GetInitialResourcesAction(
                        "You can choose 2 initial resources, you will receive a faith point also", 2));
                players.get(3).getPersonalBoard().getFaithMarker().updatePosition();
            }
        }else
            startGame();
    }

    /**
     * @param position is the player's position in turns order
     * @return the number of Resources which the Player can take based on his position
     */
    public int playerInitialResources(int position) {
        if (position==0)
            return 0;
        if (position==1 || position==2)
            return 1;
        return 2;
    }

    /**
     * @param position is the player's position in turns order
     * @return the number of Faith points which the Player can take based on his position
     */
    public int playerInitialFaithPoint(int position){
        if(position==0||position==1)
            return 0;
        return 1;
    }

    /**
     * @return true if all the players have chosen the correct number of initial Resources
     * and have received the correct number of Faith points
     */
    public boolean checkInitialResources() {
        System.out.println("sto controllando se gli altri giocatori hanno scelto le risorse");
        boolean result=true;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            String name= game.getPlayers().get(i).getName();
            System.out.println("sto controllando "+name);
            if (!checkPlayerWarehouse(i)) {
                System.out.println("non ha ancora scelto le risorse initiali");
                result=false;
            } else
                System.out.println(name+" ha il giusto numero di risorse");
            checkPlayerInitialFaithMarker(i);
            System.out.println("faithmarker sistemato");
        }
        return result;
    }

    /**
     * @param i is the position of the player
     */
    private void checkPlayerInitialFaithMarker(int i) {
        System.out.println("controllo il suo faithmarker");
        Player player = getPlayerFromId(getHandlerFromPlayerPosition(i).getClientId());
        int position = player.getPersonalBoard().getFaithMarker().getFaithPosition();
        System.out.println("mi sono salvato la sua posizione");
        if (position != playerInitialFaithPoint(i)) {
            System.out.println("ha ricevuto uno sbagliato numero di faith points");
            position = player.getPersonalBoard().getFaithMarker().reset();
            System.out.println("ho resettato il faithmarker");
            while (position < playerInitialFaithPoint(i)) {
                System.out.println("gli sto assegnando un punto");
                player.getPersonalBoard().getFaithMarker().updatePosition();
            }
        }
    }

    /**
     * @param i is the position of the player
     * @return true if he has choose the correct number of resources yet
     */
    private boolean checkPlayerWarehouse(int i){
        System.out.println("sto controllando il suo warehouse");
        Player player=getPlayerFromId(getHandlerFromPlayerPosition(i).getClientId());
        ArrayList<Resource> resources=player.getPersonalBoard().getWarehouseDepots().getResources();
        System.out.println("mi sono salvato tutte le sue risorse");
        if (resources.size()!= playerInitialResources(i) ) {
            if (resources.size()>0) {
                System.out.println("ha scelto un numero sbagliato di risorse");
                game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().clear();
                System.out.println("ho pulito il suo warehouse");
                if (i != 0 && server.isClientOnline(game.getPlayers().get(i).getName())) {
                    System.out.println("gli chiedo di scegliere nuovamente");
                    getHandlerFromPlayerPosition(i).send(new GetInitialResourcesAction("You have choose an incorrect number of resources, please resend your initial resources:  ", playerInitialResources(i)));
                }
            }return false;
        }
        else return true;
    }

    /**
     * @param id is the id of the client
     * @param position is the level of the Chelf chosen by the client
     * @param resource is the Resource that is to put on the shelf chosen
     * @throws ResourceNotValidException if the shelf chosen is not valid
     */
    public void checkInsertResourcePosition(int id, int position, Resource resource) throws ResourceNotValidException {
        //System.out.println("sto controllando se il giocatore può mettere la risorse nello shelf richiesto");[Debug]
        Player player = getPlayerFromId(id);
        Shelf shelf= player.getPersonalBoard().getWarehouseDepots().getShelves()[position];
        //System.out.println("mi sono salvato lo shelf richiesto");[Debug]
        if(((shelf.isShelfAvailability()) && (resource.equals(shelf.getResourceType()))) || shelf.getSlots().isEmpty()) {
            //System.out.println("è possibile inserire la risorsa nello shef");[Debug]
            player.getPersonalBoard().getWarehouseDepots().addInShelf(position, resource);
        }else {
            //System.out.println("NON è possibile inserire la risorsa nello shelf richiesto");[Debug]
            throw new ResourceNotValidException("Cannot put the resource in the chosen shelf");
        }
    }

    //TODO methods actions
    //TODO creo mappa
    public boolean checkBuy(int card, int id, int position) throws CardNotOnTableException, ResourceNotValidException, InvalidSlotException, ActionAlreadySetException {
        Player player=getPlayerInTurn();
        Optional<Action> playerAction= Optional.ofNullable(player.getAction());
        if(playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else if(position<0||position>2)
            throw new InvalidSlotException();
        else {
            System.out.println("ho controllato l'azione e la posizione scelta");
            DevCard[][] upper;
            if(playerCardLevel(player, card)) {
                upper = game.getDevDeckMatrix().getUpperDevCardsOnTable();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        System.out.println(upper[i][j].getId());
                    }
                }
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (upper[i][j].getId() == card) {
                            System.out.println("ho trovato la carta");
                            DevCard cardToBuy;
                            try {
                                cardToBuy = game.getDevDeck().getCardFromId(card);
                            } catch (CardChosenNotValidException e) {
                                System.err.println(e.getMessage());
                                server.getClientFromId().get(id).getClientHandler().send(new LobbyMessage(e.getMessage()));
                            return false;
                            }
                            System.out.println("mi sono preso la carta");
                            if (player.getPersonalBoard().removeResourcesFromBuy(cardToBuy.getRequirements())) {
                                System.out.println("ha le risorse necessarie");
                                player.setAction(Action.BUYCARD);
                                game.getDevDeckMatrix().buyCard(cardToBuy);
                                System.out.println("ha comprato la carta, invio la nuova dev matrix");
                                lobby.sendAll(new DevMatrixChangeMessage(game.getSimplifiedDevMatrix()));
                                System.out.println("dev matrix inviata");
                                player.getPersonalBoard().removeResources(cardToBuy.getRequirements());
                                getHandlerFromPlayer(id).send(new WareHouseChangeMessage(player.getPersonalBoard().getSimplifiedWarehouse()));
                                getHandlerFromPlayer(id).send(new StrongboxChangeMessage(player.getPersonalBoard().getSimplifiedStrongbox()));
                                System.out.println("ho rimosso le risorse usate");
                                player.getPersonalBoard().getDevCardSlot().overlap(cardToBuy, position);
                                System.out.println("ho posizionato la carta");
                                getHandlerFromPlayer(id).send(new CardIDChangeMessage(player.getCardsId()));
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
        DevCard devCard;
        try {
            devCard = game.getDevDeck().getCardFromId(card);
        } catch (CardChosenNotValidException e) {
            System.err.println(e.getMessage());
            return false;
        }
        int level=devCard.getLevel();
        if(level==1) {
            return player.getPersonalBoard().getDevCardSlot().getActiveCards().size() < 3;
        }else {
                for (DevCard card1 : player.getPersonalBoard().getDevCardSlot().getActiveCards())
                    if (card1.getLevel() == level - 1)
                        return true;
       }
        return false;
    }

    public void checkMarket(int gameObj, int id) throws NotAcceptableSelectorException, FullSupplyException, ActionAlreadySetException {
        Player player=getPlayerInTurn();
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
            lobby.setStateOfGame(GameState.MARKET);
            System.out.println("azione eseguibile");
            player.setAction(Action.TAKEFROMMARKET);
            game.getMarket().buyResources(gameObj, player);
            String[][] simplifiedMarket = game.getSimplifiedMarket();
            lobby.sendAll(new MarketChangeMessage(simplifiedMarket));
            System.out.println("risorse messe nel supply");
            ArrayList<Resource> resSupply = player.getResourceSupply().viewResources();
            if(resSupply.contains(Resource.CHOOSABLE)) {
                int num=Collections.frequency(resSupply,Resource.CHOOSABLE);
                getHandlerFromPlayer(id).send(new ChangeChoosableResourceRequest(num,"You can choose between "+player.getWhiteMarbleAbility().get(0)+" and "+player.getWhiteMarbleAbility().get(1)));
            }else {
                ArrayList<String> supply = (ArrayList<String>) resSupply.stream().map(resource -> Objects.toString(resource, null)).collect(Collectors.toList());
                System.out.println("invio la richiesta di sistemare le risorse");
                getHandlerFromPlayer(id).send(new ResourceInSupplyRequest(supply));
            }
        }
    }

    public void checkChangeChooosable(int clientId, ArrayList<String> newRes){
        Player player=getPlayerInTurn();
        ArrayList<Resource> newResources=stringArrayToResArray(newRes);
        ArrayList<Resource> resSupply = player.getResourceSupply().viewResources();
        int num=Collections.frequency(resSupply,Resource.CHOOSABLE);
        if(newResources.size()==num) {
            for (Resource res : newResources) {
                if (res == player.getWhiteMarbleAbility().get(0))
                    player.getResourceSupply().changeChoosable(res);
                else if (res == player.getWhiteMarbleAbility().get(1))
                    player.getResourceSupply().changeChoosable(res);
                else {
                    server.getClientFromId().get(clientId).getClientHandler().send(new LobbyMessage("Choice not possible, please try again"));
                    server.getClientFromId().get(clientId).getClientHandler().send(new ChangeChoosableResourceRequest(num, "You can choose between " + player.getWhiteMarbleAbility().get(0) + " and " + player.getWhiteMarbleAbility().get(1)));
                    return;
                }
            }
        }else{
            server.getClientFromId().get(clientId).getClientHandler().send(new ChangeChoosableResourceRequest(num,"Too many resources choosen, please try again"));
            return;
        }
        ArrayList<String> supply = (ArrayList<String>) resSupply.stream().map(resource -> Objects.toString(resource, null)).collect(Collectors.toList());
        server.getClientFromId().get(clientId).getClientHandler().send(new ResourceInSupplyRequest(supply));
    }

    public boolean checkProduction(ArrayList<Integer> cardProd , ArrayList<String> personalProdIn, String personalProdOut, ArrayList<String> leadProdOut, int id) throws ActionAlreadySetException, ResourceNotValidException, CardNotOwnedByPlayerOrNotActiveException {
        //TODO cosa manda client, produzione personale e leader da controllare
Player player=getPlayerInTurn();
        ArrayList<Resource> resourceArrayList=player.getPersonalBoard().getStrongBox().getStrongboxContent();
            resourceArrayList.addAll(player.getPersonalBoard().getWarehouseDepots().getResources());
       if(!player.getPersonalBoard().getSpecialShelves().isEmpty()) {
            if(player.getPersonalBoard().getSpecialShelves().get(0).isPresent())
                resourceArrayList.addAll(player.getPersonalBoard().getSpecialShelves().get(0).get().getSpecialSlots());
            if(player.getPersonalBoard().getSpecialShelves().get(1).isPresent())
            resourceArrayList.addAll(player.getPersonalBoard().getSpecialShelves().get(1).get().getSpecialSlots());
        }

        Optional<Action> playerAction = Optional.ofNullable(player.getAction());
        if (playerAction.isPresent())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else if (checkOwnerCards(cardProd,player)) {
            ArrayList<Resource> totalProdIn;
            try {
                totalProdIn = takeAllProdIn(cardProd, stringArrayToResArray(personalProdIn), player);
            } catch (CardChosenNotValidException e) {
                getHandlerFromPlayer(id).send(new LobbyMessage(e.getMessage()));
                return false;
            }
            ArrayList<Resource> totalProdOut;
            if(checkResourcePlayer(totalProdIn, player)) {
                player.setAction(Action.ACTIVATEPRODUCTION);
                totalProdOut = takeAllProdOut(cardProd, stringArrayToResArray(personalProdIn), personalProdOut, leadProdOut, id);
                player.getPersonalBoard().getStrongBox().addInStrongbox(totalProdOut);
                getHandlerFromPlayer(id).send(new StrongboxChangeMessage(player.getPersonalBoard().getSimplifiedStrongbox()));
                getHandlerFromPlayer(id).send(new WareHouseChangeMessage(player.getPersonalBoard().getSimplifiedWarehouse()));
                return true;
            }

        }

        return false;
    }


    private boolean checkResourcePlayer(ArrayList<Resource> totalProdIn, Player player) {
       player.getPersonalBoard().removeResources(totalProdIn);
            return true;
    }

    private ArrayList<Resource> takeAllProdIn( ArrayList<Integer> cardProd ,ArrayList<Resource> personalProdIn, Player player) throws CardChosenNotValidException {
        ArrayList<Resource> totalProdIn = new ArrayList<>();
        ArrayList<DevCard> prodDevs = new ArrayList<>();
        ArrayList<LeadCard> prodLeads = new ArrayList<>();
        cardProd.stream().filter(integer -> integer > 0 && integer < 49).forEach(integer -> {
            DevCard dev;
            try {
                dev = game.getDevDeck().getCardFromId(integer);
            } catch (CardChosenNotValidException e) {
                System.err.println(e.getMessage());
                return;
            }
            prodDevs.add(dev);
        });
        AtomicBoolean correctLead= new AtomicBoolean(true);
        cardProd.stream().filter(integer -> integer > 48 && integer < 65).forEach(integer -> {
            LeadCard lead = null;
            try {
                lead = player.getCardFromId(integer);
            } catch (CardChosenNotValidException e) {
                correctLead.set(false);
            }
            prodLeads.add(lead);
        });
        if (!correctLead.get())
            throw new CardChosenNotValidException("You have not the card chosen");
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
            DevCard dev;
            try {
                dev = game.getDevDeck().getCardFromId(integer);
            } catch (CardChosenNotValidException e) {
                System.err.println(e.getMessage());
                return;
            }
            prodDevs.add(dev);
        });
        prodDevs.forEach(card -> {
                ArrayList<Resource> prodOut = card.getProdOut();
                totalProdOut.addAll(prodOut);
            });
        int numofLead= (int) cardProd.stream().filter(integer -> integer > 48).count();
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

        player.getPersonalBoard().getDevCardSlot().getDevCards().forEach(card->{int id=card.getId();playerCards.add(id);});
        player.getLeadCards().forEach(card->{int id=card.getId();
            if(card.isActive()&&(card.getAbility() instanceof LeadAbilityProduction))
                    playerCards.add(id);});

        return playerCards.containsAll(cardsId);



    }

    public void checkLeadActivation(int gameObj, int id) {
        String name = getActualPlayerTurn().getNickName();
        Player player = game.getPlayers().get(0);
        for (Player p : game.getPlayers()) {
            if (p.getName().equals(name))
                player = p;
        }
        System.out.println("mi sono salvato il player");
        if (gameObj < 48 || gameObj > 64) {
            getHandlerFromPlayer(id).send(new LobbyMessage("LeadCard ID not valid"));
        } else {
            System.out.println("mi hai passato l'id di una lead");
            try {
                LeadCard card = player.getCardFromId(gameObj);
                if (card.isActive()) {
                    getHandlerFromPlayer(id).send(new LobbyMessage("This leadCard is already active"));
                } else {
                    System.out.println("puoi attivare la carta");
                    player.activateAbility(card);
                    getHandlerFromPlayer(id).send(new CardIDChangeMessage(player.getCardsId()));
                }
            } catch (CardChosenNotValidException e) {
                getHandlerFromPlayer(id).send(new LobbyMessage("You do not own the leadCard chosen"));
            }
        }
    }

    public void checkDiscardLead(int gameObj, int id) {
        String name = getActualPlayerTurn().getNickName();
        Player player = game.getPlayers().get(0);
        for (Player p : game.getPlayers()) {
            if (p.getName().equals(name))
                player = p;
        }System.out.println("mi sono salvato il player");
        if (gameObj < 48 || gameObj > 64) {
            getHandlerFromPlayer(id).send(new LobbyMessage("LeadCard ID not valid"));
        } else {
            System.out.println("mi hai passato l'id di una lead");
            try {
                LeadCard card = player.getCardFromId(gameObj);
                if (card.isActive()) {
                    getHandlerFromPlayer(id).send(new LobbyMessage("This leadCard is already active"));
                } else {
                    System.out.println("puoi attivare la carta");
                    player.discardLead(card);
                    getHandlerFromPlayer(id).send(new CardIDChangeMessage(player.getCardsId()));
                }
            } catch (CardChosenNotValidException e) {
                getHandlerFromPlayer(id).send(new LobbyMessage("You do not own the leadCard chosen"));
            }
        }
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

        return !result;
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
            System.out.println("dimensione del nuovo warehouse valida");
            ResourceSupply supply= player.getResourceSupply();
            ArrayList<Resource> newRes= new ArrayList<>();

            for (ArrayList<String> strings : gameObj) newRes.addAll(stringArrayToResArray(strings));

            ArrayList<Resource> allResources= null;
            allResources = player.getPersonalBoard().getWarehouseDepots().getResources();

            try {
                if(!supply.getResources().isEmpty())
                    allResources.addAll(supply.getResources());
            } catch (NoSuchRequirementException e) {
                e.printStackTrace();
            }
            System.out.println("mi sono salvato le risorse del player in strongbox e in warehouse");
            if(!player.getPersonalBoard().getSpecialShelves().isEmpty()) {
                for (int i = 0; i < 2; i++)
                    if (player.getPersonalBoard().getSpecialShelves().get(i).isPresent())
                        allResources.addAll(player.getPersonalBoard().getSpecialShelves().get(i).get().getSpecialSlots());
                System.out.println("mi sono salvato le risorse del plat");
            }
            allResources.removeIf(newRes::contains);

            if(!allResources.isEmpty()){
                int pointsToGive =player.getResourceSupply().discardResources(allResources);
                game.pointsGiveAway(player, pointsToGive);
            }

            else{
                if(checkShelfContent(gameObj,id)){
                    for(int i=0;i<3;i++) {
                        if(gameObj[i].isEmpty()) {
                            System.out.println("DEBUG 3.1");
                            player.getPersonalBoard().getWarehouseDepots().getShelves()[i] = new Shelf(i + 1);
                            System.out.println("DEBUG 3.2");
                        }else
                            player.getPersonalBoard().getWarehouseDepots().addInShelf(i, stringArrayToResArray(gameObj[i]));
                    }
                    if(!player.getPersonalBoard().getSpecialShelves().isEmpty()){
                        for(int i=3;i<5;i++) {
                            Resource resource=Resource.CHOOSABLE;
                            if(player.getPersonalBoard().getSpecialShelves().get(i-3).isPresent())
                                resource=player.getPersonalBoard().getSpecialShelves().get(i-3).get().getResourceType();
                            player.getPersonalBoard().getSpecialShelves().remove(i-3);
                            System.out.println("DEBUG 3.3");
                            player.getPersonalBoard().getSpecialShelves().add(i-3, Optional.of(new SpecialShelf(resource)));
                            if(!gameObj[i].isEmpty()) {
                                System.out.println("DEBUG 3.4");
                                player.getPersonalBoard().getWarehouseDepots().addInShelf(i, stringArrayToResArray(gameObj[i]));
                                System.out.println("DEBUG 3.5");
                            }
                        }
                    }
                    System.out.println("DEBUG 3.6");
                    getHandlerFromPlayer(id).send( new WareHouseChangeMessage(player.getPersonalBoard().getSimplifiedWarehouse()));
                    System.out.println("DEBUG 3.7");
                }else{
                    getHandlerFromPlayer(id).send(new LobbyMessage("Resources not valid in this disposition, please retry"));
                }
            }
        }
        System.out.println("DEBUG 4");
    }

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
            if (checkSpecialShelf(stringArrayToResArray(gameObj[3]), id))
                return false;
        }
        if(!gameObj[4].isEmpty()){
                System.out.println("controllo secondo special shelf");
                if (checkSpecialShelf(stringArrayToResArray(gameObj[4]), id))
                    return false;
            }
        System.out.println("special shelf vuoti");
        return true;
    }

    public void startGame() {
        lobby.setStateOfGame(GameState.ONGOING);
        System.out.println("sono nello start game");
        actualPlayerTurn=lobby.getClientFromPosition().get(0);
        System.out.println("sto creando il startingGameMessage");
        System.out.println("l'actual player turn è "+actualPlayerTurn.getID());
        System.out.println("l'actual player turn è "+actualPlayerTurn.getNickName());
        String[][] simplifiedMarket =game.getSimplifiedMarket();
        System.out.println("market salvato");
        lobby.sendAll(new MarketChangeMessage(simplifiedMarket));
        int[][] devMatrix=game.getSimplifiedDevMatrix();
        System.out.println("devMatrix salvata");
        for(Player p:game.getPlayers()){
            String name=p.getName();
            if (server.isClientOnline(name)) {
                System.out.println(name);
                ArrayList<String>[] warehouse = p.getPersonalBoard().getSimplifiedWarehouse();
                Map<Integer, Boolean> cardsId = p.getCardsId();
                System.out.println("warehouse di " + name + " salvato");
                int faithPosition = p.getPersonalBoard().getFaithMarker().getFaithPosition();
                System.out.println("mi sono salvato il faith marker");
                //int[] simplifiedStrongbox=p.getPersonalBoard().getSimplifiedStrongbox();
                System.out.println("messaggio costruito per " + name);
                getHandlerFromPlayer(name).send(new LobbyMessage("ciao"));
                getHandlerFromPlayer(name).send(new StartingGameMessage(cardsId, warehouse, faithPosition, simplifiedMarket, devMatrix, "We are ready to start. it's turn of " /*+
                        server.getNameFromId().get(actualPlayerTurn.getID())*/,p.getPersonalBoard().getSimplifiedStrongbox() ));
                System.out.println("messaggio inviato al player " + name);
            }
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
                ArrayList<Resource> resources=player.getResourceSupply().viewResources();
                if(!resources.isEmpty()){
                    int pointsToGive =player.getResourceSupply().discardResources(resources);
                    game.pointsGiveAway(player, pointsToGive);
                }
                actualIndex=game.getPlayers().indexOf(player);
                player.resetAction();
                break;
            }
        }
        actualIndex=(actualIndex+1)%(game.getPlayers().size());
        String name=game.getPlayers().get(actualIndex).getName();
        id=server.getIDFromName().get(name);
        actualPlayerTurn=server.getClientFromId().get(id);
        String s =game.draw();
        if(s.isEmpty()) {
            lobby.sendAll(new LobbyMessage("è il turno di " + server.getNameFromId().get(actualPlayerTurn.getID())));
        }else if(s.equalsIgnoreCase("finished")) {
            //TODO gestione fine gioco
        } else{
            if(s.contains("Lorenzo has discarded two development card of color ")) {
                lobby.sendAll(new DevMatrixChangeMessage(game.getSimplifiedDevMatrix()));
            }
            lobby.sendAll(new LobbyMessage(s+", è di nuovo il tuo turno"));
        }
    }

    public void insertPlayerInOrder(int id, String name) {
        ArrayList<String> names;
        names= (ArrayList<String>) game.getPlayers().stream().map(Player::getName).collect(Collectors.toList());
        int position= names.indexOf(name);
        lobby.getPositionFromClient().put(server.getClientFromId().get(id),position);
        lobby.getClientFromPosition().put(position,server.getClientFromId().get(id));
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
                        lobby.getClientFromPosition().get(0).getClientHandler().send(new LobbyMessage("Wait until other players have chosen initial resources"));
                    else if (index == 1)
                        lobby.getClientFromPosition().get(1).getClientHandler().send(new GetInitialResourcesAction("You can choose 1 initial resource",1));
                    else if (index == 2)
                        lobby.getClientFromPosition().get(2).getClientHandler().send(new GetInitialResourcesAction(
                                "You can choose 1 initial resource, you will receive a faith point also",1));
                    else
                        lobby.getClientFromPosition().get(3).getClientHandler().send(new GetInitialResourcesAction(
                                "You can choose 2 initial resources, you will receive a faith point also",2));
                }
            }
        }
        else if(lobby.getStateOfGame()==GameState.ONGOING) {
            String[][] simplifiedMarket = game.getSimplifiedMarket();
            System.out.println("market salvato");
            getHandlerFromPlayer(id).send(new MarketChangeMessage(simplifiedMarket));
            int[][] devMatrix= game.getSimplifiedDevMatrix();
            System.out.println("devMatrix salvata");
            getHandlerFromPlayer(id).send(new DevMatrixChangeMessage(devMatrix));

            for (Player p : game.getPlayers()) {
                if (p.getName().equals(name)) {
                    ArrayList<String>[] warehouse = p.getPersonalBoard().getSimplifiedWarehouse();
                    Map<Integer,Boolean> cardsId = p.getCardsId();
                    System.out.println("warehouse di " + name + " salvato");
                    getHandlerFromPlayer(id).send(new WareHouseChangeMessage(warehouse));
                    getHandlerFromPlayer(id).send(new CardIDChangeMessage(cardsId));
                    int faithPosition = p.getPersonalBoard().getFaithMarker().getFaithPosition();
                    System.out.println("messaggio costruito per " + name);
                    getHandlerFromPlayer(id).send(new FaithPositionChangeMessage(faithPosition));
                }
            }
        }
    }
}
