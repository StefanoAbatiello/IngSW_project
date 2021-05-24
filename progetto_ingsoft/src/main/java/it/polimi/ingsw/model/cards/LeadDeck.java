package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//TODO tipologia di requirement delle lead dipende dall'abilità della carta
public class LeadDeck {

    private static final ArrayList<LeadCard> leadDeck = new ArrayList<>();

    public LeadDeck() {
        JSONParser jsonP = new JSONParser();

        try(FileReader reader = new FileReader("Deliverables/LEADCARDS")){
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
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //TODO posso mettere due hashmap opzionali per i requirements

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

    private LeadAbility createAbility(String string, Resource resource) throws WrongAbilityInCardException {
        switch (string) {
            case "WHITEMARBLE":
                return new LeadAbilityWhiteMarble(resource);
            case "PRODUCTION":
                return new LeadAbilityProduction(resource);
            case "SHELF":
                return new LeadAbilityShelf(resource);
            case "DISCOUNT":
                return new LeadAbilityDiscount(resource);

        }
        //TODO stop the game? error in the card construction
        throw new WrongAbilityInCardException("Error in the card construction");
    }

    /**
     *
     * @param jsonObj
     * @return
     */
    private HashMap<Integer, Resource> fromJSONObjToResHash(JSONObject jsonObj){
        HashMap<Integer,Resource> requirements = new HashMap<>();
        requirements.put((int)(long)jsonObj.get("NUM"), Resource.valueOf((String)jsonObj.get("KIND")));

        return requirements;
    }

    /**
     *
     * @param jsonObj
     * @return
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

    public static LeadCard getCardFromId(int id){
        for(LeadCard card: leadDeck) {
            if (card.getId() == id)
                return card;
        }
        //TODO exception
        return null;
    }


}



