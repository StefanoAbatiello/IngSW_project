package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.SimplifiedModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class DevMatrixController implements GUIcontroller {
    @FXML
    GridPane devMatrixGrid;
    @FXML
    Button backButton;



    private GUI gui;



    @Override
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void setDevMatrix(SimplifiedModel info) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int id = info.getDevMatrix()[i][j];
                ImageView card = new ImageView();
                if(id==0)
                    card.setImage(new Image("org.example/images/Masters of Renaissance__Cards_BACK_3mmBleed-"+i+"-"+j+".png"));
                else
                    card.setImage(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + id + "-1.png"));
                card.setFitHeight(138.0);
                card.setFitWidth(104.0);
                devMatrixGrid.add(card, j, i);
                card.setId(String.valueOf(id));
                card.setOnMouseClicked(this::zoomCard);
        /*for (Node node : devMatrixGrid.getChildren()) {
                    if(devMatrixGrid.getRowIndex(node)!=null && devMatrixGrid.getColumnIndex(node)!=null && i==devMatrixGrid.getRowIndex(node) && j==devMatrixGrid.getColumnIndex(node)) {
                        ((ImageView) node).setImage(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-"+info.getDevMatrix()[i][j]+"-1.png"));
                    }
                }*/
            }
        }
    }

    public void goBack() {
        gui.changeStage("board.fxml");
    }

    public void zoomCard(MouseEvent mouseEvent) {
        ImageView target = (ImageView) mouseEvent.getTarget();
        int id = Integer.parseInt(target.getId());
        gui.changeStage("zoomedCard.fxml");
        ZoomController zoomController= (ZoomController) gui.getControllerFromName("zoomedCard.fxml");
        zoomController.setCard(id);

    }


    //todo aggiungo scelta slot


}