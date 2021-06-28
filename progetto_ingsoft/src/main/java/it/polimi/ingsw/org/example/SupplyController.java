package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.ResourceInSupplyAction;
import it.polimi.ingsw.messages.answerMessages.WareHouseChangeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class SupplyController implements GUIcontroller{
    @FXML
    HBox supply;
    @FXML
    MenuButton shelfMenu;


    private GUI gui;
    private String selectedResource;





    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void selectShelf1(ActionEvent actionEvent) {
        System.out.println("sono qui");

        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println("finito");
        gui.getViewCLI().getWarehouse()[0].add(selectedResource);


    }

    public void selectShelf2(ActionEvent actionEvent) {
        System.out.println("sono qui");

        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println("finito");
        gui.getViewCLI().getWarehouse()[1].add(selectedResource);
    }

    public void selectShelf3(ActionEvent actionEvent) {
        System.out.println("sono qui");

        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println("finito");
        gui.getViewCLI().getWarehouse()[2].add(selectedResource);
    }



    public void setSupply(ArrayList<String> resources) {
        String resource;
        for(Node image: supply.getChildren()){
            if(!resources.isEmpty()) {
                resource = resources.get(0);
                ((ImageView) image).setImage(new Image("org.example/images/" + resource + ".png"));
                image.setId(resource);
                resources.remove(0);
            }
        }

    }

    public void select_resource(MouseEvent mouseEvent) {
        for(Node res:supply.getChildren())
            res.setMouseTransparent(true);
        ImageView target= (ImageView) mouseEvent.getTarget();
        selectedResource= (target.getId());
        target.setOpacity(0.8);

        shelfMenu.setVisible(true);
    }

    public void sendResource() {
        gui.getMainClient().send(new ResourceInSupplyAction(gui.getViewCLI().getWarehouse()));
        gui.changeStage("board.fxml");
    }

    public void goBack() {
        gui.changeStage("board.fxml");
    }
}
