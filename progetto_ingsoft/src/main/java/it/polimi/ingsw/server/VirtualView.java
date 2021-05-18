package it.polimi.ingsw.server;

//import sun.jvm.hotspot.utilities.Observable;

import java.util.ArrayList;

public class VirtualView /*extends Observable*/ {

    private String[][] virtualMarket;
    private int[][] virtualDevCards;
    private ArrayList<Integer> virtualFaithPos;

    public VirtualView(String[][] virtualMarket, int[][] virtualDevCards, ArrayList<Integer> virtualFaithPos) {
        this.virtualMarket = virtualMarket;
        this.virtualDevCards = virtualDevCards;
        this.virtualFaithPos = virtualFaithPos;
    }
}
