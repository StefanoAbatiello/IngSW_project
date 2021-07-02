package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.ResourceInSupplyAction;
import it.polimi.ingsw.messages.answerMessages.WareHouseChangeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class SupplyController implements GUIcontroller{
    @FXML
    HBox supply;
    @FXML
    MenuButton shelfMenu;
    @FXML
    MenuItem shelf4;
    @FXML
    MenuItem shelf5;
    @FXML
    Button viewBoard;


    private GUI gui;
    private String selectedResource;
    private ArrayList<String>[] newWarehouse;




    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void selectShelf1(ActionEvent actionEvent) {
        System.out.println("shelf 1");
        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println("newWarehouse[1]: "+selectedResource);
        newWarehouse[0].add(selectedResource);


    }

    public void selectShelf2(ActionEvent actionEvent) {
        System.out.println("shelf2");

        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println("newWarehouse[2]: "+selectedResource);
        newWarehouse[1].add(selectedResource);
    }

    public void selectShelf3(ActionEvent actionEvent) {
        System.out.println("shelf3");

        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println("newWarehouse[3]: "+selectedResource);
        newWarehouse[2].add(selectedResource);
    }

    public void selectShelf4(ActionEvent actionEvent) {
        System.out.println("sono qui");

        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println("newWarehouse[4]: "+selectedResource);
        newWarehouse[3].add(selectedResource);
    }

    public void selectShelf5(ActionEvent actionEvent) {
        System.out.println("sono qui");

        for(Node res:supply.getChildren()){
            res.setMouseTransparent(false);
            res.setOpacity(1.0);
        }
        shelfMenu.setVisible(false);
        System.out.println(("newWarehouse[5]: "+selectedResource));
        newWarehouse[4].add(selectedResource);
    }



    public void setSupply(ArrayList<String> resources) {
        String resource;
        newWarehouse=gui.getViewCLI().getWarehouse();
        for(Node image: supply.getChildren()){
            ((ImageView)image).setImage(null);
            if(!resources.isEmpty()) {
                resource = resources.get(0);
                ((ImageView) image).setImage(new Image(getClass().getResourceAsStream("/org.example/images/" + resource.toLowerCase() + ".png")));
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

        if(((BoardController)(gui.getControllerFromName("board.fxml"))).checkSpecial()==1) {
            System.out.println("sono checkSpecial gui ==1");
            shelf4.setVisible(true);
        }else
            if(((BoardController)(gui.getControllerFromName("board.fxml"))).checkSpecial()==2) {
                System.out.println("sono checkSpecial gui ==2");
                shelf4.setVisible(true);
                shelf5.setVisible(true);
            }
        shelfMenu.setVisible(true);
    }

    public void sendResource() {
        System.out.println(newWarehouse);
        gui.getMainClient().send(new ResourceInSupplyAction(newWarehouse));
        gui.changeStage("board.fxml");
    }

    public void viewBoard(){
        gui.changeStage("board.fxml");
        BoardController controller=(BoardController) gui.getControllerFromName("board.fxml");
        controller.disableAll(false,true,false,false);
        controller.backSupply.setVisible(true);
    }
}