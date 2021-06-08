package it.polimi.ingsw.org.example;

import it.polimi.ingsw.ClientCLI;
import it.polimi.ingsw.messages.NickNameAction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class PrimaryController implements GUIcontroller{
    @FXML
    TextField nickname_field;
    @FXML
    TextField ip_field;
    @FXML
    TextField port_field;
    @FXML
    Label confirmation;
    @FXML
    Label validation;
    private GUI gui;
    private static  int port;
    private static String ip;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        PrimaryController.port = port;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        PrimaryController.ip = ip;
    }

    @FXML
    public void setup_nickname() throws IOException {

        try {
            port = Integer.parseInt(port_field.getText());
            ip = ip_field.getText();

            ClientCLI clientCLI = new ClientCLI(ip, port, true);

            GUIcontroller guIcontroller;
            gui.changeStage("Nickname.fxml");
            guIcontroller = (PrimaryController) gui.getControllerFromName("Nickname.fxml");
        }catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

    }

    public void startgame() {

        if(nickname_field.getText().equals("")){
            confirmation.setText("Choose a valid nickname");
        }
        else
        {
            gui.getClientHandler().send(new NickNameAction(nickname_field.getText()));
            //se il messaggio che ricevo è request num of player metto la schermata scegli numero, altrimenti metti nella schermata loading
            gui.changeStage("board.fxml");
        }
    }

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }
}