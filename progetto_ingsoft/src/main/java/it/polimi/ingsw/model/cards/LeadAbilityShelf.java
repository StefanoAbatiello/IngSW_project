package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.personalboard.SpecialShelf;

import java.util.ArrayList;
import java.util.Optional;

public class LeadAbilityShelf extends LeadAbility{

    public LeadAbilityShelf(Resource resource){
        super(resource);
    }
    @Override
    public boolean activeAbility(Player p) {
       // if(!p.getPersonalBoard().getSpecialShelves().isPresent() || //TODO control both shelves !p.getPersonalBoard().getSpecialShelves().get().contains()) {
            Optional<SpecialShelf> newShelf= Optional.of(new SpecialShelf(this.abilityResource));
            p.getPersonalBoard().getSpecialShelves().add(newShelf);
            return true;

    }

}
