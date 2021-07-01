package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.ViewCLI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.personalboard.BlackCross;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.*;
import java.util.stream.Stream;

public class BoardController implements GUIcontroller{
    @FXML
    Pane board;
    @FXML
    VBox leadsBox;
    private GUI gui;
    @FXML
    ImageView shelf1;
    @FXML
    HBox shelf2;
    @FXML
    HBox shelf3;
    @FXML
    ImageView croce;
    @FXML
    VBox buttonsVbox;
    @FXML
    ComboBox in1;
    @FXML
    ComboBox in2;
    @FXML
    ComboBox out;
    @FXML
    ImageView card1;
    @FXML
    ImageView card2;
    @FXML
    ImageView card3;
    @FXML
    Button viewBack;
    @FXML
    ComboBox chooseLeadOut;
    @FXML
    Button endChoose;
    @FXML
    Button production;
    @FXML
    Button endProd;
    @FXML
    Label leadOutLabel;
    @FXML
    GridPane allDevs;
    @FXML
    Button backDevs;
    @FXML
    Button backSupply;
    @FXML
    Button backProd;
    @FXML
    Button viewDevs;
    @FXML
    ImageView pope1;
    @FXML
    ImageView pope2;
    @FXML
    ImageView pope3;
    @FXML
    Label coinBox;
    @FXML
    Label servantBox;
    @FXML
    Label shieldBox;
    @FXML
    Label stoneBox;
    @FXML
    HBox sShelf1;
    @FXML
    HBox sShelf2;
    @FXML
    ImageView lead1;
    @FXML
    ImageView lead2;
    @FXML
    ImageView blackCross;


    private ArrayList<Integer> prodCards= new ArrayList<>();
    private boolean productionActive=false;
    private String[] personalProdIn= new String[2];
    private String output= "";
    //TODO scelta leadout
    private ArrayList<String> leadOut= new ArrayList<>();
    private String lead1Id= "lead1";
    private String lead2Id= "lead2";
    private boolean shelf4= false;
    private boolean shelf5= false;

    private String card1ID="card1";
    private String card2ID="card2";
    private String card3ID="card3";

    private int space1=0;
    private int space2=0;
    private int space3=0;

    private int blackCrossPos=0;

    private int oldRedCrossPosition=0;
    private int oldBlackCrossPosition=0;



    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void setLeads(ViewCLI viewCLI) {
        int card;
        Set<Integer> leadsIdSet= viewCLI.getLeadCardsId().keySet();
        ArrayList<Integer> leadsId= new ArrayList<>(leadsIdSet);
        for(Node image: leadsBox.getChildren()) {
            card = leadsId.get(0);
            ((ImageView) image).setImage(new Image("org.example/leadcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + card + "-1.png"));
            image.setId(String.valueOf(card));
            image.setOpacity(0.7);
            if(lead1Id.equals("lead1"))
                lead1Id=image.getId();
            else
                lead2Id= image.getId();
            leadsId.remove(0);
        }
    }

    public void addDev(String id,int slot){
       switch (slot) {
           case 0:
               ((ImageView)board.lookup("#"+card1ID)).setImage(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + id + "-1.png"));
               board.lookup("#"+card1ID).setId(id);
               card1ID=id;
               break;
           case 1:
               ((ImageView)board.lookup("#"+card2ID)).setImage(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + id + "-1.png"));
               board.lookup("#"+card2ID).setId(id);
               card2ID=id;
               break;
           case 2:
               ((ImageView)board.lookup("#"+card3ID)).setImage(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + id + "-1.png"));
               board.lookup("#"+card3ID).setId(id);
               card3ID=id;
               break;
       }
    }

    public void showMarket() {
        gui.changeStage("provabiglia.fxml");
    }

    public int checkSpecial(){
        if(shelf4 && shelf5)
            return 2;
        else
            if(shelf4)
                return 1;
        else
            return 0;
    }

    public void showLeadsAction(MouseEvent mouseEvent) {
        ImageView target = (ImageView) mouseEvent.getTarget();
        int imageId = Integer.parseInt((target.getId()));

        ButtonType discard = new ButtonType("discard");
        ButtonType active = new ButtonType("active");
        ButtonType production= new ButtonType("production");
        Alert alert;
        if(!productionActive) {
            alert = new Alert(Alert.AlertType.INFORMATION, "choice action", discard, active);
            alert.setTitle("Leads Actions");
        }else{
            alert = new Alert(Alert.AlertType.INFORMATION, "use this production",production);
            alert.setTitle("Leads Production");
        }

        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> alert.hide());

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(res -> {
            if (res.equals(discard))
                gui.getMainClient().send(new DiscardLeadAction(imageId));
            else if (res.equals(active))
                gui.getMainClient().send(new ActiveLeadAction(imageId));
            else if(res.equals(production)){
                prodCards.add(imageId);
                chooseLeadOut();
            }

        });

    }

    public void showDevMatrix(ActionEvent actionEvent) {
        gui.changeStage("devMatrix.fxml");

    }

    public void uploadRedCrossPosition(int newPosition) {
        final int shift = 35;
        if(newPosition>oldRedCrossPosition || newPosition==0) {
            if (newPosition == 0) {
                croce.setLayoutX(46.0);
                croce.setLayoutY(176.0);
            } else if (newPosition > 0 && newPosition <= 2) {
                croce.setLayoutX(croce.getLayoutX() + shift);
            } else if (newPosition <= 4) {
                croce.setLayoutY(croce.getLayoutY() - shift);
            } else if (newPosition <= 9) {
                croce.setLayoutX(croce.getLayoutX() + shift);
            } else if (newPosition <= 11) {
                croce.setLayoutY(croce.getLayoutY() + shift);
            } else if (newPosition <= 16)
                croce.setLayoutX(croce.getLayoutX() + shift);
            else if (newPosition <= 18) {
                croce.setLayoutY(croce.getLayoutY() + shift);
            } else if (newPosition <= 24) {
                croce.setLayoutX(croce.getLayoutX() + shift);
            }
            oldRedCrossPosition= newPosition;
        }
    }

    public void uploadBlackCrossPosition(int newPosition) {
        final int shift = 35;
        if(newPosition>oldBlackCrossPosition || newPosition==0) {
            if (newPosition == 0) {
                blackCross.setLayoutX(46.0);
                blackCross.setLayoutY(176.0);
            } else if (newPosition > 0 && newPosition <= 2) {
                blackCross.setLayoutX(blackCross.getLayoutX() + shift);
            } else if (newPosition <= 4) {
                blackCross.setLayoutY(blackCross.getLayoutY() - shift);
            } else if (newPosition <= 9) {
                blackCross.setLayoutX(blackCross.getLayoutX() + shift);
            } else if (newPosition <= 11) {
                blackCross.setLayoutY(blackCross.getLayoutY() + shift);
            } else if (newPosition <= 16)
                blackCross.setLayoutX(blackCross.getLayoutX() + shift);
            else if (newPosition <= 18) {
                blackCross.setLayoutY(blackCross.getLayoutY() + shift);
            } else if (newPosition <= 24) {
                blackCross.setLayoutX(blackCross.getLayoutX() + shift);
            }
            oldBlackCrossPosition= newPosition;
        }
    }

    public void setWareHouse(ViewCLI viewCLI) {

        emptyShelves();

        if(!viewCLI.getWarehouse()[0].isEmpty()) {
            shelf1.setImage(new Image("org.example/images/" + viewCLI.getWarehouse()[0].get(0) + ".png"));
            shelf1.setOpacity(1.0);
        }
        System.out.println(viewCLI.getWarehouse()[0]);

        for(int i=0;i<viewCLI.getWarehouse()[1].size();i++){
            System.out.println(viewCLI.getWarehouse()[1]);
            ImageView image= (ImageView) shelf2.getChildren().get(i);
            image.setImage(new Image("org.example/images/"+viewCLI.getWarehouse()[1].get(i)+".png"));
            image.setOpacity(1.0);
        }
        for(int i=0;i<viewCLI.getWarehouse()[2].size();i++){
            System.out.println(viewCLI.getWarehouse()[2]);
            ImageView image= (ImageView) shelf3.getChildren().get(i);
            image.setImage(new Image("org.example/images/"+viewCLI.getWarehouse()[2].get(i)+".png"));
            image.setOpacity(1.0);
        }

        setSpecialShelf(viewCLI);
    }

    private void emptyShelves() {
        shelf1.setImage(null);

        for(Node node: shelf2.getChildren())
            ((ImageView)node).setImage(null);
        for(Node node: shelf3.getChildren())
            ((ImageView)node).setImage(null);
        /*for(Node node: shelf2.getChildren())
            ((ImageView)node).setImage(null);
        for(Node node: shelf2.getChildren())
            ((ImageView)node).setImage(null);*/
    }

    private void setSpecialShelf(ViewCLI viewCLI) {
        if(!viewCLI.getWarehouse()[3].isEmpty())
            for(int i=0;i<viewCLI.getWarehouse()[3].size();i++){
                System.out.println(viewCLI.getWarehouse()[3]);
                //considero che se mi arriva c'è sicuramente attiva
                HBox special= (HBox) board.lookup("#shelf4");
                ImageView image= (ImageView) special.getChildren().get(i);
                image.setImage(new Image("org.example/images/"+viewCLI.getWarehouse()[3].get(i)+".png"));
                image.setOpacity(1.0);
            }
        if(!viewCLI.getWarehouse()[4].isEmpty())
            for(int i=0;i<viewCLI.getWarehouse()[4].size();i++){
                System.out.println(viewCLI.getWarehouse()[4]);
                HBox special= (HBox) board.lookup("#shelf5");
                ImageView image= (ImageView) special.getChildren().get(i);
                image.setImage(new Image("org.example/images/"+viewCLI.getWarehouse()[4].get(i)+".png"));
                image.setOpacity(1.0);
            }

    }

    //production
    public void activateProds(ActionEvent actionEvent) {
        disableAll(false,false,true,false);

        in1.setOnAction((event) -> {
            Label selectedItem = (Label)in1.getSelectionModel().getSelectedItem();
            insertIn1(selectedItem);
        });
        in2.setOnAction((event) -> {
            Label selectedItem = (Label)in2.getSelectionModel().getSelectedItem();
            insertIn2(selectedItem);
        });
        out.setOnAction((event) -> {
            Label selectedItem = (Label)out.getSelectionModel().getSelectedItem();
            insertOut(selectedItem);
        });
        board.lookup("#"+card1ID).setDisable(false);
        System.out.println(board.lookup("#"+card1ID).getId());
        board.lookup("#"+card2ID).setDisable(false);
        System.out.println(board.lookup("#"+card2ID).getId());
        board.lookup("#"+card3ID).setDisable(false);
        System.out.println(board.lookup("#"+card3ID).getId());
        productionActive=true;
        endProd.setVisible(true);
        backProd.setVisible(true);

    }

    public void cardProd(MouseEvent mouseEvent) {
        ImageView card= (ImageView) mouseEvent.getTarget();
        if(!card.getId().equals("card1") && !card.getId().equals("card2") && !card.getId().equals("card3")) {
            card.setOpacity(0.8);
            int id = Integer.parseInt(card.getId());
            if (!prodCards.contains(id))
                prodCards.add(id);
            else
                gui.lobbyMessageHandler(new LobbyMessage("You've already chosen the production of this card"));
            //prodCards.remove(id);
        }else
            gui.lobbyMessageHandler(new LobbyMessage("You do not have cards in this slot"));
    }

    public void chooseLeadOut(){
        disableAll(false,false,false,false);
        leadsBox.setVisible(false);
        chooseLeadOut.setVisible(true);
        leadOutLabel.setVisible(true);
        chooseLeadOut.setOnAction((event) -> {
            Label selectedItem = (Label)chooseLeadOut.getSelectionModel().getSelectedItem();
            insertLeadOut(selectedItem);
        });
    }

    public void insertLeadOut(Label selectedItem) {
        String id= selectedItem.getText();
        System.out.println(id);
        leadOut.add(id);
        endChoose.setVisible(true);
    }

    public void endChooseLeadOut(ActionEvent actionEvent){
        endChoose.setVisible(false);
        disableAll(true,true,true,false);
        leadsBox.setVisible(true);
        production.setVisible(false);
        chooseLeadOut.setVisible(false);
        leadOutLabel.setVisible(false);
        if(leadOut.get(leadOut.size()-1).equals("none")) {
            prodCards.remove(prodCards.size() - 1);
            leadOut.remove(leadOut.size()-1);
        }
    }

    public void insertIn1(Label label) {
        String id= label.getText();
        System.out.println(id);
        if(id.equals("none"))
            personalProdIn[0]=null;
        else
            personalProdIn[0]=id;
    }

    public void insertIn2(Label label) {
        String id= label.getText();
        System.out.println(id);
        if(id.equals("none"))
            personalProdIn[1]=null;
        personalProdIn[1]=id;
    }

    public void insertOut(Label label) {
        String id= label.getText();
        System.out.println(id);
        if(id.equals("none"))
            output=null;
        else
            output=id;
    }

    public void sendProd(ActionEvent actionEvent){
        //array to arraylist for personal prod in
        ArrayList<String> prodIn= new ArrayList<>();
        prodIn.add(personalProdIn[0]);
        prodIn.add(personalProdIn[1]);
        //TODO controllo cosa succede se mando tutto vuoto
        System.out.println("prodCards: " + prodCards);
        System.out.println("prodIn: "+ prodIn);
        System.out.println("output: "+ output);
        System.out.println("leadout: " + leadOut);
        gui.getMainClient().send(new ProductionAction(prodCards, prodIn,output,leadOut));
        backProd();
    }

    public void backProd(){
        prodCards= new ArrayList<>();
        productionActive=false;
        personalProdIn= new String[2];
        output= "";
        disableAll(true,false,false,false);
        board.lookup("#"+card1ID).setDisable(true);
        board.lookup("#"+card2ID).setDisable(true);
        board.lookup("#"+card3ID).setDisable(true);
        productionActive=false;
        //non può rifare la produzione, la lascio invisibile, oppure posso fare main già scelta
        endProd.setVisible(false);
        backProd.setVisible(false);

    }

    //enable buttons
    public void disableAll(boolean buttons,boolean leads, boolean personal, boolean back) {
        for (Node button : buttonsVbox.getChildren())
            button.setVisible(buttons);
        for (Node card : leadsBox.getChildren())
            card.setDisable(leads);
        in1.setVisible(personal);
        in2.setVisible(personal);
        out.setVisible(personal);
        viewBack.setVisible(back);

    }

    public void viewBack(ActionEvent actionEvent) {
        for(Node button: buttonsVbox.getChildren())
            button.setVisible(true);
        for (Node card : leadsBox.getChildren())
            card.setDisable(false);
        in1.setVisible(false);
        in2.setVisible(false);
        out.setVisible(false);
        viewBack.setVisible(false);
        gui.changeStage("zoomedCard.fxml");
        System.out.println("change made");

    }

    public void supplyBack(ActionEvent actionEvent) {
        for(Node button: buttonsVbox.getChildren())
            button.setVisible(true);
        for (Node card : leadsBox.getChildren())
            card.setDisable(false);
        in1.setVisible(false);
        in2.setVisible(false);
        out.setVisible(false);
        backSupply.setVisible(false);
        gui.changeStage("supply.fxml");
        System.out.println("change made");

    }

    public void endTurn(ActionEvent actionEvent) {
        gui.getMainClient().send(new TurnChangeMessage());

    }

    public void setCards(ViewCLI viewCLI) {
        Set<Integer> keySet = viewCLI.getLeadCardsId().keySet();
        ArrayList<Integer> updatedCards = new ArrayList<>(keySet);
        for(Node node: leadsBox.getChildren()) {
            if (!node.getId().equals(lead1Id) && !node.getId().equals(lead2Id)) {
                if (!updatedCards.contains(Integer.parseInt(node.getId())))
                    ((ImageView) node).setImage(new Image("/org.example/images/Masters of Renaissance__Cards_BACK_3mmBleed-49-1.png"));
                else if (viewCLI.getLeadCardsId().get(Integer.parseInt(node.getId())))
                    node.setOpacity(1.0);
            }
        }
        keySet = viewCLI.getDevCardsId().keySet();
        updatedCards = new ArrayList<>(keySet);
        System.out.println(updatedCards);
        for(Node node: allDevs.getChildren())
            ((ImageView)node).setImage(null);
        for(int id: updatedCards) {
            String stringId = String.valueOf(id);
            updateDevs(stringId, viewCLI.getDevPositions().get(id));
            if (!stringId.equals(card1ID) && !stringId.equals(card2ID) && !stringId.equals(card3ID)) {
                if (viewCLI.getDevCardsId().get(id)) {
                    addDev(stringId, viewCLI.getDevPositions().get(id));
                    //updateDevs(stringId, viewCLI.getDevPositions().get(id));
                }
            }
        }
    }

    private void updateDevs(String stringId, Integer slot) {
        ImageView card= new ImageView(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + stringId + "-1.png"));
        card.setFitWidth(150.0);
        card.setFitHeight(200.0);
        switch(slot){
            case 0:
                allDevs.add(card,slot,space1);
                space1++;
                break;
            case 1:
                allDevs.add(card,slot,space2);
                space2++;
                break;
            case 2:
                allDevs.add(card,slot,space3);
                space3++;
                break;
        }
        GridPane.setMargin(card,new Insets(50,0,0,30));

    }

    public void viewAllDevs(ActionEvent actionEvent){
        if(allDevs.getChildren().size()==0)
            gui.lobbyMessageHandler(new LobbyMessage("You do not own any development card yet"));
        else {
            viewDevs.setVisible(false);
            allDevs.setVisible(true);
            disableAll(false, true, false, false);
            board.lookup("#"+card1ID).setVisible(false);
            board.lookup("#"+card2ID).setVisible(false);
            board.lookup("#"+card3ID).setVisible(false);
            for (Node card : leadsBox.getChildren())
                card.setVisible(false);
            backDevs.setVisible(true);
        }
    }

    public void devsBack(ActionEvent actionEvent){
        allDevs.setVisible(false);
        viewDevs.setVisible(true);
        disableAll(true,false,false,false);
        backDevs.setVisible(false);
        board.lookup("#"+card1ID).setVisible(true);
        board.lookup("#"+card2ID).setVisible(true);
        board.lookup("#"+card3ID).setVisible(true);
        for (Node card : leadsBox.getChildren())
            card.setVisible(true);
    }


    public void setStrongbox(ViewCLI viewCLI) {
        coinBox.setText( String.valueOf(viewCLI.getStrongbox()[0]));
        servantBox.setText( String.valueOf(viewCLI.getStrongbox()[1]));
        shieldBox.setText( String.valueOf(viewCLI.getStrongbox()[2]));
        stoneBox.setText( String.valueOf(viewCLI.getStrongbox()[3]));
    }

    public void activePope(int meetingNumber) {
        switch (meetingNumber){
            case 1:
                pope1.setVisible(true);
                break;
            case 2:
                pope2.setVisible(true);
                break;
            case 3:
                pope3.setVisible(true);
                break;
        }
    }

    public void leadShelfActivation(int cardId) {

        if (leadsBox.lookup("#" + lead1Id).getId().equals(String.valueOf(cardId))) {
            if(!shelf4)
                sShelf1.setId("shelf4");
            else {
                sShelf1.setId("shelf5");
                shelf5=true;
            }
        }
        if (leadsBox.lookup("#" + lead2Id).getId().equals(String.valueOf(cardId))) {
            if(!shelf4)
                sShelf2.setId("shelf4");
            else
            {
                sShelf2.setId("shelf5");
                shelf5=true;
            }        }
        shelf4=true;
    }

    public void lorenzoUpdate(int val) {
            if(val>=0) {
                blackCross.setVisible(true);
                System.out.println("old position of BC: "+oldBlackCrossPosition);
                System.out.println("new position of BC: "+(val));
                uploadBlackCrossPosition(val);
            }else
                blackCross.setVisible(false);
    }
}
