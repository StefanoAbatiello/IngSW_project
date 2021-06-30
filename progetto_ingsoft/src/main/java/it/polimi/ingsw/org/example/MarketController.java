package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.MarketAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.awt.*;

public class MarketController implements GUIcontroller{
    @FXML
    GridPane market;


    private GUI gui;
    public void changeFirstRow(MouseEvent mouseEvent) {
            gui.getMainClient().send(new MarketAction(0));
    }

    public void changeSecondRow(MouseEvent mouseEvent) {
            gui.getMainClient().send(new MarketAction(1));
    }

    public void changeThirdRow(MouseEvent mouseEvent) {
            gui.getMainClient().send(new MarketAction(2));
    }

    public void changeFourthColumn(MouseEvent mouseEvent) {
            gui.getMainClient().send(new MarketAction(6));
    }

    public void changeThirdColumn(MouseEvent mouseEvent) {
            gui.getMainClient().send(new MarketAction(5));
    }

    public void changeSecondColumn(MouseEvent mouseEvent) {
            gui.getMainClient().send(new MarketAction(4));
    }

    public void changeFirstColumn(MouseEvent mouseEvent) {
            gui.getMainClient().send(new MarketAction(3));
    }

    public void changeMarket(String[][] info){
        for (int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                Circle circle=new Circle(24);
                circle.setFill(Color.web(info[i][j]));
                market.add(circle,j,i);

            }
        }
    }

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void goBack(ActionEvent actionEvent) {
        gui.changeStage("board.fxml");
    }
}