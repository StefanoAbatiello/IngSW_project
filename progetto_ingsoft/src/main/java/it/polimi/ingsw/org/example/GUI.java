package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.View;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import it.polimi.ingsw.server.ClientHandler;
import javafx.application.Application;
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

    private final HashMap<String,Scene> nameMapScene= new HashMap<>();
    private final HashMap<String,GUIcontroller> nameMapController= new HashMap<>();
    private Scene currentscene;
    private Stage stage;
    private ClientHandler clientHandler=null;

    public static void main() {
        launch();
    }

    @Override
    public void start(Stage stage) {
        this.stage=stage;

        List<String> fxmList = new ArrayList<>(Arrays.asList(LOGIN, NICKNAME,BOARD));
        try {
            for (String path : fxmList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.example/" +path));
                nameMapScene.put(path, new Scene(loader.load()));
                GUIcontroller controller = loader.getController();
                controller.setGui(this);
                nameMapController.put(path, controller);
            }
            currentscene=nameMapScene.get(LOGIN);
            stage.setTitle("Master of Renaissance");
            stage.setScene(currentscene);
            stage.show();
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

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    @Override
    public void nicknameHandler(NickNameAction nickNameAction) {

    }

    @Override
    public void numOfPlayerHandler(RequestNumOfPlayers requestNumOfPlayers) {

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
