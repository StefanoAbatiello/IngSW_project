package it.polimi.ingsw.model.cards;

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

    private static ArrayList<LeadCard> leadDeck;
    static ArrayList<DevCard> devDeck = new ArrayList<>();

    public LeadDeck() {
        JSONParser jsonP = new JSONParser();

       /* try(FileReader reader = new FileReader("/Users/camillablasucci/IdeaProjects/ingswAM2021-Blasucci-Abatiello-Buono/progetto_ingsoft/src/main/java/it/polimi/ingsw/model/cards/LEADCARDS.json")){
            //Read JSON File
            Object obj = jsonP.parse(reader);
            JSONArray leadCardList = (JSONArray) obj;
            //Iterate over devCard array
            leadCardList.forEach(card-> parseLeadCard((JSONObject) card));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void parseLeadCard(JSONObject card) {
        JSONObject devCardObj = (JSONObject) card.get("LEADCARD");
        //get devCard info to create the card
        /*JSONArray jsonProdIn = (JSONArray) devCardObj.get("PRODIN");
        ArrayList<Resource> prodIn = fromJSONArrayToResourceList(jsonProdIn);
        JSONArray jsonProdOut = (JSONArray) devCardObj.get("PRODOUT");
        ArrayList<Resource> prodOut = fromJSONArrayToResourceList(jsonProdOut);
        JSONArray jsonRequirements = (JSONArray) devCardObj.get("REQUIREMENTS");
        ArrayList<Resource> requirements = fromJSONArrayToResourceList(jsonRequirements);

        DevCard newDevCard= new DevCard((long) devCardObj.get("POINTS"),
                (String) devCardObj.get("COLOR"),
                (long) devCardObj.get("LEVEL"),
                requirements,
                prodIn,
                prodOut,(boolean) devCardObj.get("FAITHPOINT")
        );
        devDeck.add(newDevCard);*/

    }

    private ArrayList<Resource> fromJSONArrayToResourceList(JSONArray jsonArray){

        ArrayList<Resource> resourceList= new ArrayList<>();
        Iterator<String> iterator= jsonArray.iterator();
        while(iterator.hasNext()){
            resourceList.add(Resource.valueOf(((String)iterator.next())));
        }
        return resourceList;
    }

//TODO vedere metodi in comune tra due carte e implementare interfaccia o classe astratta
   public ArrayList<LeadCard> shuffleCards(){

        Collections.shuffle(this.leadDeck);

        return  this.leadDeck;
   }

  public boolean giveCardsToPlayer(Player player) throws playerLeadsNotEmptyException{
       if(player.playerLeadsEmpty){
           LeadCard[] playerLeads= new LeadCard[4];
           for(int i=0;i<3;i++){
               playerLeads[i]=leadDeck.get(0);
               leadDeck.remove(0);
           }
           player.setPlayerLeads(playerLeads);
       }else
           throw new playerLeadsNotEmptyException("Error: playerLeads already full");
       return true;
  }


/**setplayerLeads prende array e lo mette al giocatore
 * public LeadCard[] setPlayerLeads(LeadCard[] playerLeads){
 *  return playerLeads;
 * }
 * */
}



