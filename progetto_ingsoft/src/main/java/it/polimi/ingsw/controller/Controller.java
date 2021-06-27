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
     * @param player is the Player to check
     * @return true if the Player has done an action in this turn yet
     */
    private boolean checkActionDoneYet(Player player){
        Optional<Action> playerAction= Optional.ofNullable(player.getAction());
        return playerAction.isPresent();
    }

    /**
     * @param resources is the ArrayList of Resources to change in ArrayList of Strings
     * @return the correspondent ArrayList of Strings
     */
    private ArrayList<String> resArrayToStringArray(ArrayList<Resource> resources) {
        ArrayList<String> result=new ArrayList<>();
        resources.forEach(resource -> result.add(resource.toString()));
        return result;
    }

    /**
     * @param resources is the ArrayList of Strings to change in ArrayList of Resources
     * @return the correspondent ArrayList of Resources
     */
    private ArrayList<Resource> stringArrayToResArray(ArrayList<String> resources){
        ArrayList<Resource> allRes = new ArrayList<>();
        resources.forEach(res->allRes.add(Resource.valueOf(res)));
        return allRes;
    }

    /**
     * this method creates the game,
     * if there is only one player in lobby it will be a Single player mode
     * otherwise a multi player mode
     */
    public void createGame(){
        //lobby.sendAll(new LobbyMessage("The game is starting"));[Debug]
        lobby.setStateOfGame(GameState.PREPARATION1);
        try{
            if(lobby.getPositionFromClient().size()==1) {
                //System.out.println("creo partita singlePlayer");[Debug]
                game = new SinglePlayer(lobby.getPlayersName().get(0));
                //System.out.println("partita singlePlayer creata");[Debug]
            }else {
                //System.out.println("creo partita multiPlayer");[Debug]
                game=new MultiPlayer(lobby.getPlayersName());
                //System.out.println("partita multiPlayer creata");[Debug]
            }
            for(VirtualClient client: lobby.getClientFromPosition().values())
                notifyLeadCardDistributed(client);
        } catch (playerLeadsNotEmptyException | IOException | ParseException e) {
            //TODO gestisco le eccezioni
            e.printStackTrace();
        }
    }

    /**
     * this method summarise the initial situation of the game and send to all player those info
     */
    public void startGame() {
        //System.out.println("sono nello start game");[Debug]
        lobby.setStateOfGame(GameState.ONGOING);
        actualPlayerTurn=lobby.getClientFromPosition().get(0);
        for(Player p:game.getPlayers()){
            String name=p.getName();
            if (server.isClientOnline(name)) {
                sendMarketInfo(p);
                sendDevCardMatrixInfo(p);
                sendWarehouseInfo(p);
                sendPlayerCardsInfo(p);
                sendFaithMarkerPosition(p);
                sendStrongboxInfo(p);
            }
        }
        lobby.sendAll(new StartingGameMessage());
    }

    /**
     * advise each Player of the Leader cards he received
     */
    private void notifyLeadCardDistributed(VirtualClient client){
        int i=lobby.getPositionFromClient().get(client);
        ArrayList<Integer> leaderId = new ArrayList<>();
        for (LeadCard card : game.getPlayers().get(i).getLeadCards())
            leaderId.add(card.getId());
        client.getClientHandler().send(new LeaderCardDistribution(leaderId, "Please choose 2 leader card to hold"));
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
        if(!leaderCardChosenYet(player)) {
            if (checkLeadsIdChosen(player,card1,card2)){
                getHandlerFromPlayer(id).send(new LobbyMessage("Leader cards picked correctly"));
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
    private boolean leaderCardChosenYet(Player player){
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
     * @param client is the client who has to choose the initial Resource
     */
    public void askInitialResources(VirtualClient client) {
        lobby.setStateOfGame(GameState.PREPARATION2);
        if (server.isClientOnline(client.getNickName())) {
            int position = lobby.getPositionFromClient().get(client);
            if (position == 0)
                getHandlerFromPlayerPosition(position).send(new LobbyMessage("Wait until other players have chosen initial resources"));
            else {
                int num = playerInitialResources(position);
                getHandlerFromPlayerPosition(position).send(new GetInitialResourcesAction("You can choose " + num + " initial resource", num));
                if (position >= 2) {
                    ClientHandler handler=getHandlerFromPlayerPosition(position);
                    handler.send(new LobbyMessage("you will receive a Faith point also"));
                    getPlayerFromId(handler.getClientId()).getPersonalBoard().getFaithMarker().updatePosition();
                }
            }
        }
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
        //System.out.println("sto controllando se gli altri giocatori hanno scelto le risorse");[Debug]
        boolean result=true;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            String name= game.getPlayers().get(i).getName();
            //System.out.println("sto controllando "+name);[Debug]
            if (!checkPlayerStartingWarehouse(i)) {
                System.out.println(name+" non ha ancora scelto le risorse initiali");//[Debug]
                result=false;
            } else {
                System.out.println(name + " ha il giusto numero di risorse");//[Debug]
                checkPlayerInitialFaithMarker(i);
                System.out.println(name+": faithmarker ok");//[Debug]
            }
        }
        return result;
    }

    /**
     * @param i is the position of the player
     */
    private void checkPlayerInitialFaithMarker(int i) {
        //System.out.println("controllo il suo faithmarker");[Debug]
        Player player = getPlayerFromId(getHandlerFromPlayerPosition(i).getClientId());
        //System.out.println("mi sono salvato la sua posizione");[Debug]
        if (player.getPersonalBoard().getFaithMarker().getFaithPosition() != playerInitialFaithPoint(i)) {
            System.out.println("ha ricevuto uno sbagliato numero di faith points");//[Debug]
            player.getPersonalBoard().getFaithMarker().reset();
            //System.out.println("ho resettato il faithmarker");[Debug]
            while (player.getPersonalBoard().getFaithMarker().getFaithPosition() < playerInitialFaithPoint(i)) {
                System.out.println("gli sto assegnando un punto");//[Debug]
                player.getPersonalBoard().getFaithMarker().updatePosition();
            }
        }
    }

    /**
     * @param i is the position of the player
     * @return true if the Player has chosen the correct number of resources yet
     */
    private boolean checkPlayerStartingWarehouse(int i){
        //System.out.println("sto controllando il suo warehouse");[Debug]
        Player player=getPlayerFromId(getHandlerFromPlayerPosition(i).getClientId());
        ArrayList<Resource> resources=player.getPersonalBoard().getWarehouseDepots().getResources();
        //System.out.println("mi sono salvato tutte le sue risorse");[Debug]
        if (resources.size()!= playerInitialResources(i) ) {
            if (resources.size()>0) {
                //System.out.println("ha scelto un numero sbagliato di risorse");[Debug]
                game.getPlayers().get(i).getPersonalBoard().getWarehouseDepots().clear();
                //System.out.println("ho pulito il suo warehouse");[Debug]
                if (i != 0 && server.isClientOnline(game.getPlayers().get(i).getName())) {
                    //System.out.println("gli chiedo di scegliere nuovamente");[Debug]
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

    //TODO check discount in controller
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
                            if (player.getPersonalBoard().checkResourcesForUsages(cardToBuy.getRequirements())) {
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


    /**
     * @param index is the index of the market wanted by the Player
     * @param id is the id of the Player
     * @throws ActionAlreadySetException if the Player has done an action in this turn already
     * @throws NotAcceptableSelectorException if the Player has chosen a selector <0 or >6
     */
    public void checkMarket(int index, int id) throws ActionAlreadySetException, NotAcceptableSelectorException {
        Player player=getPlayerInTurn();
        if (checkActionDoneYet(player)){
            throw new ActionAlreadySetException("You have already gone through with an action in this turn");
        } else if(index <0 || index >6) {
            throw new NotAcceptableSelectorException("Index out of range : " + index);
        } else {
            lobby.setStateOfGame(GameState.MARKET);
            player.setAction(Action.TAKEFROMMARKET);
            game.getMarket().buyResources(index, player);
            lobby.sendAll(new MarketChangeMessage(game.getSimplifiedMarket()));
            ArrayList<String> resSupply = player.getSimplifiedSupply();
            int num=Collections.frequency(resSupply,"CHOOSABLE");
            if(num>0) {
                getHandlerFromPlayer(id).send(new ChangeChoosableResourceRequest(num, resArrayToStringArray(player.getWhiteMarbleAbility()),
                        "You can choose between this Resources"));
            }else {
                getHandlerFromPlayer(id).send(new ResourceInSupplyRequest(resSupply));
            }
        }
    }

    /**
     * @param newResources is the list of String indicating the Resources wanted by the player
     * @param player is the player who wants to change the resources
     * @return true if all the Choosable Resources are changed in valid Resources
     */
    public boolean checkChangeResource(ArrayList<Resource> newResources, Player player){
        for (Resource res : newResources) {
            if (res == player.getWhiteMarbleAbility().get(0))
                player.getResourceSupply().changeChoosable(res);
            else if (res == player.getWhiteMarbleAbility().get(1))
                player.getResourceSupply().changeChoosable(res);
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * this method check if the Resources sent by the player are valid
     * @param clientId is the id or the player
     * @param newRes is the list of String indicating the Resources wanted by the player
     */
    public void ChangeChoosable(int clientId, ArrayList<String> newRes){
        Player player=getPlayerInTurn();
        ArrayList<Resource> newResources=stringArrayToResArray(newRes);
        int num=Collections.frequency(player.getResourceSupply().viewResources(),Resource.CHOOSABLE);
        if(newResources.size()==num && checkChangeResource(newResources,player)) {
            server.getClientFromId().get(clientId).getClientHandler().send(new ResourceInSupplyRequest(player.getSimplifiedSupply()));
        } else{
            num=Collections.frequency(player.getResourceSupply().viewResources(),Resource.CHOOSABLE);
            getHandlerFromPlayer(clientId).send(new LobbyMessage("Action not valid"));
            getHandlerFromPlayer(clientId).send(new ChangeChoosableResourceRequest(num, resArrayToStringArray(player.getWhiteMarbleAbility()),
                    "You can choose between this Resources"));
        }
    }

    public boolean checkProduction(ArrayList<Integer> cardProd , ArrayList<String> personalProdIn, String personalProdOut, ArrayList<String> leadProdOut, int id) throws ActionAlreadySetException, ResourceNotValidException, CardNotOwnedByPlayerOrNotActiveException {
        //TODO cosa manda client, produzione personale e leader da controllare
        Player player=getPlayerInTurn();

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
            if(player.getPersonalBoard().checkResourcesForUsages(totalProdIn)) {
                player.setAction(Action.ACTIVATEPRODUCTION);
                player.getPersonalBoard().removeResources(totalProdIn);
                totalProdOut = takeAllProdOut(cardProd, stringArrayToResArray(personalProdIn), personalProdOut, leadProdOut, id);
                player.getPersonalBoard().getStrongBox().addInStrongbox(totalProdOut);
                getHandlerFromPlayer(id).send(new StrongboxChangeMessage(player.getPersonalBoard().getSimplifiedStrongbox()));
                getHandlerFromPlayer(id).send(new WareHouseChangeMessage(player.getPersonalBoard().getSimplifiedWarehouse()));
                return true;
            }

        }

        return false;
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
        Player player= getPlayerFromId(id);
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
                    if(requirementsLeadCheck(card,player)) {
                        player.activateAbility(card);
                        getHandlerFromPlayer(id).send(new CardIDChangeMessage(player.getCardsId()));
                    }else
                        getHandlerFromPlayer(id).send(new LobbyMessage("Missing requirements to activate this lead"));

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

    private boolean requirementsLeadCheck(LeadCard card, Player player ){
        System.out.println("dentro reqCheck");
        if(!card.getResources().isEmpty()) {
            System.out.println("!card.getResources().isEmpty()");
            if (player.getPersonalBoard().checkResourcesForUsages(card.getResources())) {
                player.getPersonalBoard().removeResources(card.getResources());
                return true;
            } else
                return false;
        }else{
            System.out.println("else debug");
            return cardsReqLeadCheck(card, player);

        }

    }

    private boolean cardsReqLeadCheck(LeadCard card, Player player){
        boolean result=false;
        ArrayList<DevCard> playerDev= player.getPersonalBoard().getDevCardSlot().getDevCards();
        if(card.getDevCardRequired().keySet().contains(1)) {
            System.out.println("if(card.getDevCardRequired().keySet().contains(1))");
            ArrayList<String> playerDevsColors = new ArrayList<>();
            playerDev.forEach(dev -> {
                String color = dev.getColor();
                playerDevsColors.add(color);
            });
            for (String color : card.getDevCardRequired().get(1)) {
                result = false;
                if (playerDevsColors.contains(color)) {
                    playerDevsColors.remove(color);
                    result = true;
                }
            }
        }else {
            for (DevCard dev : playerDev)
                if (dev.getColor().equals(card.getDevCardRequired().get(2).get(0)) && dev.getLevel() == 2)
                    result = true;
        }
        return result;
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

    /**
     * @param player is the player who gives away faith points
     * @param pointsToGive is the number of faith points to give away
     */
    public void faithPointsGiveAwayHandler(Player player, int pointsToGive){
        for (int i=0; i<pointsToGive;i++) {
            int meetingNumber = game.faithPointsGiveAway(player);
            lobby.getClientFromPosition().values().forEach(client->{
                int faithPosition=getPlayerFromId(client.getID()).getPersonalBoard().getFaithMarker().getFaithPosition();
                client.getClientHandler().send(new FaithPositionChangeMessage(faithPosition));
                    }
            );
            if (meetingNumber > 0 && meetingNumber < 4)
                lobby.sendAll(new ActivePopeMeetingMessage(meetingNumber));
        }
    }

    public void checkPositionOfResources(ArrayList<String>[] gameObj, int id){
        Player player=getPlayerFromId(id);
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
                faithPointsGiveAwayHandler(player,player.getResourceSupply().discardResources(allResources));
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

    /**
     * this method summarise the composition of the Player's Strongbox and send it to the player
     * @param p is the Player who need the info of his Strongbox
     */
    private void sendStrongboxInfo(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        System.out.println("mando strongbox");
        handler.send(new StrongboxChangeMessage(p.getPersonalBoard().getSimplifiedStrongbox()));
        System.out.println("strongbox mandato");
    }

    /**
     * this method pick the position of the Player's Faith marker and send it to the player
     * @param p is the Player who need the info of his Faith marker
     */
    private void sendFaithMarkerPosition(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        System.out.println("mando faith position");
        handler.send(new FaithPositionChangeMessage(p.getPersonalBoard().getFaithMarker().getFaithPosition()));
        System.out.println("faith position mandato");
    }

    /**
     * this method pick the id of the Player's cards and send them to the player
     * @param p is the Player who need the info of his cards
     */
    private void sendPlayerCardsInfo(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new CardIDChangeMessage(p.getCardsId()));
    }

    /**
     * this method summarise the composition of the Player's Warehouse and send it to the player
     * @param p is the Player who need the info of his Warehouse
     */
    private void sendWarehouseInfo(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new WareHouseChangeMessage(p.getPersonalBoard().getSimplifiedWarehouse()));
    }

    /**
     * this method summarise the composition of the Development cards matrix and send it to all players
     * @param p is the Player who need the info of the Development cards Matrix
     */
    private void sendDevCardMatrixInfo(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new DevMatrixChangeMessage(game.getSimplifiedDevMatrix()));
    }

    /**
     * this method summarise the composition of the Resource market and send it to all players
     * @param p is the Player who need the info of the Resource Market
     */
    private void sendMarketInfo(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new MarketChangeMessage(game.getSimplifiedMarket()));
    }

    public VirtualClient getActualPlayerTurn() {
        return actualPlayerTurn;
    }

    /**
     * this method handles the end of the Player's turn
     */
    public void turnUpdate() {
        if (!lobby.getClientFromPosition().values().isEmpty()) {
            String name = actualPlayerTurn.getNickName();
            Player player = game.getPlayerFromName(name);
            finishPlayerTurn(player);
            changeActualPlayerTurn();
            String s = game.draw();
            if (s.isEmpty()) {
                lobby.sendAll(new LobbyMessage("è il turno di " + server.getNameFromId().get(actualPlayerTurn.getID())));
            } else if (s.equalsIgnoreCase("finished")) {
                //TODO gestione fine gioco
            } else {
                if (lobby.playersOnline() > 0) {
                    if (s.contains("Lorenzo has discarded two development card of color ")) {
                        lobby.sendAll(new DevMatrixChangeMessage(game.getSimplifiedDevMatrix()));
                    }
                    lobby.sendAll(new LobbyMessage(s + ", è di nuovo il tuo turno"));
                }
            }
        }
    }

    /**
     * this method establish who is the next Player
     */
    private void changeActualPlayerTurn() {
        int actualIndex=lobby.getPositionFromClient().get(actualPlayerTurn);
        do {
            actualIndex=(actualIndex+1)%(game.getPlayers().size());
        }
        while(!lobby.getClientFromPosition().containsKey(actualIndex));
        actualPlayerTurn=lobby.getClientFromPosition().get(actualIndex);
    }

    /**
     * this method check if there is something not concluded and in case terminate it
     * @param player is the player who is terminating his turn
     */
    private void finishPlayerTurn(Player player) {
        player.resetAction();
        ArrayList<Resource> resources=player.getResourceSupply().viewResources();
        if(!resources.isEmpty()){
            faithPointsGiveAwayHandler(player,player.getResourceSupply().discardResources(resources));
        }
    }

    /**
     * this method insert the reconnected client in the correct position in game
     * @param id is the id of the client reconnected
     * @param name is the name of the client reconnected
     */
    public void insertPlayerInOrder(int id, String name) {
        ArrayList<String> names;
        names= (ArrayList<String>) game.getPlayers().stream().map(Player::getName).collect(Collectors.toList());
        int position= names.indexOf(name);
        lobby.getPositionFromClient().put(server.getClientFromId().get(id),position);
        lobby.getClientFromPosition().put(position,server.getClientFromId().get(id));
    }

    /**
     * this method send all the info of the game to the client reconnected based on the game phase
     * @param id is the id of the client reconnected
     */
    public void sendInfoAfterReconnection(int id) {
        if (lobby.getStateOfGame() == GameState.WAITING) {
            getHandlerFromPlayer(id).send(new LobbyMessage("Welcome back. " +
                    "We are waiting for other " + lobby.getSeatsAvailable() + " players"));
        } else if (lobby.getStateOfGame() == GameState.PREPARATION1) {
            if (leaderCardChosenYet(getPlayerFromId(id)))
                notifyLeadCardDistributed(server.getClientFromId().get(id));
            else
                getHandlerFromPlayer(id).send(new LobbyMessage("You have Chosen your Leader cards yet"));
        } else if (lobby.getStateOfGame() == GameState.PREPARATION2) {
            if (!checkPlayerStartingWarehouse(lobby.getPositionFromClient().get(server.getClientFromId().get(id)))) {
                askInitialResources(server.getClientFromId().get(id));
                checkPlayerInitialFaithMarker(lobby.getPositionFromClient().get(server.getClientFromId().get(id)));
            }else
                getHandlerFromPlayer(id).send(new LobbyMessage("You have Chosen your initial Resource yet"));
        } else if (lobby.getStateOfGame() == GameState.ONGOING) {
            Player p=getPlayerFromId(id);
            sendMarketInfo(p);
            sendDevCardMatrixInfo(p);
            sendWarehouseInfo(p);
            sendPlayerCardsInfo(p);
            sendFaithMarkerPosition(p);
            sendStrongboxInfo(p);
            getHandlerFromPlayer(id).send(new StartingGameMessage());
        }
    }
}

