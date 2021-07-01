package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.ChangeChoosableAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public class WhiteMarbleController implements GUIcontroller{

    private GUI gui;
    private ArrayList<String> resourcesChosen;
    private String resourceChosen;
    private int numWhite;

    @FXML
    AnchorPane pane;
    @FXML
    ImageView resource1;
    @FXML
    ImageView resource2;
    @FXML
    Button resChosen;
    @FXML
    Label resLabel;

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

//TODO
    public void setChoosableRes(int num, ArrayList<String> resources) {
        resource1.setImage(new Image("org.example/images/" + resources.get(0) + ".png"));
        resource1.setId(resources.get(0));
        resource2.setImage(new Image("org.example/images/" + resources.get(1) + ".png"));
        resource2.setId(resources.get(1));
        numWhite=num;
        resetRes();
    }

    public void resetRes(){
        resLabel.setText("You can choose" + numWhite +"resources");
    }


    public void selectRes(MouseEvent mouseEvent) {
        String result= ((ImageView)mouseEvent.getTarget()).getId();
        for(Node node: pane.getChildren())
            node.setOpacity(1.0);
        ((ImageView)mouseEvent.getTarget()).setOpacity(0.6);
        resourceChosen=result;
        resChosen.setVisible(true);
    }

    public void sendResource(ActionEvent actionEvent) {
        for(Node node: pane.getChildren())
            node.setOpacity(1.0);
        if(numWhite==1) {
            resourcesChosen.add(resourceChosen);
            gui.getMainClient().send(new ChangeChoosableAction(resourcesChosen));
        }else {
            resourcesChosen.add(resourceChosen);
            numWhite--;
            resetRes();
        }
    }
}
