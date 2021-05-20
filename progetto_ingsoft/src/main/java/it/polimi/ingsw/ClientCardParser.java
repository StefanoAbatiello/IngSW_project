package it.polimi.ingsw;

import it.polimi.ingsw.model.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ClientCardParser {

    private MainClient client;
    private JSONArray devCardList;
    private JSONArray leadCardList;
    private int cardId;

    public ClientCardParser(MainClient client) {
        this.client=client;
        JSONParser jsonP = new JSONParser();
        FileReader reader;
        try{
            reader = new FileReader("Deliverables/DEVCARDS.json");
            //Read JSON File
            this.devCardList = (JSONArray)jsonP.parse(reader);
            reader = new FileReader("progetto_ingsoft/Deliverables/LEADCARDS");
            //Read JSON File
            this.leadCardList = (JSONArray) jsonP.parse(reader);

        }
        catch (FileNotFoundException e) {
            System.out.println("File Not found");
            client.disconnect();
        } catch (IOException | ParseException e) {
            System.out.println("Parsing Error");
            client.disconnect();
        }
    }

    public void takeDevCardFromId(int cardId){
        this.cardId=cardId;
        devCardList.forEach(card-> parseDevCard((JSONObject) card));
    }

    private void parseDevCard(JSONObject card) {
        ArrayList<String>[] cardValues = new ArrayList[6];
        JSONObject devCardObj = (JSONObject) card.get("DEVCARD");
        if (((Long) devCardObj.get("ID")).intValue() == cardId) {
            for (int i = 0; i < 6; i++)
                cardValues[i] = new ArrayList<>();
            cardValues[0].add(String.valueOf(devCardObj.get("LEVEL")));
            cardValues[1].add(String.valueOf(devCardObj.get("COLOR")));
            JSONArray jsonProdIn = (JSONArray) devCardObj.get("PRODIN");
            for (String s : (Iterable<String>) jsonProdIn)
                cardValues[2].add(String.valueOf(s));
            JSONArray jsonProdOut = (JSONArray) devCardObj.get("PRODOUT");
            for (String s : (Iterable<String>) jsonProdOut)
                cardValues[3].add(String.valueOf(s));
            cardValues[4].add(String.valueOf(devCardObj.get("FAITHPOINT")));
            JSONArray jsonRequirements = (JSONArray) devCardObj.get("REQUIREMENTS");
            for (String s : (Iterable<String>) jsonRequirements)
                cardValues[5].add(String.valueOf(s));
            client.getViewCLI().getCardsFromId().put(cardId,cardValues);
        }
    }
    public void takeLeadCardFromId(int cardId){
        this.cardId=cardId;
        devCardList.forEach(card-> parseLeadCard((JSONObject) card));
    }

    private void parseLeadCard(JSONObject card) {
        JSONObject devCardObj = (JSONObject) card.get("LEADCARD");
        if (((Long) devCardObj.get("ID")).intValue() == cardId) {
            ArrayList<String>[] cardValues = new ArrayList[6];
            for (int i = 0; i < 6; i++)
                cardValues[i] = new ArrayList<>();
            cardValues[0].add(String.valueOf(devCardObj.get("ABILITY")));
            cardValues[1].add(String.valueOf(devCardObj.get("RESOURCE")));
            JSONObject jsonResourceReq = (JSONObject) devCardObj.get("RESOURCEREQ");
            if(!jsonResourceReq.isEmpty()){
                cardValues[2].add(String.valueOf(jsonResourceReq.get("NUM")));
                cardValues[3].add(String.valueOf(jsonResourceReq.get("KIND")));
            }
            JSONObject jsonCardReq = (JSONObject) devCardObj.get("CARDREQ");
            if(!jsonCardReq.isEmpty()) {
                cardValues[4].add(String.valueOf(jsonCardReq.get("LEVEL")));
                JSONArray jsonColor = (JSONArray) jsonCardReq.get("COLOR");
                for (String s : (Iterable<String>) jsonColor)
                    cardValues[5].add(String.valueOf(s));
            }
            client.getViewCLI().getCardsFromId().put(cardId,cardValues);
        }
    }

}
