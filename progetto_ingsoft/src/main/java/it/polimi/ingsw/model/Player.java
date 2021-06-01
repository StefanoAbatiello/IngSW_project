package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.AbilityAlreadySetException;
import it.polimi.ingsw.exceptions.ActionAlreadySetException;
import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.Market.ResourceSupply;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import it.polimi.ingsw.model.personalboard.PersonalBoard;

import java.util.ArrayList;

public class Player implements Points{
    //TODO controllo commenti
    private int points;
    private final PersonalBoard personalBoard;
    private int faithtrackPoints=0;
    private ArrayList<LeadCard> leadCards=new ArrayList<>();
    private final ArrayList<Resource> productionAbility=new ArrayList<>();
    private final ArrayList<Resource> discountAbility=new ArrayList<>();
    private final ArrayList<Resource> whiteMarbleAbility=new ArrayList<>();
    private final ResourceSupply resourceSupply = new ResourceSupply();
    private Action action;
    private final String name;

    public Player(String username) {
        name=username;
        this.points = 0;
        //System.out.println("creo la personalboard di "+ username); [Debug]
        this.personalBoard = new PersonalBoard();
        //System.out.println("personalBoard creata"); [Debug]
    }

    public void resetAction(){action=null;}

    //TODO penso metodi in game che chiamano strategy,penso a costruttore di ability

    public ArrayList<Resource> getProductionAbility() {
        return productionAbility;
    }

    public ArrayList<Resource> getDiscountAbility() {
        return discountAbility;
    }

    public ArrayList<Resource> getWhiteMarbleAbility() {
        return whiteMarbleAbility;
    }

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
    public int setPoints ( int points){
        this.points += points;
        return this.points;
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public int getFaithtrackPoints() {
        return faithtrackPoints;
    }

    public int increaseFaithtrackPoints ( int faithtrackpoints){
        this.faithtrackPoints += faithtrackpoints;
        return this.faithtrackPoints;
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

    //TODO check tests
    public boolean choose2Leads(int card1, int  card2)  {
        leadCards.removeIf(card -> card.getId() != card1 && card.getId() != card2);
             return true;
        }

    public boolean discardLead(LeadCard card){
        leadCards.remove(card);
        return true;
    }


//TODO ragionare su abilità e pattern

    public Resource doBasicProduction (ArrayList<Resource> resources,Resource potentialResource) {
        getPersonalBoard().removeResources(resources);
        return potentialResource;
    }

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

}
