package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.personalboard.*;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.*;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.*;

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

    /**
     * this boolean indicates if this round of Players' turn is the last one ore not
     */
    private boolean lastRound;

    public Controller(Lobby lobby, MainServer server) {
        this.lobby=lobby;
        this.server=server;
        this.lastRound=false;
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
        resources.forEach(res->allRes.add(Resource.valueOf(res.toUpperCase())));
        return allRes;
    }

    /**
     * this method creates the game,
     * if there is only one player in lobby it will be a Single player mode
     * otherwise a multi player mode
     */
    public void createGame(){
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
            server.quitter();
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
                sendBlackCrossInfo(p);
            }
        }
        lobby.sendAll(new StartingGameMessage());
    }

    /**
     * advise each Player of the Leader cards he received
     */
    private void notifyLeadCardDistributed(VirtualClient client){
        int position =lobby.getPositionFromClient().get(client);
        ArrayList<Integer> leaderId = new ArrayList<>();
        for (LeadCard card : game.getPlayers().get(position).getLeadCards())
            leaderId.add(card.getId());
        client.getClientHandler().send(new LeaderCardDistribution(leaderId, "Please choose 2 leader card to hold"));
    }

    /**
     * @param id is the id of the client who choose this cards
     * @param card1 is the id of the first Leader card
     * @param card2 is the id of the second Leader card
     */
    public void check2Leads(int id, int card1, int card2){
        Player player = getPlayerFromId(id);
        if(!player.leaderCardAlreadyChosen()) {
            if (player.checkLeadsIdChosen(card1,card2)){
                getHandlerFromPlayer(id).send(new LobbyMessage("Leader cards picked correctly"));
                player.choose2Leads(card1, card2);
                if (game.checkAllPlayersChooseLeads()) {
                    System.out.println("tutti hanno scelto le lead cards");
                    if(lobby.getPositionFromClient().size()>1 && lobby.playersOnline()>1) {
                        lobby.setStateOfGame(GameState.PREPARATION2);
                        for(VirtualClient client:lobby.getClientFromPosition().values())
                            askInitialResources(client);
                    }else
                        startGame();
                }
            }else {
                ArrayList<Integer> leaderId = player.getLeadCardsId();
                getHandlerFromPlayer(id).send(new LeaderCardDistribution(leaderId, "You choose not valid leader cards "));
            }
        }else{
            getHandlerFromPlayer(id).send(new LobbyMessage("You have chosen yours leader cards yet"));
        }
    }

    /**
     * if the game is a single player mode this method start the game,
     * otherwise advise the players that they can take some initial Resources
     * @param client is the client who has to choose the initial Resource
     */
    public void askInitialResources(VirtualClient client) {
        if (server.isClientOnline(client.getNickName())) {
            int position = lobby.getPositionFromClient().get(client);
            if (position == 0)
                getHandlerFromPlayerPosition(position).send(new LobbyMessage("Wait until other players have chosen initial resources"));
            else {
                int num = game.playerInitialResources(position);
                getHandlerFromPlayerPosition(position).send(new GetInitialResourcesAction("You can choose " + num + " initial resource", num));
                if (position >= 2) {
                    getHandlerFromPlayerPosition(position).send(new LobbyMessage("you will receive a Faith point also"));
                    getPlayerFromId(client.getID()).getPersonalBoard().getFaithMarker().updatePosition();
                }
            }
        }
    }



    /**
     * @return true if all the players have chosen the correct number of initial Resources
     * and have received the correct number of Faith points
     */
    public boolean checkInitialResources() {
        //System.out.println("sto controllando se gli altri giocatori hanno scelto le risorse");[Debug]
        boolean result=true;
        for (int position = 0; position < game.getPlayers().size(); position++) {
            String name= game.getPlayers().get(position).getName();
            //System.out.println("sto controllando "+name);[Debug]
            if (!checkPlayerStartingWarehouse(position)) {
                System.out.println(name+" non ha ancora scelto le risorse initiali");//[Debug]
                result=false;
            } else {
                System.out.println(name + " ha il giusto numero di risorse");//[Debug]
                checkPlayerInitialFaithMarker(position);
                System.out.println(name+": faithmarker ok");//[Debug]
            }
        }
        return result;
    }

    /**
     * @param position is the position of the player
     */
    private void checkPlayerInitialFaithMarker(int position) {
        //System.out.println("controllo il suo faithmarker");[Debug]
        Player player = getPlayerFromId(getHandlerFromPlayerPosition(position).getClientId());
        //System.out.println("mi sono salvato la sua posizione");[Debug]
        if (player.getPersonalBoard().getFaithMarker().getFaithPosition() != game.playerInitialFaithPoint(position)) {
            System.out.println("ha ricevuto uno sbagliato numero di faith points");//[Debug]
            player.getPersonalBoard().getFaithMarker().reset();
            //System.out.println("ho resettato il faithmarker");[Debug]
            while (player.getPersonalBoard().getFaithMarker().getFaithPosition() < game.playerInitialFaithPoint(position)) {
                System.out.println("gli sto assegnando un punto");//[Debug]
                player.getPersonalBoard().getFaithMarker().updatePosition();
            }
        }
    }

    /**
     * @param position is the position of the player
     * @return true if the Player has chosen the correct number of resources yet
     */
    private boolean checkPlayerStartingWarehouse(int position){
        //System.out.println("sto controllando il suo warehouse");[Debug]
        Player player=getPlayerFromId(getHandlerFromPlayerPosition(position).getClientId());
        ArrayList<Resource> resources=player.getWarehouseResources();
        ArrayList<String>[] warehouse=player.getPersonalBoard().getSimplifiedWarehouse();
        //System.out.println("mi sono salvato tutte le sue risorse");[Debug]
        if (resources.size()!= game.playerInitialResources(position) || !checkShelfContent(warehouse, player)) {
            if (resources.size()>0) {
                //System.out.println("ha scelto un numero sbagliato di risorse");[Debug]
                game.getPlayers().get(position).getPersonalBoard().getWarehouseDepots().clear();
                //System.out.println("ho pulito il suo warehouse");[Debug]
                if (position != 0 && server.isClientOnline(game.getPlayers().get(position).getName())) {
                    //System.out.println("gli chiedo di scegliere nuovamente");[Debug]
                    getHandlerFromPlayerPosition(position).send(new GetInitialResourcesAction("You have choose an incorrect number of resources, please resend your initial resources:  ", game.playerInitialResources(position)));
                }
            }return false;
        }else {
            return true;
        }
    }

    /**
     * @param id is the id of the client
     * @param shelves is the map of the Shelves chosen by the client
     * @param stringResources is the map of Resources that is to put on the shelf chosen
     */
    public void checkInsertResourcePosition(int id, Map<Integer, Integer> shelves, Map<Integer, String> stringResources) {
        Player player = getPlayerFromId(id);
        int playerPos=lobby.getPositionFromClient().get(server.getClientFromId().get(id));
        ArrayList<String> stringArray = new ArrayList<>(stringResources.values());
        ArrayList<Resource>  resources=stringArrayToResArray(stringArray);
        for (int i=0;i<resources.size();i++){
            int position=shelves.get(i);
            Resource resource=resources.get(i);
            Shelf shelf= player.getPersonalBoard().getWarehouseDepots().getShelves()[position];
            if(((shelf.isShelfAvailability()) && (resource.equals(shelf.getResourceType()))) || shelf.getSlots().isEmpty()) {
                player.getPersonalBoard().getWarehouseDepots().addInShelf(position, resource);
            }else {
                player.getPersonalBoard().getWarehouseDepots().clear();
                getHandlerFromPlayer(id).send(new GetInitialResourcesAction("You choose a not valid resource or shelf", game.playerInitialResources(playerPos)));
                return;
            }
        }
        getHandlerFromPlayer(id).send(new LobbyMessage("Resource put in the warehouse"));
        if(checkInitialResources()){
            System.out.println("tutti i giocatori hanno scelto le risorse iniziali");
            startGame();
        }
    }

    /**
     * @param cardId is the id of the Development card that the Player wishes to buy
     * @param position is the position where the Client wants to put the new card
     * @throws CardNotOnTableException if the Development card searched is not present in the upper level of the matrix
     * @throws ResourceNotValidException if the Player has not the required Resources to buy the card
     * @throws InvalidSlotException if the position chosen is not valid
     * @throws ActionAlreadySetException is the Player has already gone through with an action in this turn
     */
    public void checkBuy(int cardId, int position) throws CardNotOnTableException, ResourceNotValidException, InvalidSlotException, ActionAlreadySetException {
        Player player = getPlayerInTurn();
        if (player.checkActionAlreadyDone())
            throw new ActionAlreadySetException("You have already gone through with an action in this turn");
        else {
            if (checkLevelPosition(player, cardId, position)) {
                System.out.println("dentro dopo checkLevelPosition");
                DevCard cardToBuy = game.getDevDeckMatrix().findCardInMatrix(cardId);
                ArrayList<Resource> boardResources = new ArrayList<>(player.getStrongboxResources());
                boardResources.addAll(player.getWarehouseResources());
                boardResources.addAll(player.getSpecialShelfResources());
                ArrayList<Resource> requirements = getDevCardRequirements(player,cardToBuy);
                if (player.getPersonalBoard().checkResourcesForUsages(requirements, boardResources)) {
                    System.out.println("ha i requisiti");
                    player.setAction(Action.BUYCARD);
                    System.out.println("faccio buy");
                    game.getDevDeckMatrix().buyCard(cardToBuy);
                    System.out.println("finito buy, rimuovo risorse");
                    player.getPersonalBoard().removeResources(requirements);
                    System.out.println("risorse rimosse,metto carta");
                    player.getPersonalBoard().getDevCardSlot().overlap(cardToBuy, position);
                    System.out.println("carta messa");
                    if (player.getPersonalBoard().getDevCardSlot().getDevCards().size()==7){
                        lastRound=true;
                        lobby.sendAll(new LobbyMessage(player.getName()+" has bought the 7th Development card"));
                    }
                    System.out.println("invio info");
                    sendWarehouseInfo(player);
                    sendStrongboxInfo(player);
                    sendPlayerCardsInfo(player);
                    for (Player player1:game.getPlayers())
                        sendDevCardMatrixInfo(player1);
                    System.out.println("info inviate");
                } else
                    throw new ResourceNotValidException("The player does not have enough resources to go through with the action");
            } else
                getHandlerFromPlayer(player.getName()).send(new LobbyMessage("Card chosen not valid"));
        }
    }

    /**
     * @param player is the Player who wants to get buy the Development card
     * @param cardToBuy is the card the Player wats to buy
     * @return the ArrayList of resources required to by this card, considering the potential discount ability
     */
    private ArrayList<Resource> getDevCardRequirements(Player player, DevCard cardToBuy) {
        ArrayList<Resource> requirements=new ArrayList<>(cardToBuy.getRequirements());
        System.out.println("Inizio a scontare le risorse" + requirements);
        if (!player.getDiscountAbility().isEmpty()){
            requirements.remove(player.getDiscountAbility().get(0));
            System.out.println("Risorsa scontata: " + requirements);
            if (player.getDiscountAbility().size()>1)
                requirements.remove(player.getDiscountAbility().get(1));
        }
        return requirements;
    }

    /**
     * @param player is the Player who want to buy this Development card
     * @param cardId is the id of the desired Development card
     * @param position is the number of the slot where the Player wish to put the new Development card
     * @throws InvalidSlotException if the position chosen is negative or >2
     * @return true if the position chosen is valid, false otherwise
     */
    private boolean checkLevelPosition(Player player, int cardId, int position) throws InvalidSlotException {
        if (position>=0 && position<=2) {
            DevCard wantedCard;
            System.out.println("posizione valida");
            try {
                wantedCard = game.getDevDeck().getCardFromId(cardId);
            } catch (CardChosenNotValidException e) {
                return false;
            }
            int newCardLevel = wantedCard.getLevel();
            if (newCardLevel == 1) {
                if(!player.getPersonalBoard().getDevCardSlot().getSlot()[position].isEmpty()){
                    return false;
                }
                System.out.println("carta livello 1");
                System.out.println(player.getPersonalBoard().getDevCardSlot().getActiveCards().size());
                return player.getPersonalBoard().getDevCardSlot().getActiveCards().size() < 3;
            } else {
                System.out.println("carta livello>1");
                System.out.println(player.getPersonalBoard().getDevCardSlot().getSlot()[position]);
                int lastIndex = player.getPersonalBoard().getDevCardSlot().getSlot()[position].size() - 1;
                System.out.println(lastIndex);
                if (lastIndex>=0){
                    System.out.println("nello slot ci sono altre carte");
                    DevCard card = player.getPersonalBoard().getDevCardSlot().getSlot()[position].get(lastIndex);
                    return card.getLevel() == newCardLevel - 1;
                } else return false;
            }
        }
        else
            throw new InvalidSlotException("Slot chosen not valid");
    }

    /**
     * @param index is the index of the market wanted by the Player
     * @param id is the id of the Player
     * @throws ActionAlreadySetException if the Player has done an action in this turn already
     * @throws NotAcceptableSelectorException if the Player has chosen a selector <0 or >6
     */
    public int checkMarket(int index, int id) throws ActionAlreadySetException, NotAcceptableSelectorException {
        Player player=getPlayerInTurn();
        if (player.checkActionAlreadyDone()){
            throw new ActionAlreadySetException("You have already gone through with an action in this turn");
        } else if(index <0 || index >6) {
            throw new NotAcceptableSelectorException("Index out of range : " + index);
        } else {
            System.out.println("setto lobby su market");
            lobby.setStateOfGame(GameState.MARKET);
            player.setAction(Action.TAKEFROMMARKET);
            game.getMarket().buyResources(index, player);
            faithMarkerUpdateHandler(player);
            for (Player player1:game.getPlayers())
                sendMarketInfo(player1);
            ArrayList<String> resSupply = player.getSimplifiedSupply();
            int num=Collections.frequency(resSupply,"CHOOSABLE");
            if(num>0) {
                getHandlerFromPlayer(id).send(new ChangeChoosableResourceRequest(num, resArrayToStringArray(player.getWhiteMarbleAbility()),
                        "You can choose between this Resources"));
            }else {
                getHandlerFromPlayer(id).send(new ResourceInSupplyRequest(resSupply));
            }
            return num;
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
    public void checkChangeChoosable(int clientId, ArrayList<String> newRes){
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

    /**
     * @param cardProd is the ArrayList of the id of the card chosen for productions
     * @param personalProdIn is the ArrayList of Resources chosen for the personal production
     * @param personalProdOut is the Resource chosen as product of the personal production
     * @param leadProdOut is the ArrayList of Resources chosen as products of leader productions
     * @return true if the productions are done correctly, false otherwise
     * @throws ActionAlreadySetException if the player has done a main action yet
     * @throws ResourceNotValidException if the Resources chosen are not valid
     * @throws CardNotOwnedByPlayerOrNotActiveException if the Player has chosen a card not owned by him
     */
    public boolean checkProduction(ArrayList<Integer> cardProd , ArrayList<String> personalProdIn, String personalProdOut, ArrayList<String> leadProdOut) throws ActionAlreadySetException {
        Player player=getPlayerInTurn();
        if (player.checkActionAlreadyDone())
            throw new ActionAlreadySetException("The player has already gone through with an action in their turn");
        else if (checkOwnerCards(cardProd,player)) {
            System.err.println("check prod dopo checkOwner");
            ArrayList<Resource> totalProdIn = null;
            try {
                totalProdIn = takeAllProdIn(cardProd, stringArrayToResArray(personalProdIn), player);
            } catch (ResourceNotValidException e) {
                getHandlerFromPlayer(player.getName()).send(new LobbyMessage(e.getMessage()));
                return false;
            }
            System.err.println("presi tutti prodIn: "+totalProdIn);
            ArrayList<Resource> playersResources =new ArrayList<>(player.getWarehouseResources());
            System.err.println("preso warehouse");
            playersResources.addAll(player.getSpecialShelfResources());

            if(player.getPersonalBoard().checkResourcesForUsages(totalProdIn, playersResources)) {
                System.err.println("ha risorse per avere prodout");
                player.setAction(Action.ACTIVATEPRODUCTION);
                player.getPersonalBoard().removeResources(totalProdIn);
                System.err.println("ho rimosso risorse prodin");
                ArrayList<Resource> totalProdOut = null;
                try {
                    totalProdOut = takeAllProdOut(cardProd, stringArrayToResArray(personalProdIn), personalProdOut, leadProdOut, player);
                } catch (ResourceNotValidException e) {
                    getHandlerFromPlayer(player.getName()).send(new LobbyMessage(e.getMessage()));
                    return false;
                }
                System.err.println("ho preso prodout");
                player.getPersonalBoard().getStrongBox().addInStrongbox(totalProdOut);
                System.err.println("gli ho messo prodout");
                sendStrongboxInfo(player);
                sendWarehouseInfo(player);
                System.err.println("ho madato info produzione");
                return true;
            }else
                getHandlerFromPlayer(player.getName()).send(new LobbyMessage("You do not have enough resources for the production"));
        }
        return false;
    }

    /**
     * @param cardProd is the ArrayList containing the id of the cards chosen by the Player
     * @param personalProdIn is the ArrayList containing the Resources chosen by the Player to do the personal Production
     * @param player is the Player who wants to do this productions
     * @return an ArrayList containing all the Resources required to do the chosen production
     */
    private ArrayList<Resource> takeAllProdIn( ArrayList<Integer> cardProd ,ArrayList<Resource> personalProdIn, Player player) throws ResourceNotValidException {
        ArrayList<Resource> totalProdIn = new ArrayList<>();
        System.err.println("devo prendere dev giocatore");
        ArrayList<DevCard> prodDevs = new ArrayList<>(player.getPersonalBoard().getDevCardSlot().getDevCards());
        ArrayList<LeadCard> prodLeads = new ArrayList<>(player.getLeadCards());
        if(!cardProd.isEmpty()) {
            if (!prodDevs.isEmpty()) {
                prodDevs.removeIf(card -> !cardProd.contains(card.getId()));
                prodDevs.forEach(card -> {
                    ArrayList<Resource> prodIn = card.getProdIn();
                    totalProdIn.addAll(prodIn);
                });
            }
            if (!prodLeads.isEmpty()) {
                prodLeads.removeIf(card -> !cardProd.contains(card.getId()) && !(card.getAbility() instanceof LeadAbilityProduction) && !card.isActive());

                prodLeads.forEach(card -> {
                    Resource prodIn = card.getAbility().getAbilityResource();
                    totalProdIn.add(prodIn);
                });
            }
        }
            if (!personalProdIn.isEmpty()) {
                if (personalProdIn.size() == 1)
                    throw new ResourceNotValidException("You need 2 resources as input for the personal production");
                totalProdIn.addAll(personalProdIn);
            }

        return totalProdIn;
    }

    /**
     * @param cardProd is the ArrayList containing the id of the cards chosen by the Player
     * @param personalProdIn is the ArrayList containing the Resources chosen by the Player to do the personal Production
     * @param personalProdOut is the String representing the Resources chosen by the Player as result of the personal Production
     * @param leadProdOut is the ArrayList containing the Resources generated by the Leader Productions
     * @param player is the Player who wants to do this productions
     * @return an ArrayList containing all the Resources produced by the Production chosen by the Player
     */
    private ArrayList<Resource> takeAllProdOut(ArrayList<Integer> cardProd ,ArrayList<Resource> personalProdIn, String personalProdOut,ArrayList<String> leadProdOut, Player player) throws ResourceNotValidException{
        ArrayList<Resource> totalProdOut = new ArrayList<>();
        ArrayList<DevCard> prodDevs = new ArrayList<>(player.getPersonalBoard().getDevCardSlot().getDevCards());
        if(!prodDevs.isEmpty())
            prodDevs.removeIf(card->!cardProd.contains(card.getId()));
        ArrayList<LeadCard> prodLeads = new ArrayList<>(player.getLeadCards());
        if(!prodLeads.isEmpty()) {
            prodLeads.removeIf(card -> !cardProd.contains(card.getId()) && !(card.getAbility() instanceof LeadAbilityProduction) && !card.isActive());
            System.err.println("prodLeads: " + prodLeads);
        }
        if(!personalProdIn.isEmpty()) {
            if (!personalProdOut.equals(""))
                try {
                    totalProdOut.add(Resource.valueOf(personalProdOut.toUpperCase()));
                }catch (IllegalArgumentException e){
                    throw new ResourceNotValidException("Resource chosen for personal production not valid");
                }
            else
                throw new ResourceNotValidException("Resource chosen for personal production not valid");
        }else
            if(!personalProdOut.equals("")) {
                throw new ResourceNotValidException("Resource chosen in input for personal production not valid");
            }
        int numOfLead = prodLeads.size();
        System.err.println("leadProdOut size: "+ leadProdOut.size());
        if(numOfLead ==leadProdOut.size()){
            leadProdOut.forEach(resource -> totalProdOut.add(Resource.valueOf(resource.toUpperCase())));
            player.getPersonalBoard().getFaithMarker().updatePosition();
            faithMarkerUpdateHandler(player);
        }else
            throw new ResourceNotValidException("Number of Resources chosen for leader production not valid");
        prodDevs.forEach(card -> {
                totalProdOut.addAll(card.getProdOut());
                for (int faithPoint = card.getFaithPoint(); faithPoint >0; faithPoint--) {
                    player.getPersonalBoard().getFaithMarker().updatePosition();
                    faithMarkerUpdateHandler(player);
                }
        });


        return totalProdOut;
    }

    /**
     * @param cardsId is an ArrayList containing the id of the cards chosen by Player
     * @param player is the Player who choose this cards
     * @return true if the Player own all the cards he choose and they are all active
     */
    private boolean checkOwnerCards(ArrayList<Integer> cardsId,Player player){
        ArrayList<Integer> playerCards= new ArrayList<>();
        player.getPersonalBoard().getDevCardSlot().getDevCards().forEach(card->{int id=card.getId();playerCards.add(id);});
        player.getLeadCards().forEach(card->{int id=card.getId();
            if(card.isActive()&&(card.getAbility() instanceof LeadAbilityProduction))
                    playerCards.add(id);});

        return playerCards.containsAll(cardsId);
    }

    /**
     * this method handles the activation action of a Leader card
     * @param cardId is the Leader card id that the player wants to activate
     * @param clientId is the id of the Client
     */
    public void checkLeadActivation(int cardId, int clientId) {
        Player player= getPlayerFromId(clientId);
        if (cardId < 48 || cardId > 64) {
            getHandlerFromPlayer(clientId).send(new LobbyMessage("LeadCard Id not valid"));
        } else {
            try {
                LeadCard card = player.getLeadCardFromId(cardId);
                if (card.isActive()) {
                    getHandlerFromPlayer(clientId).send(new LobbyMessage("This leadCard is already active"));
                } else {
                    if(requirementsLeadCheck(card,player)) {
                        player.activateAbility(card);
                        sendPlayerCardsInfo(player);
                        if(card.getAbility() instanceof LeadAbilityShelf){
                            System.out.println("mando lo shelfabilityactivemessage");
                            getHandlerFromPlayer(clientId).send(new ShelfAbilityActiveMessage(cardId));
                        }
                    }else
                        getHandlerFromPlayer(clientId).send(new LobbyMessage("Missing requirements to activate this lead"));
                }
            } catch (CardChosenNotValidException e) {
                getHandlerFromPlayer(clientId).send(new LobbyMessage(e.getMessage()));
            }
        }
    }

    /**
     * this method handles the discard action of a Leader card and give a points to all other players
     * @param cardId is the Leader card id that the player wants to discard
     * @param clientId is the id of the Client
     */
    public void checkDiscardLead(int cardId, int clientId) {
        Player player=getPlayerFromId(clientId);
        if (cardId < 48 || cardId > 64) {
            getHandlerFromPlayer(clientId).send(new LobbyMessage("LeadCard id not valid"));
        } else {
            try {
                LeadCard card = player.getLeadCardFromId(cardId);
                if (card.isActive()) {
                    getHandlerFromPlayer(clientId).send(new LobbyMessage("This leadCard is already active"));
                } else {
                    player.discardLead(card);
                    sendPlayerCardsInfo(player);
                    player.getPersonalBoard().getFaithMarker().updatePosition();
                    faithMarkerUpdateHandler(player);
                }
            } catch (CardChosenNotValidException e) {
                getHandlerFromPlayer(clientId).send(new LobbyMessage(e.getMessage()));
            }
        }
    }

    /**
     * @param card is the Leader card that the Player wants to activate
     * @param player is the Player who wants to activate the Leader card
     * @return true if the Player has the Developments cards or the Resources required to activate the Leader card
     */
    private boolean requirementsLeadCheck(LeadCard card, Player player ){
        if(!card.getResources().isEmpty()) {
            ArrayList<Resource> playersResources =new ArrayList<>(player.getStrongboxResources());
            playersResources.addAll(player.getWarehouseResources());
            playersResources.addAll(player.getSpecialShelfResources());
            if (player.getPersonalBoard().checkResourcesForUsages(card.getResources(), playersResources)) {
                return true;
            } else
                return false;
        }else{
            return cardsReqLeadCheck(card, player);
        }
    }

    /**
     * @param card is the Leader card that the Player wants to activate
     * @param player is the Player who wants to activate the Leader card
     * @return true if the player has the Development cards required to activate the Leader cards
     */
    private boolean cardsReqLeadCheck(LeadCard card, Player player){
        ArrayList<DevCard> playerDev= player.getPersonalBoard().getDevCardSlot().getDevCards();
        if(card.getDevCardRequired().containsKey(1)) {
            ArrayList<String> playerDevsColors = new ArrayList<>();
            playerDev.forEach(dev -> playerDevsColors.add(dev.getColor()));
            for (String color : card.getDevCardRequired().get(1)) {
                if (playerDevsColors.contains(color))
                    playerDevsColors.remove(color);
                else
                    return false;
            }
            return true;
        }else {
            for (DevCard dev : playerDev) {
                if (dev.getColor().equals(card.getDevCardRequired().get(2).get(0)) && dev.getLevel() == 2)
                    return true;
            }
            return false;
        }
    }

    /**
     * @param specialRes is the ArrayList of the Resources put by th Player in the Special Shelves
     * @param player is the player to whom this method checks the Special Shelves
     * @return true if the Resources are put in a valid position
     */
    private boolean checkSpecialShelf(ArrayList<Resource> specialRes, Player player) {
        System.out.println("sono dentro checkspecialshelf");
        boolean result= false;
        if (specialRes.size() > 2) {
            System.out.println("ho messo più di 2 risorse nella special");
            getHandlerFromPlayer(player.getName()).send(new LobbyMessage("Too many resources for the special shelf"));
        } else if (specialRes.size()>1 && !specialRes.get(0).equals(specialRes.get(1))){
            System.out.println("ho messo 2 risorse diverse nella special");
            getHandlerFromPlayer(player.getName()).send(new LobbyMessage("Two different resources cannot be in the same special shelf"));
        }else {
            System.out.println("risorse valide in eventuale specialshelf");
            SpecialShelf shelf;
            for (int i = 0; i < 2; i++) {
                if (player.getPersonalBoard().getSpecialShelves().size()>i &&
                        player.getPersonalBoard().getSpecialShelves().get(i).isPresent()) {
                    System.out.println("sono nella special shelf");
                    shelf = player.getPersonalBoard().getSpecialShelves().get(i).get();
                    System.out.println("ho preso la special shelf");
                    if (shelf.getResourceType().equals(specialRes.get(0))) {
                        //shelf.getSpecialSlots().clear();
                        //shelf.getSpecialSlots().addAll(specialRes);
                        System.out.println("posso mettere le res nella special shelf");
                        result = true;
                    }else
                        System.out.println("special shelf di tipo diverso come risorse");
                }else if(i==0) {
                    System.out.println("non ho special shelves");
                    getHandlerFromPlayer(player.getName()).send(new LobbyMessage("The special shelves are not active"));
                }else if(!result){
                    System.out.println("la II shelf non è disponibile");
                    getHandlerFromPlayer(player.getName()).send(new LobbyMessage("The shelf requested is not active"));
                }
            }
        }

        return result;
    }

    /**
     * @param player is the player who gives away faith points
     * @param pointsToGive is the number of faith points to give away
     */
    public void faithPointsGiveAwayHandler(Player player, int pointsToGive){
        System.out.println("devo donare "+pointsToGive+" punti");
        int i=1;
        while (pointsToGive>0) {
            System.out.println("punto: "+i);
            game.faithPointsGiveAway(player);
            sendBlackCrossInfo(player);
            game.getPlayers().forEach(this::faithMarkerUpdateHandler);
            pointsToGive--;
            i++;
        }
    }

    /**
     * @param player is the Player whose Faith marker is updated
     */
    private void faithMarkerUpdateHandler(Player player) {
        sendFaithMarkerPosition(player);
        int popeMeeting=game.activePopeSpace(player);
        System.out.println("popo meeting "+popeMeeting);
        if (popeMeeting>0 && popeMeeting<4) {
            for (Player p: game.getPlayers()) {
                if (p.getPersonalBoard().getFaithMarker().isVaticanZone(popeMeeting)&&server.isClientOnline(p.getName()))
                    getHandlerFromPlayer(p.getName()).send(new ActivePopeMeetingMessage(popeMeeting));
            }
            if (popeMeeting==3 && lobby.playersOnline()>0) {
                lastRound = true;
                lobby.sendAll(new LobbyMessage("A Player has reached the end of the faith Track"));
            }
        }
    }

    /**
     * this method check if the Warehouse sent by the Player has all the correct Resources and in a valid disposition
     * @param newWarehouse is the Warehouse disposition send by client
     * @param id is the client's id
     */
    public void checkPositionOfResources(ArrayList<String>[] newWarehouse, int id){
        Player player=getPlayerFromId(id);
        if (newWarehouse.length <= 5){
            if(checkShelfContent(newWarehouse,player)){
                System.out.println("nuovo wharehouse ben fatto");
                ArrayList<Resource> remainingRes= null;
                try {
                    remainingRes = checkWarehouseDimension(newWarehouse, player);

                    System.out.println(remainingRes);
                    if (!remainingRes.isEmpty()) {
                        System.out.println("ci sono risorse da scartare");
                        faithPointsGiveAwayHandler(player, player.getResourceSupply().discardResources(remainingRes));
                        System.out.println("risorse scartate");
                        System.out.println(player.getSupplyResources());
                    }
                    player.getResourceSupply().emptySupply();
                    System.out.println(player.getSupplyResources());
                    setWarehouseNewDisposition(newWarehouse, player);
                    getHandlerFromPlayer(id).send(new WareHouseChangeMessage(player.getPersonalBoard().getSimplifiedWarehouse()));
                    lobby.setStateOfGame(GameState.ONGOING);
                } catch (ResourceNotValidException e) {
                    System.out.println(e.getMessage());
                    sendWarehouseInfo(player);
                    getHandlerFromPlayer(id).send(new ResourceInSupplyRequest(player.getSimplifiedSupply()));
                    getHandlerFromPlayer(id).send(new LobbyMessage(e.getMessage()));
                }
            }else{
                sendWarehouseInfo(player);
                getHandlerFromPlayer(id).send(new ResourceInSupplyRequest(player.getSimplifiedSupply()));
                getHandlerFromPlayer(id).send(new LobbyMessage("Resources not valid in this disposition, please retry"));
            }
        }
    }

    /**
     * @param newWarehouse is the new Warehouse disposition send by the client
     * @param player is the Player who send the disposition
     * @return an Arraylist of the Resources remained in the Player's Supply
     */
    private ArrayList<Resource> checkWarehouseDimension(ArrayList<String>[] newWarehouse, Player player) throws ResourceNotValidException {
        ArrayList<Resource> allResources = new ArrayList<>(player.getSupplyResources());
        allResources.addAll(player.getWarehouseResources());
        allResources.addAll(player.getSpecialShelfResources());
        System.out.println("mi sono salvato tutte le risorse del tizio");
        System.out.println(allResources);
        ArrayList<Resource> newRes= new ArrayList<>();
        for (int k=0;k<5;k++)
            newRes.addAll(stringArrayToResArray(newWarehouse[k]));
        System.out.println("mi sono salvato tutte le risorse del messaggio");
        System.out.println(newRes);
        for (int i=0;i<allResources.size();i++){
            System.out.println("risorsa "+i+" su dim: "+allResources.size());
            System.out.println("cerco risorsa "+allResources.get(i));
            for (int j=0;j<newRes.size();j++){
                if (newRes.get(j).equals(allResources.get(i))){
                    System.out.println("risorsa trovata");
                    allResources.remove(i);
                    newRes.remove(j);
                    i=-1;
                    break;
                }
            }
        }
        System.out.println("allResources:");
        System.out.println(allResources);
        System.out.println("newResources:");
        System.out.println(newRes);
        if(newRes.size()>0) {
            System.out.println("ha scelto troppe risorse");
            throw new ResourceNotValidException("you have chosen not valid resources");
        }
        return allResources;
    }

    /**
     * @param newWarehouse is the new warehouse disposition send by client
     * @param player is the Player who send the disposition
     */
    private void setWarehouseNewDisposition(ArrayList<String>[] newWarehouse, Player player) {
        for(int i=0;i<3;i++) {
            if(newWarehouse[i].isEmpty()) {
                player.getPersonalBoard().getWarehouseDepots().getShelves()[i].removeAllRes();
            }else {
                player.getPersonalBoard().getWarehouseDepots().getShelves()[i].removeAllRes();
                player.getPersonalBoard().getWarehouseDepots().addInShelf(i, stringArrayToResArray(newWarehouse[i]));
            }
        }
        if(!player.getPersonalBoard().getSpecialShelves().isEmpty()){
            for(int i=3;i<5;i++) {
                if(player.getPersonalBoard().getSpecialShelves().size()>(i-3) &&
                        player.getPersonalBoard().getSpecialShelves().get(i-3).isPresent()) {
                    Resource resource = player.getPersonalBoard().getSpecialShelves().get(i - 3).get().getResourceType();
                    player.getPersonalBoard().getSpecialShelves().remove(i - 3);
                    player.getPersonalBoard().getSpecialShelves().add(i - 3, Optional.of(new SpecialShelf(resource)));
                    if (!newWarehouse[i].isEmpty()) {
                        int finalI = i;
                        newWarehouse[i].forEach(res->player.getPersonalBoard().getSpecialShelves().get(finalI -3).get().addResources(Resource.valueOf(res.toUpperCase())));
                    }
                }
            }
        }
    }

    /**
     * @param newWarehouse is the new Warehouse disposition send by client
     * @param player is the Player who send the disposition
     * @return true if the disposition is valid, false otherwise
     */
    private boolean checkShelfContent(ArrayList<String>[] newWarehouse, Player player) {
        System.out.println("sono dentro checkShelfContent");
        for(int i=0; i<3;i++) {
            if (newWarehouse[i].size() <= i + 1) {
                System.out.println("dimensione dell shelf corretta "+i);
                for (int j = 0; j < newWarehouse[i].size() -1; j++) {
                    if (!newWarehouse[i].get(j).equals(newWarehouse[i].get(j + 1))) {
                        System.out.println("risorse shelf non stesso tipo");
                        return false;
                    }
                }
                System.out.println("risorse shelf dello stesso tipo");
            } else
                return false;
        }
        System.out.println("sono fuori dal for");
        if((!newWarehouse[0].isEmpty()&&!newWarehouse[1].isEmpty()&&newWarehouse[0].get(0).equals(newWarehouse[1].get(0)))
                ||(!newWarehouse[0].isEmpty()&&!newWarehouse[2].isEmpty()&&newWarehouse[0].get(0).equals(newWarehouse[2].get(0)))
                ||(!newWarehouse[1].isEmpty()&&!newWarehouse[2].isEmpty()&&newWarehouse[1].get(0).equals(newWarehouse[2].get(0)))) {
            System.out.println("ci sono risorse di stesso tipo su shelf diversi");
            return false;
        }
        System.out.println("sono prima if specialshelf 4 in check");
        if(!newWarehouse[3].isEmpty()) {
            System.out.println("dentro !new[3]");
            if (!checkSpecialShelf(stringArrayToResArray(newWarehouse[3]), player))
                return false;
        }
        System.out.println("sono prima if specialshelf 4 in check");
        if(!newWarehouse[4].isEmpty()){
            System.out.println("dentro !new[4]");
            return checkSpecialShelf(stringArrayToResArray(newWarehouse[4]), player);
        }
        return true;
    }

    /**
     * this method summarise the composition of the Player's Strongbox and send it to the player
     * @param p is the Player who need the info of his Strongbox
     */
    private void sendStrongboxInfo(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        //System.out.println("mando strongbox");[Debug]
        handler.send(new StrongboxChangeMessage(p.getPersonalBoard().getSimplifiedStrongbox()));
        //System.out.println("strongbox mandato");[Debug]
    }

    /**
     * this method pick the position of the Player's Faith marker and send it to the player
     * @param p is the Player who need the info of his Faith marker
     */
    private void sendFaithMarkerPosition(Player p) {
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        //System.out.println("mando faith position");[Debug]
        handler.send(new FaithPositionChangeMessage(p.getPersonalBoard().getFaithMarker().getFaithPosition()));
        //System.out.println("faith position mandato");[Debug]
    }

    /**
     * this method pick the position of Lorenzo's black cross and send it to the player
     * @param p is the Player who need the info of the Lorenzo's black cross
     */
    private void sendBlackCrossInfo(Player p) {
        System.out.println("mando un lorenzo message");
        getHandlerFromPlayer(p.getName()).send(new LorenzoActionMessage(game.getBlackCrossPosition()));
    }

    /**
     * this method pick the id of the Player's cards and send them to the player
     * @param p is the Player who need the info of his cards
     */
    private void sendPlayerCardsInfo(Player p) {
       // System.out.println("mando le carte del giocatore");[Debug]
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new CardIDChangeMessage(p.getCardsId(),p.getCardsPosition()));
    }

    /**
     * this method summarise the composition of the Player's Warehouse and send it to the player
     * @param p is the Player who need the info of his Warehouse
     */
    private void sendWarehouseInfo(Player p) {
        //System.out.println("mando il warehouse");[Debug]
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new WareHouseChangeMessage(p.getPersonalBoard().getSimplifiedWarehouse()));
    }

    /**
     * this method summarise the composition of the Development cards matrix and send it to all players
     * @param p is the Player who need the info of the Development cards Matrix
     */
    private void sendDevCardMatrixInfo(Player p) {
        //System.out.println("mando le info della matrice di dev card");[Debug]
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new DevMatrixChangeMessage(game.getSimplifiedDevMatrix()));
    }

    /**
     * this method summarise the composition of the Resource market and send it to all players
     * @param p is the Player who need the info of the Resource Market
     */
    private void sendMarketInfo(Player p) {
        //System.out.println("mando le info del market");[Debug]
        ClientHandler handler=getHandlerFromPlayer(p.getName());
        handler.send(new MarketChangeMessage(game.getSimplifiedMarket()));
    }

    public VirtualClient getActualPlayerTurn() {
        return actualPlayerTurn;
    }

    /**
     * this method handles the end of the Player's turn
     */
    public void turnUpdate()  {
        if (!lobby.getClientFromPosition().values().isEmpty()) {
            String name = actualPlayerTurn.getNickName();
            Player player = game.getPlayerFromName(name);
            try {
                finishPlayerTurn(player);
            } catch (ActionNotDoneException e) {
                getHandlerFromPlayer(player.getName()).send(new LobbyMessage(e.getMessage()));
                return;
            }
            System.out.println("fuori dal try");
            String message = game.draw();
            System.out.println(message+"a");
            if (lobby.playersOnline() > 0) {
                if (message.isEmpty()) {
                    lobby.sendAll(new LobbyMessage("Now it's the turn of " + server.getNameFromId().get(actualPlayerTurn.getID())));
                } else if (message.equalsIgnoreCase("Finished")) {
                    lastRound=true;
                } else {
                    sendDevCardMatrixInfo(player);
                    sendBlackCrossInfo(player);
                    lobby.sendAll(new LobbyMessage(message + ", it's again your turn"));
                }
            }
            System.out.println("faccio il changeActualPlayerTurn");
            changeActualPlayerTurn();
        }
    }

    /**
     * this method establish who is the next Player
     */
    private void changeActualPlayerTurn() {
        int actualIndex=lobby.getPositionFromClient().get(actualPlayerTurn);
        if (lastRound && actualIndex==lobby.getPositionFromClient().size()-1){
            endGame();
        }else {
            do {
                actualIndex = (actualIndex + 1) % (game.getPlayers().size());
            }
            while (!lobby.getClientFromPosition().containsKey(actualIndex));
            actualPlayerTurn = lobby.getClientFromPosition().get(actualIndex);
        }
    }

    /**
     * this method handles the end of the game sending to the Players the name of the winner
     */
    private void endGame() {
        System.out.println("sono in endgame");
        lobby.setStateOfGame(GameState.ENDED);
        String winnerName=game.getWinner();
        System.out.println("The winner is "+winnerName);
        lobby.sendAll(new WinnerMessage(winnerName+" is the winner!!"));
        System.out.println("ho mandato il messaggio");
    }

    /**
     * this method check if there is something not concluded and in case terminate it
     * @param player is the player who is terminating his turn
     */
    private void finishPlayerTurn(Player player) throws ActionNotDoneException {
        if(!player.checkActionAlreadyDone())
            throw new ActionNotDoneException("Main Action not chosen, cannot end turn");
        player.resetAction();
        ArrayList<Resource> resources=player.getResourceSupply().viewResources();
        if(!resources.isEmpty()){
            System.out.println("ci sono ancora risorse nel supply");
            System.out.println(resources);
            faithPointsGiveAwayHandler(player,player.getResourceSupply().discardResources(resources));
        }
    }

    /**
     * this method insert the reconnected client in the correct position in game
     * @param id is the id of the client reconnected
     * @param name is the name of the client reconnected
     */
    public void insertPlayerInOrder(int id, String name) {
        ArrayList<String> names=new ArrayList<>();
        for (Player player:game.getPlayers())
            names.add(player.getName());
        System.out.println("mi sono salvato i nomi dei giocatori");
        int position= names.indexOf(name);
        lobby.getPositionFromClient().put(server.getClientFromId().get(id),position);
        lobby.getClientFromPosition().put(position,server.getClientFromId().get(id));
        System.out.println("ho inserito il giocatore");
    }

    /**
     * this method send all the info of the game to the client reconnected based on the game phase
     * @param id is the id of the client reconnected
     */
    public void sendInfoAfterReconnection(int id) {
        if (lobby.getStateOfGame() == GameState.PREPARATION1) {
            //System.out.println("il giocatori stanno scegliendo le Leads");[Debug]
            if (getPlayerFromId(id).leaderCardAlreadyChosen())
                notifyLeadCardDistributed(server.getClientFromId().get(id));
            else
                getHandlerFromPlayer(id).send(new WaitingRoomAction("You have Chosen your Leader cards yet"));
        } else if (lobby.getStateOfGame() == GameState.PREPARATION2) {
            //System.out.println(" i giocatori stanno scegliendo le risorse iniziali");[Debug]
            if (!checkPlayerStartingWarehouse(lobby.getPositionFromClient().get(server.getClientFromId().get(id)))) {
                askInitialResources(server.getClientFromId().get(id));
                checkPlayerInitialFaithMarker(lobby.getPositionFromClient().get(server.getClientFromId().get(id)));
            } else
                getHandlerFromPlayer(id).send(new WaitingRoomAction("You have Chosen your Leader cards yet"));
        } else if (lobby.getStateOfGame() == GameState.ONGOING) {
            //System.out.println("i giocatori stanno giocando");[Debug]
            Player p = getPlayerFromId(id);
            sendMarketInfo(p);
            sendDevCardMatrixInfo(p);
            sendWarehouseInfo(p);
            sendPlayerCardsInfo(p);
            sendFaithMarkerPosition(p);
            sendBlackCrossInfo(p);
            sendStrongboxInfo(p);
            getHandlerFromPlayer(id).send(new StartingGameMessage());
        }
    }

    /**
     * this method set a
     * @param id is the id of the Player who is disconnecting
     */
    public void actionForDisconnection(int id) {
        getPlayerFromId(id).setAction(Action.ACTIVATEPRODUCTION);
        turnUpdate();
    }


}

