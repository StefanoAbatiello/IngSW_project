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
     * This attribute represents the points given by the card to the player
     */
    private final long points;
    /**
     * This attribute represents the color of the card
     */
    private final String color;
    /**
     *This attribute represents the level of the card
     */
    private final long level;
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
    private final long faithPoint;
    /**
     *This attribute represents if a card is active in order to use its production
     */
    private boolean active = false;

    //private Player owner = null;

    /**
     * This constructor creates the development card with in input all the attributes needed from the outside
     * @param points
     * @param color
     * @param level
     * @param requirements
     * @param prodIn
     * @param prodOut
     * @param faithPoint
     */
    public DevCard(long points, String color, long level, ArrayList<Resource> requirements, ArrayList<Resource> prodIn, ArrayList<Resource> prodOut, long faithPoint) {
        this.points= points;
        this.color = color;
        this.level = level;
        this.requirements = requirements;
        this.prodIn = prodIn;
        this.prodOut = prodOut;
        this.faithPoint = faithPoint;
    }

    /**
     *
     * @return the points of the card
     */
    public long getPoints() {
        return points;
    }

    /**
     *
     * @return the color of the card
     */
    public String getColor() {
        return color;
    }

    /**
     *
     * @return the level of the card
     */
    public long getLevel() {
        return level;
    }

    /**
     *
     * @return the requirements of the card
     */
    public ArrayList<Resource> getRequirements() {
        return requirements;
    }

    /**
     *
     * @return the input of the card production
     */
    public ArrayList<Resource> getProdIn() {
        return prodIn;
    }

    /**
     *
     * @return the output of the card production
     */
    public ArrayList<Resource> getProdOut() {
        return prodOut;
    }

    /**
     *
     * @return if the card has a faithpoint as output of the production
     */
    public long getFaithPoint() {
        return faithPoint;
    }

    /**
     *
     * @return if the card is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     *This method sets if the card is active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *
     * @return the devCard
     */
    @Override
    public Cards getCard() {
        return this;
    }

    public StrongBox useProduction(Player player) throws ResourceNotValidException {
            player.getPersonalBoard().removeResources(getProdIn());
        for(Resource resource: getProdOut())
            player.getPersonalBoard().getStrongBox().addInStrongbox(resource);
        return player.getPersonalBoard().getStrongBox();
    }

}


