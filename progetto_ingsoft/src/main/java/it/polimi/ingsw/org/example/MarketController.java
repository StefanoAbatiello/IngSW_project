package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.MarketAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class MarketController implements GUIcontroller{
    @FXML
    GridPane market;
    @FXML
    Button backButton;
    @FXML
    ImageView arrow0;
    @FXML
    ImageView arrow1;
    @FXML
    ImageView arrow2;
    @FXML
    ImageView arrow3;
    @FXML
    ImageView arrow4;
    @FXML
    ImageView arrow5;
    @FXML
    ImageView arrow6;




    private GUI gui;
    public void changeMarket(MouseEvent mouseEvent) {
        String target=((ImageView)mouseEvent.getTarget()).getId();
        target= target.replace("arrow","");
        System.out.println("numero freccia market: "+ target);
        gui.getMainClient().send(new MarketAction(Integer.parseInt(target)));
    }


    public void setMarket(String[][] info){
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