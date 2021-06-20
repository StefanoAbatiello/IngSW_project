package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import it.polimi.ingsw.server.ClientHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GUI extends Application implements View {

    private final String LOGIN="Login.fxml";
    private final String NICKNAME="Nickname.fxml";
    private final String BOARD="board.fxml";
    private final String WAITING="waiting.fxml";
    private final String NUMOFPLAYER="insert_num_of_player.fxml";

    private final HashMap<String,Scene> nameMapScene= new HashMap<>();
    private final HashMap<String,GUIcontroller> nameMapController= new HashMap<>();
    private Scene currentscene;
    private Stage stage;
    private MainClient client=null;

    public static void main() {
        launch();
    }


    @Override
    public void start(Stage stage) {
        this.stage=stage;

        List<String> fxmList = new ArrayList<>(Arrays.asList(LOGIN, NICKNAME,BOARD,WAITING,NUMOFPLAYER));
        try {

            for (String path : fxmList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.example/" + path));
                nameMapScene.put(path, new Scene(loader.load()));
                GUIcontroller controller = loader.getController();
                controller.setGui(this);
                nameMapController.put(path, controller);
            }
            changeStage(LOGIN);
            stage.setTitle("Master of Renaissance");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void changeStage(String s) {

        currentscene=nameMapScene.get(s);
        stage.setScene(currentscene);
        stage.show();

    }

    public GUIcontroller getControllerFromName(String s) {
        return nameMapController.get(s);
    }

    public void setMainClient(MainClient client) {
        this.client = client;
    }

    public MainClient getMainClient() {
        return client;
    }

    @Override
    public void nicknameHandler(NickNameAction nickNameAction) {
        Platform.runLater(()->{System.out.println("begin of nickname");
        changeStage(NICKNAME);
        System.out.println("change stage");
        SetupController guicontroller = (SetupController) nameMapController.get(NICKNAME);
        System.out.println("end controller setup");
        guicontroller.setConfirmation(nickNameAction.getMessage());
        System.out.println("end");});
    }

    @Override
    public void numOfPlayerHandler(RequestNumOfPlayers requestNumOfPlayers) {
        Platform.runLater(()->{System.out.println("begin of numplayers");
            changeStage(NUMOFPLAYER);
            System.out.println("change stage");
            SetupController guicontroller = (SetupController) nameMapController.get(NUMOFPLAYER);
            System.out.println("end controller setup");
            guicontroller.setConfirmation(requestNumOfPlayers.getMessage());
            System.out.println("end");});
    }

    //@Override
    public void waitingRoomHandler(WaitingRoomAction waitingRoomAction) {
        Platform.runLater(()->{System.out.println("waitingroom");
            changeStage(WAITING);
            System.out.println("change stage");
            SetupController guicontroller = (SetupController) nameMapController.get(WAITING);
            System.out.println("end controller setup");
            guicontroller.setConfirmation(waitingRoomAction.getMessage());
            System.out.println("end");});
    }

    @Override
    public void lobbyMessageHandler(LobbyMessage lobbyMessage) {


    }

    @Override
    public void initialResourceHandler(GetInitialResourcesAction initialResourcesAction) {

    }

    @Override
    public void leadCardHandler(LeaderCardDistribution leaderCardDistribution) {

    }

    @Override
    public void supplyHandler(ResourceInSupplyRequest resource) {

    }

    @Override
    public void marketHandler(MarketChangeMessage marketChangeMessage) {

    }

    @Override
    public void warehouseHandler(WareHouseChangeMessage wareHouseChangeMessage) {

    }

    @Override
    public void personalCardHandler(CardIDChangeMessage cardIDChangeMessage) {

    }

    @Override
    public void devMatrixHandler(DevMatrixChangeMessage devMatrixChangeMessage) {

    }

    @Override
    public void strongboxHandler(StrongboxChangeMessage strongboxChangeMessage) {

    }

    @Override
    public void choosableResourceHandler(ChangeChoosableResourceRequest input) {

    }


}
