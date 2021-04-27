package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.ResourceSupply;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.personalboard.PersonalBoard;

import java.util.ArrayList;

public class Player {
    public boolean playerLeadsEmpty = true;
    private LeadCard[] leadCards=new LeadCard[4];
    private ArrayList<Resource> productionAbility= new ArrayList<>();
    private ArrayList<Resource> discountAbility = new ArrayList<>();
    private ArrayList<Resource> whiteMarbleAbility = new ArrayList<>();
    private ArrayList<Resource> shelfAbility = new ArrayList<>();
    private int points = 0;
    private int playerID;
    private String potentialResource;
    private PersonalBoard personalBoard = new PersonalBoard();
    private int faithtrackPoints = 0;
    private LeadCard[] cards;
    private ResourceSupply resourceSupply = new ResourceSupply();

    public Player(int playerID) {
        this.playerID = playerID;
    }

    public ArrayList<Resource> getShelfAbility(ArrayList<Resource> shelfAbility) {
        return this.shelfAbility;
    }
    public ArrayList<Resource> getDiscountAbility(ArrayList<Resource> discountAbility) {
       return this.discountAbility;
    }
    public ArrayList<Resource> getWhiteMarbleAbility(ArrayList<Resource> whiteMarbleAbility) {
        return this.whiteMarbleAbility;
    }
    public ArrayList<Resource> getProductionAbility(ArrayList<Resource> whiteMarbleAbility) {
        return this.whiteMarbleAbility;
    }
    public boolean setPlayerLeads(LeadCard[] leadCards){
         this.leadCards=leadCards;
         return true;
    }


        public LeadCard[] getLeadCards () {
            return cards;
        }

        public PersonalBoard getPersonalBoard() {
            return personalBoard;
        }

        public int getPlayerID () {
            return playerID;
        }

        public void setPoints ( int points){
            this.points = points;
        }

        public int increaseFaithtrackPoints ( int points){
            this.faithtrackPoints = this.faithtrackPoints + points;
            return faithtrackPoints;
        }

        public int getFaithtrackPoints() {
            return faithtrackPoints;
        }


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

        public void setPotentialresource(String potentialResource) {
            this.potentialResource = potentialResource;
        }

    public ResourceSupply getResourceSupply() {
        return resourceSupply;
    }
}
