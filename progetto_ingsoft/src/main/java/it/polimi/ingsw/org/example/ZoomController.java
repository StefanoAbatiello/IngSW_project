package it.polimi.ingsw.org.example;

import it.polimi.ingsw.messages.BuyCardAction;
import it.polimi.ingsw.messages.LobbyMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.MenuItem;



public class ZoomController implements GUIcontroller{
    @FXML
    ImageView zoomCard;
    @FXML
    MenuButton slots;
    @FXML
    Button viewBoard;
    @FXML
    Button buyButton;
    @FXML
    Button sendBuy;

    private int buyCard;
    private GUI gui;
    private int slot;

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void setCard(int id) {
        zoomCard.setImage(new Image("org.example/devcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + id + "-1.png"));
        buyCard = id;
        System.out.println(buyCard);/*debug*/
    }

    public void chooseSlot(ActionEvent actionEvent) {
        buyButton.setVisible(false);
        slots.setVisible(true);
        viewBoard.setVisible(true);
    }


    public void viewBoard(){
        gui.changeStage("board.fxml");
        BoardController controller=(BoardController) gui.getControllerFromName("board.fxml");
        controller.disableAll(false,true,false,true);
    }

    public void cardBuy(){
        if(slot==0)
            gui.getMainClient().send(new LobbyMessage("You haven't chosen a slot, please retry"));
        else {
            gui.getMainClient().send(new BuyCardAction(buyCard, slot));
            buyButton.setVisible(true);
            slots.setVisible(false);
            viewBoard.setVisible(false);
            sendBuy.setVisible(false);
            gui.changeStage("board.fxml");
        }
    }

    public void zoomedBack(ActionEvent actionEvent) {
        gui.changeStage("devMatrix.fxml");
        buyButton.setVisible(true);
        slots.setVisible(false);
        viewBoard.setVisible(false);
        sendBuy.setVisible(false);

    }

    public void saveSlot(ActionEvent actionEvent) {
        MenuItem target = (MenuItem) actionEvent.getTarget();
        slot=Integer.parseInt(target.getId());
        sendBuy.setVisible(true);
    }
}
