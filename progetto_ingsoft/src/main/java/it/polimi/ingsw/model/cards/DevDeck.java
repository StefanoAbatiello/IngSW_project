package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

public class DevDeck implements ResourceGeneratorFromJSON, Decks{

    /**
     * This attribute is the array of development cards that form the development deck
     */
    private final ArrayList<DevCard> devCards = new ArrayList<>();

    /**
     * This constructor uses a JSON file to create the deck of dev cards through the parsing methods
     */
    public DevDeck() throws IOException, ParseException {
        JSONParser jsonP = new JSONParser();
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(JSONParser.class.getResourceAsStream("/DEVCARDS.json"
        )), StandardCharsets.UTF_8);
        //Read JSON File
        Object obj = jsonP.parse(reader);
        JSONArray devCardList = (JSONArray) obj;
        //Iterate over devCard array
        devCardList.forEach(card -> parseDevCard((JSONObject) card));
    }

    /**
     * This method parse all the values of a development card get from the JSON file in a java development card
     * @param card is a JSONObject representing a development card
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
                ((Long) devCardObj.get("LEVEL")).intValue(),
                requirements,
                prodIn,
                prodOut,((Long) devCardObj.get("FAITHPOINT")).intValue()
        );
        devCards.add(newDevCard);

    }

    /**
     * This method permits to cast a received JSONArray in an array of Resources
     * that is returned at the end of the process
     * @param jsonArray represents an array of object in JSON
     * @return ArrayList<Resource> used to create different attributes of a development card
     */
    @Override
    public ArrayList<Resource> fromJSONArrayToResourceList(JSONArray jsonArray){
        ArrayList<Resource> resourceList= new ArrayList<>();
        Iterator<String> iterator= jsonArray.iterator();
        while(iterator.hasNext()){
            resourceList.add(Resource.valueOf((iterator.next())));
        }
        return resourceList;
    }

    /**
     * This method creates a little deck of cards of a certain color and put the cards in it in order of level
     * @param color represent the color of the cards desired in the new little deck
     * @return an ArrayList of DevCard representing a little deck with all the cards of the same color and in order of level
     */
    public ArrayList<DevCard> createLittleDecks(String color){
        ArrayList<DevCard> littleDeck;

        littleDeck= devCards.stream().filter(x -> x.getColor().equals(color)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        littleDeck.sort(Comparator.comparing(DevCard::getLevel));
        return littleDeck;
    }

    /**
     * @return current state of devCards
     */
    public ArrayList<DevCard> getDevCards() {
        return devCards;
    }

    /**
     * @param id is the id of the card to find
     * @return the card searched
     * @throws CardChosenNotValidException if the id passed is not valid(id<0 || id>48)
     */
    @Override
    public DevCard getCardFromId(int id) throws CardChosenNotValidException {
        for(DevCard card: devCards) {
            if (card.getId() == id)
                return card;
        }
        throw new CardChosenNotValidException("the id passed is not valid");
    }



}