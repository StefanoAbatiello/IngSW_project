package it.polimi.ingsw.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class ClientCardParser {

    private final MainClient client;
    private JSONArray devCardList;
    private JSONArray leadCardList;
    private int cardId;

    public ClientCardParser(MainClient client) {
        this.client=client;
        JSONParser jsonP = new JSONParser();
        InputStreamReader reader;
        try{
            reader = new InputStreamReader(Objects.requireNonNull(JSONParser.class.getResourceAsStream(
                    "/DEVCARDS.json")), StandardCharsets.UTF_8);
            //System.out.println("ho letto il file dev");[Debug]
            this.devCardList = (JSONArray)jsonP.parse(reader);
            reader = new InputStreamReader(Objects.requireNonNull(JSONParser.class.getResourceAsStream(
                    "/LEADCARDS.json")), StandardCharsets.UTF_8);
            //System.out.println("ho letto il file lead");[Debug]
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
        devCardList.forEach(card->{JSONObject devCardObj = (JSONObject) ((JSONObject) card).get("DEVCARD");
            if (((Long) devCardObj.get("ID")).intValue() == cardId) { parseDevCard(devCardObj);}});
    }

    private void parseDevCard(JSONObject card) {
        if (!client.getViewCLI().getCardsFromId().containsKey(cardId)) {
            ArrayList<String>[] cardValues = new ArrayList[6];
            for (int i = 0; i < 6; i++)
                cardValues[i] = new ArrayList<>();
            cardValues[0].add(String.valueOf(card.get("LEVEL")));
            cardValues[1].add(String.valueOf(card.get("COLOR")));
            JSONArray jsonProdIn = (JSONArray) card.get("PRODIN");
            for (String s : (Iterable<String>) jsonProdIn)
                cardValues[2].add(String.valueOf(s));
            JSONArray jsonProdOut = (JSONArray) card.get("PRODOUT");
            for (String s : (Iterable<String>) jsonProdOut)
                cardValues[3].add(String.valueOf(s));
            cardValues[4].add(String.valueOf(card.get("FAITHPOINT")));
            JSONArray jsonRequirements = (JSONArray) card.get("REQUIREMENTS");
            for (String s : (Iterable<String>) jsonRequirements)
                cardValues[5].add(String.valueOf(s));
            client.getViewCLI().getCardsFromId().put(cardId, cardValues);
        }
    }

    public void takeLeadCardFromId(int cardId){
        this.cardId=cardId;
        this.leadCardList.forEach(card-> {JSONObject devCardObj = (JSONObject) ((JSONObject) card).get("LEADCARD");
            if (((Long) devCardObj.get("ID")).intValue() == cardId) { parseLeadCard(devCardObj);}});
    }

    private void parseLeadCard(JSONObject card) {
        if (!client.getViewCLI().getCardsFromId().containsKey(cardId)) {
            ArrayList<String>[] cardValues = new ArrayList[6];
            for (int i = 0; i < 6; i++)
                cardValues[i] = new ArrayList<>();
            System.out.println("inizio il parsing");
            cardValues[0].add(String.valueOf(card.get("ABILITY")));
            cardValues[1].add(String.valueOf(card.get("RESOURCE")));
            JSONObject jsonResourceReq = (JSONObject) card.get("RESOURCEREQ");
            if (!jsonResourceReq.isEmpty()) {
                cardValues[2].add(String.valueOf(jsonResourceReq.get("NUM")));
                cardValues[3].add(String.valueOf(jsonResourceReq.get("KIND")));
            }
            JSONObject jsonCardReq = (JSONObject) card.get("CARDREQ");
            if (!jsonCardReq.isEmpty()) {
                cardValues[4].add(String.valueOf(jsonCardReq.get("LEVEL")));
                JSONArray jsonColor = (JSONArray) jsonCardReq.get("COLOR");
                for (String s : (Iterable<String>) jsonColor)
                    cardValues[5].add(String.valueOf(s));
            }
            System.out.println("salvo dati carta nella mappa");
            client.getViewCLI().getCardsFromId().put(cardId, cardValues);
            System.out.println("dati carta salvati nella mappa");
        }
    }

}
