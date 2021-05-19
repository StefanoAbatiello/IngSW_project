package it.polimi.ingsw.model.cards;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import it.polimi.ingsw.model.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DevDeck{

    /**
     * This attribute is the array of development cards that form the development deck
     */
    private final ArrayList<DevCard> devCards = new ArrayList<>();

    /**
     * This constructor uses a JSON file to create the deck of dev cards through the parsing methods
     */
    public DevDeck() {
        JSONParser jsonP = new JSONParser();

        try(FileReader reader = new FileReader("Deliverables/DEVCARDS.json")){
            //Read JSON File
            Object obj = jsonP.parse(reader);
            JSONArray devCardList = (JSONArray) obj;
            //Iterate over devCard array
            devCardList.forEach(card-> parseDevCard((JSONObject) card));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method parse all the values of a development card get from the JSON file in a java development card
     * @param card represents a JSONObject representing a development card
     */

    private void parseDevCard(JSONObject card) {
        JSONObject devCardObj = (JSONObject) card.get("DEVCARD");
        //get devCard info to create the card
        JSONArray jsonProdIn = (JSONArray) devCardObj.get("PRODIN");
        ArrayList<Resource> prodIn = fromJSONArrayToResourceList(jsonProdIn);
        JSONArray jsonProdOut = (JSONArray) devCardObj.get("PRODOUT");
        ArrayList<Resource> prodOut = fromJSONArrayToResourceList(jsonProdOut);
        JSONArray jsonRequirements = (JSONArray) devCardObj.get("REQUIREMENTS");
        ArrayList<Resource> requirements = fromJSONArrayToResourceList(jsonRequirements);

        DevCard newDevCard= new DevCard(((Long) devCardObj.get("ID")).intValue(),((Long) devCardObj.get("POINTS")).intValue(),
                (String) devCardObj.get("COLOR"),
                (long) devCardObj.get("LEVEL"),
                requirements,
                prodIn,
                prodOut,(long) devCardObj.get("FAITHPOINT")
        );
        devCards.add(newDevCard);

    }

    /**
     * This method permits to cast a received JSONArray in an array of Resources that is returned at the end of the process
     * @param jsonArray represents an array of object in JSON
     * @return ArrayList<Resource> used to create different attributes of a development card
     */
    //TODO @Override
    private ArrayList<Resource> fromJSONArrayToResourceList(JSONArray jsonArray){

        ArrayList<Resource> resourceList= new ArrayList<>();
        Iterator<String> iterator= jsonArray.iterator();
        while(iterator.hasNext()){
            resourceList.add(Resource.valueOf((iterator.next())));
        }
        return resourceList;
    }

    /**
     * This method creates a little deck of cards of a certain color and put the cards in it in order of level
     * @param color represent the color of the cards desidered in the new little deck
     * @return an ArrayList of DevCard representing a little deck with all the cards of the same color and in order of level
     */
    public ArrayList<DevCard> createLittleDecks(String color){
        ArrayList<DevCard> littleDeck;

        littleDeck= devCards.stream().filter(x -> x.getColor().equals(color)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        littleDeck.sort(Comparator.comparing(DevCard::getLevel));
        return littleDeck;
    }

    /**
     *
     * @return current state of devCards
     */
    public ArrayList<DevCard> getDevCards() {
        return devCards;
    }

    public DevCard getCardFromId(int id){
        for(DevCard card: devCards) {
            if (card.getId() == id)
                return card;
        }
        //TODO exception
        return null;
    }



}