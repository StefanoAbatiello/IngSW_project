package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.ViewCLI;
import it.polimi.ingsw.messages.ActiveLeadAction;
import it.polimi.ingsw.messages.DiscardLeadAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import javafx.scene.input.MouseEvent;

public class BoardController implements GUIcontroller{
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


    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void setLeads(ArrayList<Integer> cards) {
        int card;
        for(Node image: leadsBox.getChildren()) {
            card = cards.get(0);
            ((ImageView) image).setImage(new Image("org.example/leadcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + card + "-1.png"));
            image.setId(String.valueOf(card));
            image.setOpacity(0.8);
            cards.remove(0);
        }
    }

    public void showMarket() {
        gui.changeStage("provabiglia.fxml");
    }

    public void showLeadsAction(MouseEvent mouseEvent) {
        ImageView target= (ImageView) mouseEvent.getTarget();
        int imageId= Integer.parseInt((target.getId()));

        ButtonType discard=new ButtonType("discard");
        ButtonType active=new ButtonType("active");
        Alert alert=new Alert(Alert.AlertType.INFORMATION,"choice action",discard,active);
        alert.setTitle("Leads Actions");
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> alert.hide());

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(res->{
            if(res.equals(discard))
                gui.getMainClient().send(new DiscardLeadAction(imageId));
            else if(res.equals(active))
                gui.getMainClient().send(new ActiveLeadAction(imageId));
        });

    }


    public void buyCard(MouseEvent mouseEvent) {
    }

    public void showDevMatrix(ActionEvent actionEvent) {
        gui.changeStage("devMatrix.fxml");
    }


    public void uploadPosition(int newPosition) {
        final int shift = 25;

        if (newPosition == 0) {
            croce.setLayoutX(18);
            croce.setLayoutY(115);
        } else if (newPosition > 0 && newPosition <= 2) {
            croce.setLayoutX(croce.getLayoutX() + shift);
        } else if (newPosition <= 4) {
            croce.setLayoutY(croce.getLayoutY() + shift);
        } else if (newPosition <= 9) {
            croce.setLayoutX(croce.getLayoutX() + shift);
        } else if (newPosition <= 11) {
            croce.setLayoutY(croce.getLayoutY() - shift);
        } else if (newPosition <= 16)
            croce.setLayoutX(croce.getLayoutX() + shift);
        else if (newPosition <= 18) {
            croce.setLayoutY(croce.getLayoutY() + shift);
        } else if (newPosition <= 24) {
            croce.setLayoutX(croce.getLayoutX() + shift);
        }
    }

    public void setWareHouse(ViewCLI viewCLI) {
        if(!viewCLI.getWarehouse()[0].isEmpty())
            shelf1.setImage(new Image("org.example/images/"+viewCLI.getWarehouse()[0].get(0)+".png"));
        System.out.println(viewCLI.getWarehouse()[0]);

        for(int i=0;i<viewCLI.getWarehouse()[1].size();i++){
            System.out.println(viewCLI.getWarehouse()[1]);
            ImageView image= (ImageView) shelf2.getChildren().get(i);
            image.setImage(new Image("org.example/images/"+viewCLI.getWarehouse()[1].get(i)+".png"));
        }
        for(int i=0;i<viewCLI.getWarehouse()[2].size();i++){
            System.out.println(viewCLI.getWarehouse()[2]);
            ImageView image= (ImageView) shelf2.getChildren().get(i);
            image.setImage(new Image("org.example/images/"+viewCLI.getWarehouse()[2].get(i)+".png"));
        }
    }
}
