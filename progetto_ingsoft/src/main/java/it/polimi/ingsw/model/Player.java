package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.personalboard.PersonalBoard;

import java.util.ArrayList;

public class Player {
//TODO fare lista con due elementi che sono le risosrse possibili delle abilità, anzichè utilizzare il boolean
    public boolean playerLeadsEmpty = true;
    private LeadCard[] leadCards=new LeadCard[4];
    private boolean productionAbility= false;
    private boolean discountAbility = false;
    private boolean whiteMarbleAbility = false;
    private boolean shelfAbility = false;

    private int points;
    private int playerID;
    private PersonalBoard personalBoard;
    private int faithtrackpoints;
    private String potentialresource;
    public Resource[] WhiteMarbleAbility = new Resource[2];
    private LeadCard[] cards;

    public boolean isProductionAbility() {
        return productionAbility;
    }

    public void setProductionAbility(boolean productionAbility) {
        this.productionAbility = productionAbility;
    }

    public boolean isShelfAbility() {
        return shelfAbility;
    }

    public void setShelfAbility(boolean shelfAbility) {
        this.shelfAbility = shelfAbility;
    }

    public boolean isDiscountAbility() {
        return discountAbility;
    }

    public void setDiscountAbility(boolean discountAbility) {
        this.discountAbility = discountAbility;
    }

    public boolean isWhiteMarbleAbility() {
        return whiteMarbleAbility;
    }

    public void setWhiteMarbleAbility(boolean whiteMarbleAbility) {
        this.whiteMarbleAbility = whiteMarbleAbility;
    }

    public boolean setPlayerLeads(LeadCard[] leadCards){
         this.leadCards=leadCards;
         return true;
    }


    /**spcial prod è attiva?
     * se è attiva vado a vedere le carte e prendo quella con prod
     *
     *
     * PROD<>
     * SHELf<>
     * WHITE<SERVANT,COIN>
     * DISCOUNT<>
     */



    public Player(int playerID) {
        this.faithtrackpoints = 0;
        this.playerID = playerID;
        this.personalBoard = new PersonalBoard();
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
            this.faithtrackpoints = this.faithtrackpoints + points;
            return faithtrackpoints;
        }

        public int getFaithtrackpoints () {
            return faithtrackpoints;
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
            return Enum.valueOf(Resource.class, potentialresource);

        }

        public void setPotentialresource(String potentialresource) {
            this.potentialresource = potentialresource;
        }

    }
