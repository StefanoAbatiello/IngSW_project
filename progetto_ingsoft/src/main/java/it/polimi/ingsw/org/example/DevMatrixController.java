package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.ViewCLI;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class DevMatrixController implements GUIcontroller{
    @FXML
    GridPane devMatrixGrid;

    private GUI gui;


    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void setDevMatrix(ViewCLI info){
        for(int i=0;i<4;i++){
            for(int j=0;j<3;j++){
                for (Node node : devMatrixGrid.getChildren()) {
                    if(devMatrixGrid.getRowIndex(node)!=null && devMatrixGrid.getColumnIndex(node)!=null && i==devMatrixGrid.getRowIndex(node) && devMatrixGrid.getColumnIndex(node)==j) {
                        ((ImageView) node).setImage(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + info.getDevMatrix()[i][j] + "-1.png"));
                    }
                }
            }
        }
    }

    public void goBack() {
        gui.changeStage("board.fxml");
    }

}