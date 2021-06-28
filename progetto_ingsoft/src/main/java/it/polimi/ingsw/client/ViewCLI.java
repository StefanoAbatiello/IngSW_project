package it.polimi.ingsw.client;

import java.util.*;

public class ViewCLI {

    private Map<Integer, Boolean> devCardsId;
    private Map<Integer, Boolean> leadCardsId;
    private ArrayList<String>[] warehouse;
    /*Array di risorse dove ogni indice è un tipo di risorse:
        0-COIN
        1-SERVANT
        2-SHIELD
        3-STONE
         */
    private int[] strongbox;
    private int faithPosition;
    private int[][] devMatrix;
    private final Map<Integer, ArrayList<String>[]> cardsFromId;
    private String[][] market;

    public void setLeadCardsId(Map<Integer,Boolean> leadCardsId) {
        this.leadCardsId = leadCardsId;
    }

    public Map<Integer, ArrayList<String>[]> getCardsFromId() {
        return cardsFromId;
    }

    public String[][] getMarket() {
        return market;
    }

    public void setMarket(String[][] market) {
        this.market = market;
    }

    public ViewCLI() {
        devCardsId=new HashMap<>();
        leadCardsId=new HashMap<>();
        strongbox = new int[4];
        for (int i=0;i<4;i++)
            strongbox[i]=0;
        faithPosition=0;
        devMatrix=new int[4][3];
        warehouse=new ArrayList[5];
        for(int i=0;i<warehouse.length;i++)
            warehouse[i]=new ArrayList<>();
        cardsFromId = new HashMap<>();
        market=new String[3][4];
    }

    public void setWarehouse(ArrayList<String>[] warehouse) {
        this.warehouse = warehouse;
    }

    public ArrayList<String>[] getWarehouse(){
        return warehouse;
    }

    public void setDevMatrix(int[][] devMatrix) {
        this.devMatrix = devMatrix;
    }

    public int[][] getDevMatrix(){return this.devMatrix;}

    public Map<Integer, Boolean> getDevCardsId() {
        return devCardsId;
    }

    public void addDevCardId(int devCardId,boolean active) {
        this.devCardsId.put(devCardId,active);
    }

    public Map<Integer,Boolean> getLeadCardsId() {
        return leadCardsId;
    }

    public void addLeadCardsId(int leadCardsId,Boolean active) {
        this.leadCardsId.put(leadCardsId,active);
    }

    public int[] getStrongbox() {
        return strongbox;
    }

    public void setStrongbox(int[] strongbox){
        this.strongbox=strongbox;
    }

    public void addResourceInBox(String resource) {
        switch (resource.toLowerCase()) {
            case "COIN":
                this.strongbox[0]++;
                break;
            case "SERVANT":
                this.strongbox[1]++;
                break;
            case "SHIELD":
                this.strongbox[2]++;
                break;
            case "STONE":
                this.strongbox[3]++;
                break;
        }
    }

    public void removeResourceFromBox(String resource) {
        switch (resource.toLowerCase()) {
            case "COIN":
                this.strongbox[0]--;
                break;
            case "SERVANT":
                this.strongbox[1]--;
                break;
            case "SHIELD":
                this.strongbox[2]--;
                break;
            case "STONE":
                this.strongbox[3]--;
                break;
        }
    }

    public int getFaithPosition() {
        return faithPosition;
    }

    public void setFaithPosition(int faithPosition) {
        this.faithPosition = faithPosition;
    }

    public void showPersonalBoard(){
        System.out.println("Your development card id:");
        for(int i:devCardsId.keySet()) {
            showDevCard(i);
            System.out.println("\n    active: " + devCardsId.get(i));
        }
        System.out.println("Your leader card id:");
        for(int i:leadCardsId.keySet()) {
            showLeadCard(i);
            System.out.println("\n    active: " + leadCardsId.get(i));
        }
        System.out.println("Your faith track is in position: " + faithPosition);
        System.out.print("In strongbox you have: ");
        System.out.print(strongbox[0] + " COIN, ");
        System.out.print(strongbox[1] + " SERVANT, ");
        System.out.print(strongbox[2] + " SHIELD, ");
        System.out.print(strongbox[3] + " STONE.");
        System.out.println("\nIn warehouse depots you have: ");
        if(warehouse[0].isEmpty())
            System.out.println("No resources in first shelf");
        else
            System.out.println(warehouse[0].size() +" "+ warehouse[0].get(0) + " in first shelf");
        if(warehouse[1].isEmpty())
            System.out.println("No resources in second shelf");
        else
            System.out.println(warehouse[1].size() +" "+ warehouse[1].get(0) + " in second shelf");
        if(warehouse[2].isEmpty())
            System.out.println("No resources in third shelf");
        else
            System.out.println(warehouse[2].size() +" "+ warehouse[2].get(0) + " in third shelf");
    }

    public void showMarket(){
        for(int i=0;i<3;i++){
            System.out.println(i+"->    " + market[i][0] + "    |       " + market[i][1] + "    |       " + market[i][2] + "    |       " + market[i][3]);
        }
        System.out.println("        ^               ^                   ^               ^");
        System.out.println("        3               4                   5               6");
    }

    public void showLeadCard(int cardId) {
        //System.out.println("a");[Debug]
        ArrayList<String>[] card = cardsFromId.get(cardId);
        System.out.println("ID: " + cardId);
        System.out.println("    Ability: " + card[0].get(0));
        System.out.println("    Resource: " + card[1].get(0));
        if (!card[2].isEmpty() && !card[3].isEmpty())
            System.out.println("    Requirements: " + card[2].get(0)/*num of resources required*/
                    +" "+ card[3].get(0)/*type of resources required*/);
        else {
            if (card[5].size() == 1)
                System.out.println("    Requirements: a " + card[5].get(0)/*color of dev card*/
                        + " devCard of level " + card[4].get(0)/*level of devCArd*/);
            else {
                System.out.print("    Requirements: devCards of color ");
                card[5].forEach(s -> System.out.print(s + ", "));
                System.out.print("\n");
            }
        }
    }

    private void showDevCard(int cardId){
        ArrayList<String>[] card = cardsFromId.get(cardId);
        System.out.println("id: "+cardId);
        System.out.println("    level: "+card[0].get(0));
        System.out.println("    color: "+card[1].get(0));
        System.out.print("    prodin: "+card[2].get(0));
        for(int k=0;k<card[2].size()-1;k++)
            System.out.print(", "+card[2].get(k));
        if(!card[3].isEmpty()) {
            System.out.print("\n    prodout: " + card[3].get(0));
            for (int k = 0; k < card[3].size() - 1; k++)
                System.out.print(", " + card[3].get(k));
        }else
            System.out.print("\n    prodout: ");
        System.out.println("\n    faithpoints: "+card[4].get(0));
    }

    public void showDevMatrix(){
        ArrayList<String>[] card;
        for(int i=0; i<4;i++)
            for(int j=0;j<3;j++){
                card = cardsFromId.get(devMatrix[i][j]);
                showDevCard(devMatrix[i][j]);
                System.out.print("    requirements: "+card[5].get(0));
                for(int k=0;k<card[5].size()-1;k++)
                    System.out.print(", "+card[5].get(k));
                System.out.print("\n");
        }
    }


}
