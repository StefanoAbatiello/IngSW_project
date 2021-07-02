package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.QuitMessage;
import it.polimi.ingsw.messages.WinnerMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.event.WindowEvent;

public class WinnerController implements GUIcontroller{

    @FXML
    Label winnerLabel;

    private GUI gui;

    public void setWinnerLabel(String string){
        winnerLabel.setText(string);
    }

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void endGameQuit(ActionEvent actionEvent) {
        gui.getMainClient().send(new QuitMessage());
        gui.getMainClient().disconnect();
        gui.getStage().close();
    }
}
