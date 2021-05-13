package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.AbilityAlreadySetException;
import it.polimi.ingsw.exceptions.ActionAlreadySet;
import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.Market.ResourceSupply;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.cards.cardExceptions.AlreadyActivatedException;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import it.polimi.ingsw.model.personalboard.PersonalBoard;

import java.util.ArrayList;
import java.util.Optional;

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
        System.out.println("creo la personalboard di "+ username);
        this.personalBoard = new PersonalBoard();//mettere faithtrackPoints a zero quando si costruisce board
        System.out.println("personalBoard creata");
    }

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

    public boolean activateAbility(LeadCard card) throws InvalidActiveParameterException {
       if(!card.isActive()) {
           card.getAbility().activeAbility(this);
           card.setActive(true);
           return card.isActive();
       }else
           return false;
    }

    public boolean setAction(Action newAction) throws ActionAlreadySet{
        Optional<Action> playerAction= Optional.ofNullable(action);
        if(!playerAction.isPresent()) {
            this.action = newAction;
            return true;
        }else
            throw new ActionAlreadySet("This player has already a major action set");
    }

    public Action getAction(){
        return this.action;
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

    public boolean choose2Leads(LeadCard card1, LeadCard card2) throws CardChosenNotValidException, WrongAbilityInCardException, AbilityAlreadySetException {//communication with the player
        if(leadCards.contains(card1) && leadCards.contains(card2)) {//controller? controllo anche che due carte sono differenti
            leadCards.removeIf(card -> !card.equals(card1) && !card.equals(card2));
             return true;
        }
        else
            throw new CardChosenNotValidException("One or both card chosen are not present in the player's leadCards available");
    }


//TODO ragionare su abilità e pattern

    public Resource doBasicProduction (ArrayList<Resource> resources,Resource potentialResource) throws ResourceNotValidException {
        getPersonalBoard().removeResources(resources);
        return potentialResource;
    }


    public ResourceSupply getResourceSupply() {
        return  resourceSupply;
    }
}
