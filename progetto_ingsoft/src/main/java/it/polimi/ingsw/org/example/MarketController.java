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
    @FXML
    Label errormarket;


    private GUI gui;
    private int count=0;
    public void changeFirstRow(MouseEvent mouseEvent) {
        if(count<1) {
            gui.getMainClient().send(new MarketAction(0));
            count++;
        }
        else
            errormarket.setVisible(true);

    }

    public void changeSecondRow(MouseEvent mouseEvent) {
        if(count<1) {
            gui.getMainClient().send(new MarketAction(1));
            count++;
        }
        else
            errormarket.setVisible(true);
    }

    public void changeThirdRow(MouseEvent mouseEvent) {
        if(count<1) {
            gui.getMainClient().send(new MarketAction(2));
            count++;
        }
        else
            errormarket.setVisible(true);
    }

    public void changeFourthColumn(MouseEvent mouseEvent) {
        if(count<1){
            gui.getMainClient().send(new MarketAction(6));
            count++;
        }
        else
            errormarket.setVisible(true);
    }

    public void changeThirdColumn(MouseEvent mouseEvent) {
        if(count<1) {
            gui.getMainClient().send(new MarketAction(5));
            count++;
        }
        else
            errormarket.setVisible(true);
    }

    public void changeSecondColumn(MouseEvent mouseEvent) {
        if(count<1) {
            gui.getMainClient().send(new MarketAction(4));
            count++;
        }
        else
            errormarket.setVisible(true);
    }

    public void changeFirstColumn(MouseEvent mouseEvent) {
        if(count<1) {
            gui.getMainClient().send(new MarketAction(3));
            count++;
        }
        else
            errormarket.setVisible(true);
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