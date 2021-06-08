package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LeadDeck implements Decks {

    private final ArrayList<LeadCard> leadDeck = new ArrayList<>();

    public LeadDeck() throws IOException, ParseException {
        JSONParser jsonP = new JSONParser();
        InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(JSONParser.class.getResourceAsStream("/LEADCARDS.json")), StandardCharsets.UTF_8);
            //Read JSON File
            Object obj = jsonP.parse(reader);
            JSONArray leadCardList = (JSONArray) obj;
            //Iterate over leadCard array
            leadCardList.forEach(card-> {
                try {
                    parseLeadCard((JSONObject) card);
                } catch (WrongAbilityInCardException e) {
                    //TODO handle exception
                    e.printStackTrace();
                }
            });
    }

    /**
     * This method parse all the values of a leader card get from the JSON file in a java leader card
     * @param card is a JSONObject representing a leader card
     */
    private void parseLeadCard(JSONObject card) throws WrongAbilityInCardException {
        JSONObject leadCardObj = (JSONObject) card.get("LEADCARD");

        JSONObject jCardReq = (JSONObject) leadCardObj.get("CARDREQ");
        JSONObject jResourceReq = (JSONObject) leadCardObj.get("RESOURCEREQ");
        Resource abilityResource= Resource.valueOf((String)leadCardObj.get("RESOURCE"));
        LeadCard newCard;
        if(jCardReq.isEmpty()){
            HashMap<Integer, Resource> requirements = fromJSONObjToResHash(jResourceReq);
            newCard= new LeadCard(((Long)leadCardObj.get("ID")).intValue(), ((Long)leadCardObj.get("POINTS")).intValue(),
                    createAbility((String) leadCardObj.get("ABILITY"),abilityResource), requirements);
        }else{
            HashMap<Integer,ArrayList<String>> requirements = fromJSONObjToCardHash(jCardReq);
            newCard= new LeadCard(((Long)leadCardObj.get("ID")).intValue(),((Long) leadCardObj.get("POINTS")).intValue(),requirements,
                    createAbility((String) leadCardObj.get("ABILITY"),abilityResource) );
        }
        leadDeck.add(newCard);

    }

    /**
     * @param string represents the ability of this lead card
     * @param resource is the resources related to the ability
     * @return the new LeadAbility
     * @throws WrongAbilityInCardException if
     */
    private LeadAbility createAbility(String string, Resource resource) {
        LeadAbility ability=null;
        switch (string) {
            case "WHITEMARBLE":
                ability=new LeadAbilityWhiteMarble(resource);
            case "PRODUCTION":
                ability=new LeadAbilityProduction(resource);
            case "SHELF":
                ability=new LeadAbilityShelf(resource);
            case "DISCOUNT":
                ability=new LeadAbilityDiscount(resource);

        }
        return ability;
        //throw new WrongAbilityInCardException("Error in the card construction");
    }

    /**
     * @param jsonObj is the attribute of the card in JSON that has to be translate in an hashmap of resources
     * @return the hashmap of resources required to active the card
     */
    private HashMap<Integer, Resource> fromJSONObjToResHash(JSONObject jsonObj){
        HashMap<Integer,Resource> requirements = new HashMap<>();
        requirements.put((int)(long)jsonObj.get("NUM"), Resource.valueOf((String)jsonObj.get("KIND")));

        return requirements;
    }


    /**
     * @param jsonObj is the attribute of the card in JSON that has to be translate in an hashmap of development card
     * @return the hashmap of development card required to active the card
     */
    private HashMap<Integer,ArrayList<String>> fromJSONObjToCardHash(JSONObject jsonObj){
        HashMap<Integer,ArrayList<String>> requirements = new HashMap<>();
        int level = (int)(long)jsonObj.get("LEVEL");
        JSONArray jColor = (JSONArray) jsonObj.get("COLOR");
        ArrayList<String> color = new ArrayList<>();
        Iterator<String> iterator= jColor.iterator();
        while(iterator.hasNext()){
            color.add(iterator.next());
        }
        requirements.put(level, color);
        return requirements;
    }

    public ArrayList<LeadCard> getLeadDeck(){
        return  this.leadDeck;
    }
    /**
     * This method shuffles the lead deck
     * @return the lead deck shuffled
     */
    public ArrayList<LeadCard> shuffle(){
        Collections.shuffle(this.leadDeck);
        return  this.leadDeck;
    }

    /**
     * This method gives the player the four leader card from which they have to choose their two lead cards
     * @param player the player that receives the cards
     * @return true if the player's lead card array has been fulfill without problems
     * @throws playerLeadsNotEmptyException if the player already has the cards in their array
     */
//TODO set abilities/controlla eccezioni/test quante carte do al player
    public boolean giveToPlayer(Player player) throws playerLeadsNotEmptyException{
        if(player.getLeadCards().isEmpty()){
            ArrayList<LeadCard> playerLeads= new ArrayList<>();
            for(int i=0;i<4;i++){
                playerLeads.add(leadDeck.get(0));
                leadDeck.remove(0);
            }
            player.setPlayerLeads(playerLeads);
        }else
            throw new playerLeadsNotEmptyException("Error: playerLeads already full");
        return true;
    }

    /**
     * @param id is the id of the card to find
     * @return the card searched
     * @throws CardChosenNotValidException if the id passed is not valid(id<49 || id>65)
     */
    public LeadCard getCardFromId(int id) throws CardChosenNotValidException {
        for(LeadCard card: leadDeck) {
            if (card.getId() == id)
                return card;
        }
        throw new CardChosenNotValidException("the id passed is not valid");
    }


}



