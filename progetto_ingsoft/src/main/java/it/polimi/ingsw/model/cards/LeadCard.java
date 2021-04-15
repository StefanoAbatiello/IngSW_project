package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import it.polimi.ingsw.model.cards.cardExceptions.cardOwnerAlreadySetException;

//TODO cambio parametro resourcereq perchè sempre una sola risorsa per 5
public class LeadCard  {
    private final long points;
    private final String ability;
    private final Resource resource;
    //private final LeadRequirements requirements;
    private boolean active = false;
    private boolean inGame = false;
    private Player owner = null;



    public LeadCard(long points, String ability, Resource resource /*LeadRequirements requirements*/) {
        this.points = points;
        this.ability = ability;
        this.resource = resource;
        //this.requirements = requirements;

    }

    public long getPoints(){
        return points;
    }
    public String getAbility() {
        return ability;
    }

    public Resource getResource() {
        return resource;
    }

    /*public LeadRequirements getRequirements() {

        return requirements;
    }*/

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) throws InvalidActiveParameterException {
        if (!active)
                throw new InvalidActiveParameterException("Invalid Input: active card cannot be deactivated");

        this.active = active;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame){
        this.inGame = inGame;
    }

    /*public boolean setOwner(Player p) throws cardOwnerAlreadySetException{
       if(owner==null) {
           owner=p;
            return true;
        }else {
           throw new cardOwnerAlreadySetException("Error: card owner already set");
       }
    }

    public Player getOwner(){
       return owner;
    }*/

}
