package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.Cards;
import it.polimi.ingsw.model.personalboard.StrongBox;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DevCard implements Cards{
    /**
     * This attribute represents the id of the devCard
     */
    private final int id;
    /**
     * This attribute represents the points given  to the player by the card
     */
    private final int points;
    /**
     * This attribute represents the color of the card
     */
    private final String color;
    /**
     *This attribute represents the level of the card
     */
    private final int level;
    /**
     *This attribute represents the requirements needed to buy the card
     */
    private final ArrayList<Resource> requirements;
    /**
     * This attribute represents the resources needed in input for this card in order to get through with the production
     */
    private final ArrayList<Resource> prodIn;
    /**
     *This attribute represents the output of the production of the card
     */
    private final ArrayList<Resource> prodOut;
    /**
     *This attribute represents the number of faithPoint as output of the card production
     */
    private final int faithPoint;
    /**
     *This attribute represents if a card is active in order to use its production
     */
    private boolean active = false;

    //private Player owner = null;

    /**
     * This constructor creates the development card with in input all the attributes needed from the outside
     * @param id is the card id
     * @param points are the card's points
     * @param color is the card's color
     * @param level is card's level
     * @param requirements are card's requirements
     * @param prodIn is the list of Resources to give for production
     * @param prodOut is the list of Resources that the player receives from production
     * @param faithPoint is the number of faith point that the player receives from production
     */
    public DevCard(int id, int points, String color, int level, ArrayList<Resource> requirements, ArrayList<Resource> prodIn, ArrayList<Resource> prodOut, int faithPoint) {
        this.id=id;
        this.points= points;
        this.color = color;
        this.level = level;
        this.requirements = requirements;
        this.prodIn = prodIn;
        this.prodOut = prodOut;
        this.faithPoint = faithPoint;
    }

    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public String getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public ArrayList<Resource> getRequirements() {
        return requirements;
    }

    public ArrayList<Resource> getProdIn() {
        return prodIn;
    }

    public ArrayList<Resource> getProdOut() {
        return prodOut;
    }

    public int getFaithPoint() {
        return faithPoint;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Cards getCard() {
        return this;
    }

}


