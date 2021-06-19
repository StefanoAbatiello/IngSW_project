package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.messages.NickNameAction;
import it.polimi.ingsw.messages.answerMessages.NumOfPlayersAnswer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SetupController implements GUIcontroller{
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
    @FXML
    TextField num_of_player;

    private GUI gui;
    private static  int port;
    private static String ip;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        SetupController.port = port;
    }

    public static String getIp() {
        return ip;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation.setText(confirmation);
    }

    public static void setIp(String ip) {
        SetupController.ip = ip;
    }

    @FXML
    public void setup_nickname() throws IOException {
        try {
            port = Integer.parseInt(port_field.getText());
            ip = ip_field.getText();

            if((port>=1024&&port<=65535)&&ip!=""){
                MainClient mainClient = new MainClient(ip, port, gui);
                gui.setMainClient(mainClient);
                System.out.println("mainclient created");
                new Thread(mainClient).start();
            }
            else
            {
                port_field.setText("");
                confirmation.setText("Insert valid port number & ip");
            }
        }catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }


        //gui.changeStage("Nickname.fxml");
    }



    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void waitingRoom(ActionEvent actionEvent) {
        gui.getMainClient().send(new NumOfPlayersAnswer(Integer.parseInt(num_of_player.getText())));
        //se il messaggio che ricevo è request num of player metto la schermata scegli numero, altrimenti metti nella schermata loading

    }

    public void sendNickname(ActionEvent actionEvent) {
        gui.getMainClient().send(new NickNameAction(nickname_field.getText()));
        //se il messaggio che ricevo è request num of player metto la schermata scegli numero, altrimenti metti nella schermata loading

    }
}

/**
 * confirmation.setText("Choose a valid nickname");
 */