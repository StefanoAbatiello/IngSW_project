package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ActionAlreadySetException;
import it.polimi.ingsw.model.Market.ResourceSupply;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.personalboard.PersonalBoard;
import java.util.*;

public class Player implements Points{

    /**
     * this are the points of the player
     */
    private int points=0;

    /**
     * this is the player's Personal board
     */
    private final PersonalBoard personalBoard;

    /**
     * this are the points gained from the position of Faith marker
     */
    private int faithTrackPoints =0;

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
        this.personalBoard = new PersonalBoard();
        //System.out.println("personalBoard creata"); [Debug]
    }

    /**
     * reset the action made in this turn by the player
     */
    public void resetAction(){action=null;}

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

    public boolean setAction(Action newAction) throws ActionAlreadySetException {
            this.action = newAction;
            return true;
    }

    public Action getAction(){
        return this.action;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getPoints(){
        return points;
    }

    public int setPoints (int points){
        this.points += points;
        return this.points;
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public int getFaithTrackPoints() {
        return faithTrackPoints;
    }

    public int increaseFaithTrackPoints(int faithTrackpoints){
        this.faithTrackPoints += faithTrackpoints;
        return this.faithTrackPoints;
    }

    public String getPotentialResource(String potentialResource) {
        return potentialResource;
    }//serve getter?

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
     * @param resources is the list of Resources chosen by the player to activate the basic production
     * @param potentialResource is the Resource chosen by the player as product of the basic production
     * @return the produced Resource
     */
    public Resource doBasicProduction (ArrayList<Resource> resources,Resource potentialResource) {
        getPersonalBoard().removeResources(resources);
        return potentialResource;
    }

    /**
     * @param id is the id of the Leader card searched
     * @return the Leader card searched
     * @throws CardChosenNotValidException if the player doesn't own the Leader card searched
     */
    public LeadCard getCardFromId(int id) throws CardChosenNotValidException {
        for(LeadCard card: leadCards) {
            if (card.getId() == id)
                return card;
        }
        throw new CardChosenNotValidException("You have not this card");
    }

    public ResourceSupply getResourceSupply() {
        return  resourceSupply;
    }


    /**
     * @return a Map where the cards (both Leader and Development) id are the kay and the values are a boolean indicating if the card is active
     */
    public Map<Integer,Boolean> getCardsId() {
        Map<Integer, Boolean> cardsId = new HashMap<>();
        System.out.println("ho creato la mappa");
        getLeadCards().forEach(leadCard -> cardsId.put(leadCard.getId(), leadCard.isActive()));
        System.out.println("mi sono salvato gli id delle lead card");
        getPersonalBoard().getDevCardSlot().getDevCards().forEach(devCard -> cardsId.put(devCard.getId(), devCard.isActive()));
        System.out.println("mi sono salvato gli id delle dev card");
        return cardsId;
    }

    /**
     * @return an Arraylist of the id held by the Player
     */
    public ArrayList<Integer> getLeadCardsId() {
        ArrayList<Integer> leaderId=new ArrayList<>();
        for (LeadCard card:leadCards)
            leaderId.add(card.getId());
        return leaderId;
    }
}
