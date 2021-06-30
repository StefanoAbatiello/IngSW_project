package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.WinnerMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.event.WindowEvent;

public class WinnerController implements GUIcontroller{

    @FXML
    Label winnerLabel;

    private GUI gui;

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void endGameAgain(ActionEvent actionEvent) {
        gui.getMainClient().send(new WinnerMessage("yes"));
    }

    public void endGameQuit(ActionEvent actionEvent) {
        gui.getMainClient().send(new WinnerMessage("no"));
        gui.getMainClient().disconnect();
        //TODO esci dal gioco
    }
}
