package it.polimi.ingsw.model;


import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.cards.LeadCard;
import it.polimi.ingsw.model.personalboard.PersonalBoard;


public class Player {
    private int points;
    private int playerID;
    private PersonalBoard personalBoard;
    private int faithtrackpoints;
    private String potentialresource;
    private int coderr;

    public void setPotentialresource(String potentialresource) {
        this.potentialresource = potentialresource;
    }


    public Resource[] WhiteMarbleAbility = new Resource[2];
    private LeadCard[] cards;


    public Player(int playerID) {
        this.faithtrackpoints = 0;
        this.playerID = playerID;
        this.personalBoard = new PersonalBoard();
    }

        public boolean isWhiteMarbleAbility () {
            return false;
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
            try {
                personalBoard.getStrongBox().getResource(r1);
            } catch (ResourceNotValidException e) {
                try {
                    personalBoard.getWarehouseDepots().getResource(r1);
                } catch (ResourceNotValidException e1) {
                    coderr = 1;
                }
            }
            try {
                personalBoard.getStrongBox().getResource(r2);
            } catch (ResourceNotValidException e) {
                try {
                    personalBoard.getWarehouseDepots().getResource(r2);
                } catch (ResourceNotValidException resourceNotValidException) {
                    coderr = 2;
                }

            }
            Resource resource = Enum.valueOf(Resource.class, potentialresource);
            return resource;
        }

        public int getCoderr () {
            return coderr;
        }
    }
