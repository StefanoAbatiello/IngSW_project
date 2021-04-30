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
import it.polimi.ingsw.model.personalboard.PersonalBoard;

import java.util.ArrayList;
import java.util.Optional;

public class Player implements Points{
    //TODO controllo commenti
    private int points;
    private int playerID;
    private PersonalBoard personalBoard;
    private int faithtrackPoints;
    private String potentialResource;
    private ArrayList<LeadCard> leadCards=new ArrayList<>();
    private LeadAbility ability1;
    private LeadAbility ability2;
    private ResourceSupply resourceSupply = new ResourceSupply();
    private Action action;


    public Player(int playerID) {
        this.points = 0;
        this.playerID = playerID;
        this.personalBoard = new PersonalBoard();//mettere faithtrackPoints a zero quando si costruisce board
        this.faithtrackPoints = 0;

    }


    //TODO penso metodi in game che chiamano strategy,penso a costruttore di ability

    private boolean setAbility (LeadAbility leadAbility) throws AbilityAlreadySetException {
        Optional<LeadAbility> firstAbility = Optional.ofNullable(ability1);
        Optional<LeadAbility> secondAbility = Optional.ofNullable(ability2);
        if(!firstAbility.isPresent()) {
            this.ability1 = leadAbility;
            return true;
        } else if(!secondAbility.isPresent()) {
            this.ability2 = leadAbility;
            return true;
        }else
            throw new AbilityAlreadySetException("ability slots already set");
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

    public boolean activateAbility1() throws AlreadyActivatedException {
        return ability1.setActive(true);
    }
    public boolean activateAbility2() throws AlreadyActivatedException {
        return ability2.setActive(true);
    }

    public boolean useAbility1(){
        return ability1.useAbility(this);
    }

    public boolean useAbility2(){
        return ability2.useAbility(this);
    }

    public LeadAbility getAbility1(){
        return ability1;
    }

    public LeadAbility getAbility2(){
        return ability2;
    }


    @Override
    public int getPoints(){
        return points;
    }
    public int setPoints ( int points){
        this.points += points;
        return this.points;
    }

    public int getPlayerID () {
        return playerID;
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

    //TODO use Resource instead of String
    public void setPotentialResource(String potentialResource) {
        this.potentialResource = potentialResource;
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

    //TODO test
    public boolean choose2LeadsAndSetAbilities(LeadCard card1, LeadCard card2) throws CardChosenNotValidException, WrongAbilityInCardException, AbilityAlreadySetException {//communication with the player
        if(leadCards.contains(card1) && leadCards.contains(card2)) {//controller? controllo anche che due carte sono differenti
            leadCards.removeIf(card -> !card.equals(card1) && !card.equals(card2));
            setAbility(getLeadCards().get(0).getAbilityFromCard());
            ability1.setAbilityResource(getLeadCards().get(0).getResource());
            setAbility(getLeadCards().get(1).getAbilityFromCard());
            ability2.setAbilityResource(getLeadCards().get(1).getResource());
            return true;
        }
        else
            throw new CardChosenNotValidException("One or both card chosen are not present in the player's leadCards available");
    }


//TODO ragionare su abilità e pattern

    public Resource doBasicProduction (Resource r1, Resource r2){
        ArrayList<Resource> resourceArrayList=new ArrayList<>();
        resourceArrayList.add(r1);
        resourceArrayList.add(r2);

        if(getPersonalBoard().checkUseProd(resourceArrayList)){
            try {
                getPersonalBoard().removeResources(resourceArrayList);
            } catch (ResourceNotValidException e) {
                e.printStackTrace();
            }
        }
        return Enum.valueOf(Resource.class, potentialResource);
    }


    public ResourceSupply getResourceSupply() {
        return  resourceSupply;
    }
}
