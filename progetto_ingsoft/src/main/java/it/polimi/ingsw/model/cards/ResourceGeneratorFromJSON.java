package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import org.json.simple.JSONArray;

import java.util.ArrayList;

public interface ResourceGeneratorFromJSON {
    ArrayList<Resource> fromJSONArrayToResourceList(JSONArray jsonArray);
}
