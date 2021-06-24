package it.polimi.ingsw.org.example;

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
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Optional;

public class BoardController implements GUIcontroller{
    @FXML
    VBox leadsBox;
    private GUI gui;

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

    public void showMarket(ActionEvent actionEvent) {
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
}
