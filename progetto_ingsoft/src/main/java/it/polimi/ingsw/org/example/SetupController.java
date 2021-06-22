package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.messages.ChosenLeadMessage;
import it.polimi.ingsw.messages.InitialResourceMessage;
import it.polimi.ingsw.messages.NickNameAction;
import it.polimi.ingsw.messages.answerMessages.NumOfPlayersAnswer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
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
    Label errorLabel;
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
    @FXML
    Label labelRes;
    @FXML
    HBox hboxRes;
    @FXML
    Label initialResLabel;
    @FXML
    MenuButton shelfMenu;

    private GUI gui;
    private static int port;
    private static String ip;
    private ArrayList<Integer> selectedCards = new ArrayList<>();
    private ArrayList<String> selectedRes = new ArrayList<>();
    private ArrayList<Integer> selectedShelf = new ArrayList<>();

    private int countLeads=0;
    private int initialRes;
    private int countRes=0;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        SetupController.port = port;
    }

    public static String getIp() {
        return ip;
    }

    public void setErrorLabel(String confirmation) {
        this.errorLabel.setText(confirmation);
        this.nickname_field.setText("");
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
                errorLabel.setText("Insert valid port number & ip");
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
            target.setOpacity(0.8);
            //((BoardController)gui.getControllerFromName("board.fxml")).setLeads(target);
        }
        else
           errorLabel.setText("You already chose 2 leads, if you want to change click RETRY, else NEXT ");
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
            gui.getMainClient().send(new ChosenLeadMessage(selectedCards));
            gui.changeStage("waiting.fxml");
        }
        else
            errorLabel.setText("Please choose 2 leads before pressing NEXT.If you want to change your choice, click RETRY");


    }

    public void setLabelResources(String message) {
        errorLabel.setText(message);
    }

    public void setInitialRes(int numRes) {
        initialRes=numRes;
    }

    public void select_resource(MouseEvent mouseEvent) {
            if(countRes<initialRes){
                countRes++;
                for(Node res:hboxRes.getChildren())
                    res.setMouseTransparent(true);
                ImageView target= (ImageView) mouseEvent.getTarget();
                String imageId= (target.getId());
                selectedRes.add(imageId);
                System.out.println(imageId);
                shelfMenu.setVisible(true);
                //((BoardController)gui.getControllerFromName("board.fxml")).setLeads(target);
            }
            else
                errorLabel.setText("You have already chosen "+initialRes+ "resources, if you want to change click RETRY, else NEXT ");
        }

    public void selectShelf1(MouseEvent mouseEvent) {
        selectedShelf.add(1);
        for(Node res:hbox.getChildren())
            res.setMouseTransparent(false);
        shelfMenu.setVisible(false);
         }
    public void selectShelf2(MouseEvent mouseEvent) {
        selectedShelf.add(2);
        for(Node res:hbox.getChildren())
            res.setMouseTransparent(false);
        shelfMenu.setVisible(false);

    }
    public void selectShelf3(MouseEvent mouseEvent) {
        selectedShelf.add(3);
        for(Node res:hbox.getChildren())
            res.setMouseTransparent(false);
        shelfMenu.setVisible(false);

    }


    public void retryRes(MouseEvent mouseEvent) {
        selectedRes.clear();
        selectedShelf.clear();
        countRes=0;
    }

    public void resNext(){
        if(selectedRes.size()==initialRes && selectedShelf.size()==initialRes) {
            for(int i=0;i<initialRes;i++)
                gui.getMainClient().send(new InitialResourceMessage(selectedRes.get(i),selectedShelf.get(i)));
            gui.changeStage("waiting.fxml");
        }
        else
            errorLabel.setText("Please choose 2 leads before pressing NEXT.If you want to change your choice, click RETRY");


    }
}
/**
 * confirmation.setText("Choose a valid nickname");
 */