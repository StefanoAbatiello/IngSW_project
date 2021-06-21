package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.messages.NickNameAction;
import it.polimi.ingsw.messages.answerMessages.NumOfPlayersAnswer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class SetupController implements GUIcontroller {
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
    @FXML
    ImageView card1;
    @FXML
    ImageView card2;
    @FXML
    ImageView card3;
    @FXML
    ImageView card4;
    @FXML
    HBox hbox;
    @FXML
    Label labelLeads;

    private GUI gui;
    private static int port;
    private static String ip;
    private ArrayList<Integer> selectedCards = new ArrayList<>();
    private int countLeads=0;

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

            if ((port >= 1024 && port <= 65535) && ip != "") {
                MainClient mainClient = new MainClient(ip, port, gui);
                gui.setMainClient(mainClient);
                System.out.println("mainclient created");
                new Thread(mainClient).start();
            } else {
                port_field.setText("");
                confirmation.setText("Insert valid port number & ip");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }


        //gui.changeStage("Nickname.fxml");
    }


    @Override
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void waitingRoom(ActionEvent actionEvent) {
        gui.getMainClient().send(new NumOfPlayersAnswer(Integer.parseInt(num_of_player.getText())));
        //se il messaggio che ricevo è request num of player metto la schermata scegli numero, altrimenti metti nella schermata loading

    }

    public void sendNickname(ActionEvent actionEvent) {
        gui.getMainClient().send(new NickNameAction(nickname_field.getText()));
        //se il messaggio che ricevo è request num of player metto la schermata scegli numero, altrimenti metti nella schermata loading

    }

    //TODO
    public void select_card(MouseEvent mouseEvent) {
        if(countLeads<2){
            countLeads++;
            ImageView target= (ImageView) mouseEvent.getTarget();
            String imageId= (target.getId());
            selectedCards.add(Integer.parseInt(imageId));
            System.out.println(imageId);
            target.setVisible(false);
            ((BoardController)gui.getControllerFromName("board.fxml")).setLeads(target);
        }
        else
           labelLeads.setText("You already chose 2 leads, if you want to change click RETRY, else NEXT ");
    }
    public void setLeads(ArrayList<Integer> cards){
        int card;
        for(Node image: hbox.getChildren()) {
            card = cards.get(0);
            ((ImageView) image).setImage(new Image("org.example/leadcards/Masters of Renaissance_Cards_FRONT_3mmBleed_1-" + card + "-1.png"));
            image.setId(String.valueOf(card));
            cards.remove(0);
        }
    }


    public void retry(MouseEvent mouseEvent) {
       ArrayList<Integer> cardsId= new ArrayList<>();
        for(Node image:hbox.getChildren()) {
            cardsId.add(Integer.parseInt(image.getId()));
            image.setVisible(true);
        }
        selectedCards.clear();
        countLeads=0;
        setLeads(cardsId);
    }

    public void leadsNext(){
        if(selectedCards.size()==2) {
        }
        else
            labelLeads.setText("Please choose 2 leads before pressing NEXT.If you want to change your choice, click RETRY");


    }
}

/**
 * confirmation.setText("Choose a valid nickname");
 */