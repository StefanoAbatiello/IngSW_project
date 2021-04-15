package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.LeadCard;

public class Player {
//TODO fare lista con due elementi che sono le risosrse possibili delle abilità, anzichè utilizzare il boolean
    public boolean playerLeadsEmpty = true;
    private LeadCard[] leadCards=new LeadCard[4];
    private boolean productionAbility= false;
    private boolean discountAbility = false;
    private boolean whiteMarbleAbility = false;
    private boolean shelfAbility = false;

    public boolean isProductionAbility() {
        return productionAbility;
    }

    public void setProductionAbility(boolean productionAbility) {
        productionAbility = productionAbility;
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