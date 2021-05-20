package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewCLI {

    private ArrayList<Integer> devCardsId;
    private ArrayList<Integer> leadCardsId;
    private ArrayList<String>[] warehouse;
    /*Array di risorse dove ogni indice è un tipo di risorse:
    0-COIN
    1-SERVANT
    2-SHIELD
    3-STONE
     */
    private final int[] strongbox;
    private int faithPosition;
    private int[][] devMatrix;
    private Map<Integer, ArrayList<String>[]> cardsFromId;

    public Map<Integer, ArrayList<String>[]> getCardsFromId() {
        return cardsFromId;
    }

    public ViewCLI() {
        devCardsId=new ArrayList<>();
        leadCardsId=new ArrayList<>();
        warehouse=new ArrayList[3];
        for(int i=0;i<3;i++)
            warehouse[i] = new ArrayList<>();
        strongbox = new int[4];
        for (int i=0;i<4;i++)
            strongbox[i]=0;
        faithPosition=0;
        devMatrix=new int[4][3];
        for (int i=0;i<4;i++)
            for (int j=0; j<3; j++) {
                devMatrix[i][j] = i + j;
            }
        cardsFromId = new HashMap<>();
    }

    public int[][] getDevMatrix(){return this.devMatrix;}

    public ArrayList<Integer> getDevCardsId() {
        return devCardsId;
    }

    public void addDevCardId(int devCardId) {
        this.devCardsId.add(devCardId);
    }

    public ArrayList<Integer> getLeadCardsId() {
        return leadCardsId;
    }

    public void addLeadCardsId(int leadCardsId) {
        this.leadCardsId.add(leadCardsId);
    }

    public int[] getStrongbox() {
        return strongbox;
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

    public void showView(){
        System.out.println("Your development card:");
        for(int i:devCardsId) {
            System.out.println("i");
        }
        System.out.println("Your leader card:");
        for(int i:leadCardsId) {
            System.out.println("i");
        }
        System.out.println("Your faith track is in position: " + faithPosition);
        System.out.println("In strongbox you have: ");
        System.out.println(strongbox[0] + "COIN, ");
        System.out.println(strongbox[1] + "SERVANT, ");
        System.out.println(strongbox[2] + "SHIELD, ");
        System.out.println(strongbox[3] + "STONE.");
        System.out.println("In warehouse depots you have: ");
        if(warehouse[0].isEmpty())
            System.out.println("No resources in first shelf");
        else
            System.out.print(warehouse[0].size() + warehouse[0].get(0));
        if(warehouse[1].isEmpty())
            System.out.println("No resources in second shelf");
        else
            System.out.print(warehouse[1].size() + warehouse[1].get(0));
        if(warehouse[2].isEmpty())
            System.out.println("No resources in third shelf");
        else
            System.out.print(warehouse[2].size() + warehouse[2].get(0));
    }
}
