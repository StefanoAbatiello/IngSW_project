package it.polimi.ingsw.model.personalboard;

public class PersonalBoard {
    private FaithMarker faithMarker;
    private WarehouseDepots warehouseDepots;
    private StrongBox strongBox;
    private DevCardSlot devCardSlot;

    /**
     * create personal board with its component
     */
    public PersonalBoard(){
        faithMarker=new FaithMarker();
        warehouseDepots=new WarehouseDepots();
        strongBox=new StrongBox();
        devCardSlot=new DevCardSlot();
    }


    public FaithMarker getFaithMarker() {
        return faithMarker;
    }

    public WarehouseDepots getWarehouseDepots() {
        return warehouseDepots;
    }

    public StrongBox getStrongBox() {
        return strongBox;
    }

    public DevCardSlot getDevCardSlot() {
        return devCardSlot;
    }

}

/*
Scanner in = new Scanner(System.in);
            String s = in.next();
 */