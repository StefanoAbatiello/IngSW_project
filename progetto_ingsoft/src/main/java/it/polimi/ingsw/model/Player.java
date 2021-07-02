package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.ResourceSupply;
import it.polimi.ingsw.model.cards.DevCard;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.personalboard.PersonalBoard;
import java.util.*;
import java.util.stream.Collectors;

public class Player {

    /**
     * this is the player's Personal board
     */
    private final PersonalBoard personalBoard;

    /**
     * this are the points gained from the position of Faith marker
     */
    private int popeMeetingPoints =0;

    /**
     * this is the list of leader card
     */
    private ArrayList<LeadCard> leadCards=new ArrayList<>();

    /**
     * this is an array of the kind of Resources related to the production ability
     */
    private final ArrayList<Resource> productionAbility=new ArrayList<>();

    /**
     * this is an array of the kind of Resources related to the discount ability
     */
    private final ArrayList<Resource> discountAbility=new ArrayList<>();

    /**
     * this is an array of the kind of Resources related to the white marble ability
     */
    private final ArrayList<Resource> whiteMarbleAbility=new ArrayList<>();

    /**
     * this is the player's Resource supply
     */
    private final ResourceSupply resourceSupply = new ResourceSupply();

    /**
     * this the action made by the player in a turn
     */
    private Action action;

    /**
     * this is player's nickname
     */
    private final String name;

    public Player(String username) {
        name=username;
        //System.out.println("creo la personalboard di "+ username); [Debug]
        this.personalBoard = new PersonalBoard(this);
        //System.out.println("personalBoard creata"); [Debug]
        this.action=Action.NOTDONE;
    }

    /**
     * reset the action made in this turn by the player
     */
    public void resetAction(){action=Action.NOTDONE;}

    /**
     * @return true if the Player has done an action in this turn yet
     */
    public boolean checkActionAlreadyDone(){
        return action!=Action.NOTDONE;
    }

    public ArrayList<Resource> getProductionAbility() {
        return productionAbility;
    }

    public ArrayList<Resource> getDiscountAbility() {
        return discountAbility;
    }

    public ArrayList<Resource> getWhiteMarbleAbility() {
        return whiteMarbleAbility;
    }

    /**
     * @param card this is the Leader card activated
     * @return true after activating the card ability
     */
    public boolean activateAbility(LeadCard card)  {
           card.getAbility().activeAbility(this);
           card.setActive();
           return card.isActive();
    }

    /**
     * @param card1 is the id of the first Leader card
     * @param card2 is the id of the second Leader card
     * @return true if the id chosen are valid(he holds the card chosen)
     */
    public boolean checkLeadsIdChosen(int card1, int card2) {
        return leadCards.stream().anyMatch(leadCard -> leadCard.getId()==card1) &&
                leadCards.stream().anyMatch(leadCard -> leadCard.getId()==card1) &&
                card1!=card2;
    }

    /**
     * @return true if he has only two Leader cards
     */
    public boolean leaderCardAlreadyChosen(){
        return leadCards.size()==2;
    }

    public void setAction(Action newAction) {
            this.action = newAction;
    }

    public Action getAction(){
        return this.action;
    }

    public String getName() {
        return name;
    }

    public int getPoints(){
        /**
         * this are the points of the player
         */
        int points = personalBoard.getDevCardSlot().getPoints();
        points +=getLeaderPoints();
        points +=personalBoard.getFaithMarker().getPoints();
        points +=getResourcesPoints();
        points +=popeMeetingPoints;
        return points;
    }

    private int getResourcesPoints() {
        ArrayList<Resource> resources= getSupplyResources();
        resources.addAll(getStrongboxResources());
        resources.addAll(getSpecialShelfResources());
        return resources.size()/5;
    }

    private int getLeaderPoints() {
        return leadCards.stream().filter(LeadCard::isActive).mapToInt(LeadCard::getPoints).sum();
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public int increasePopeMeetingPoints(int faithTrackPoints){
        this.popeMeetingPoints += faithTrackPoints;
        return this.popeMeetingPoints;
    }

    public ArrayList<LeadCard> getLeadCards () {
        return leadCards;
    }

    public void setPlayerLeads(ArrayList<LeadCard> leadCards) {
        this.leadCards= leadCards;
    }

    /**
     * @param card1 is the first card chosen by player
     * @param card2 is the second card chosen by player
     * @return true after discarding the cards not chosen
     */
    public boolean choose2Leads(int card1, int  card2)  {
        leadCards.removeIf(card -> card.getId() != card1 && card.getId() != card2);
             return true;
        }

    /**
     * @param card is the card to discard
     * @return true after discarding the card
     */
    public boolean discardLead(LeadCard card){
        leadCards.remove(card);
        return true;
    }

    /**
     * @param cardId is the id of the Leader card searched
     * @return the Leader card searched
     * @throws CardChosenNotValidException if the player doesn't own the Leader card searched
     */
    public LeadCard getLeadCardFromId(int cardId) throws CardChosenNotValidException {
        for(LeadCard card: leadCards) {
            if (card.getId() == cardId) {
                return card;
            }
        }
        throw new CardChosenNotValidException("You do not own the leadCard chosen");
    }

    public ResourceSupply getResourceSupply() {
        return  resourceSupply;
    }

    /**
     * @return an ArrayList of Resources stored in Player's Special Shelf
     */
    public ArrayList<Resource> getSpecialShelfResources() {
        ArrayList<Resource> resources=new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            System.out.println("controllo lo special chelf "+i);
            if (!personalBoard.getSpecialShelves().isEmpty() && personalBoard.getSpecialShelves().get(i).isPresent()) {
                System.out.println("è presente");
                resources.addAll(personalBoard.getSpecialShelves().get(i).get().getSpecialSlots());
                System.out.println("ho preso le risorse contenute");
            }
        }return resources;
    }

    /**
     * @return an ArrayList of Resources stored in Player's Supply
     */
    public ArrayList<Resource> getSupplyResources() {
        ArrayList<Resource> resources=new ArrayList<>(resourceSupply.viewResources());
        return resources;
    }

    /**
     * @return an ArrayList of Resources stored in Player's Strongbox
     */
    public ArrayList<Resource> getStrongboxResources() {
        return new ArrayList<>(personalBoard.getStrongBox().getStrongboxContent());
    }

    /**
     * @return an ArrayList of Resources stored in Player's Warehouse
     */
    public ArrayList<Resource> getWarehouseResources() {
        return new ArrayList<>(personalBoard.getWarehouseDepots().getResources());
    }

    /**
     * @return a simplified version of the Player's Supply
     */
    public ArrayList<String> getSimplifiedSupply() {
        ArrayList<Resource> resSupply=resourceSupply.viewResources();
        return (ArrayList<String>) resSupply.stream().map(resource -> Objects.toString(resource, null)).collect(Collectors.toList());
    }

    /**
     * @return a Map where the cards (both Leader and Development) id are the keys and the values are a boolean indicating if the card is active
     */
    public Map<Integer,Boolean> getCardsId() {
        Map<Integer, Boolean> cardsId = new HashMap<>();
        //System.out.println("ho creato la mappa");[Debug]
        getLeadCards().forEach(leadCard -> cardsId.put(leadCard.getId(), leadCard.isActive()));
        //System.out.println("mi sono salvato gli id delle lead card");[Debug]
        getPersonalBoard().getDevCardSlot().getDevCards().forEach(devCard -> cardsId.put(devCard.getId(), devCard.isActive()));
        //System.out.println("mi sono salvato gli id delle dev card");[Debug]
        return cardsId;
    }

    /**
     * @return a Map where the cards (only Development) id are the keys and the values are the positions of the cards
     */
    public Map<Integer,Integer> getCardsPosition() {
        Map<Integer, Integer> cardsPos = new HashMap<>();
        //System.out.println("ho creato la mappa");[Debug]
        for(int i=0;i<3;i++) {
            ArrayList<DevCard> slot = getPersonalBoard().getDevCardSlot().getSlot()[i];
            int finalI = i;
            slot.forEach(devCard -> cardsPos.put(devCard.getId(), finalI));
        }
        //System.out.println("mi sono salvato gli id delle dev card");[Debug]
        return cardsPos;
    }

    /**
     * @return an Arraylist of the id of Leader cards held by the Player
     */
    public ArrayList<Integer> getLeadCardsId() {
        ArrayList<Integer> leaderId=new ArrayList<>();
        for (LeadCard card:leadCards)
            leaderId.add(card.getId());
        return leaderId;
    }

    public int getPopeMeetingPoints() {
        return popeMeetingPoints;
    }
}
