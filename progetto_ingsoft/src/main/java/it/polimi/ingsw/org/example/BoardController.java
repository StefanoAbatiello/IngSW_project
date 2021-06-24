package it.polimi.ingsw.org.example;

import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;

public class BoardController implements GUIcontroller{
    private GUI gui;

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void setLeads(ImageView target) {
    }

    public void showMarket(ActionEvent actionEvent) {
        gui.changeStage("provabiglia.fxml");
    }
}
