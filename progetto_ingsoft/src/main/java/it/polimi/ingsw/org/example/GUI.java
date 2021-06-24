package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.ViewCLI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import it.polimi.ingsw.server.ClientHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    private final String LEADCARDSCHOICE="leadcardschoice.fxml";
    private final String INITIALRESOURCES="choiceresources.fxml";
    private final String MARKETBOARD="provabiglia.fxml";

    private final HashMap<String,Scene> nameMapScene= new HashMap<>();
    private final HashMap<String,GUIcontroller> nameMapController= new HashMap<>();
    private Scene currentscene;
    private Stage stage;
    private MainClient client=null;
    private ViewCLI viewCLI;

    public static void main() {

        launch();
    }


    @Override
    public void start(Stage stage) {
        this.stage=stage;
        List<String> fxmList = new ArrayList<>(Arrays.asList(LOGIN, NICKNAME,BOARD,WAITING,NUMOFPLAYER,LEADCARDSCHOICE,INITIALRESOURCES,MARKETBOARD));
        try {

            for (String path : fxmList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.example/fxml/" + path));
                nameMapScene.put(path, new Scene(loader.load()));
                GUIcontroller controller = loader.getController();
                controller.setGui(this);
                nameMapController.put(path, controller);
            }
            changeStage(LOGIN);
            stage.setTitle("Master of Renaissance");
            stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void closeWindowEvent(WindowEvent t) {
        getMainClient().disconnect();
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
        guicontroller.setErrorLabel(nickNameAction.getMessage());
        System.out.println("end");});
    }

    @Override
    public void numOfPlayerHandler(RequestNumOfPlayers requestNumOfPlayers) {
        Platform.runLater(()->{System.out.println("begin of numplayers");
            changeStage(NUMOFPLAYER);
            System.out.println("change stage");
            SetupController guicontroller = (SetupController) nameMapController.get(NUMOFPLAYER);
            System.out.println("end controller setup");
            guicontroller.setErrorLabel(requestNumOfPlayers.getMessage());
            System.out.println("end");});
    }

    //@Override
    public void waitingRoomHandler(WaitingRoomAction waitingRoomAction) {
        Platform.runLater(()->{System.out.println("waitingroom");
            changeStage(WAITING);
            System.out.println("change stage");
            SetupController guicontroller = (SetupController) nameMapController.get(WAITING);
            System.out.println("end controller setup");
            System.out.println("end");});
    }

    @Override
    public void lobbyMessageHandler(LobbyMessage lobbyMessage) {
        Platform.runLater(()->{
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("INFO GAME");
            alert.setHeight(10);
            alert.setContentText(lobbyMessage.getMessage());
            alert.showAndWait();
        });


    }

    @Override
    public void gameSetupHandler(ViewCLI viewCLI, SerializedMessage input){
        Platform.runLater(()->{System.out.println("gameSetup");
            changeStage(BOARD);
            System.out.println("change stage");
            BoardController guicontroller = (BoardController) nameMapController.get(BOARD);
            SetupController leadscontroller = (SetupController) nameMapController.get(LEADCARDSCHOICE);
            guicontroller.setLeads(leadscontroller.getSelectedCards());
            System.out.println("end controller game");
            System.out.println("end");});
    }

    @Override
    public void leadCardHandler(LeaderCardDistribution leaderCardDistribution) {
        Platform.runLater(()->{System.out.println("leadcardschoice");
            changeStage(LEADCARDSCHOICE);
            System.out.println("change stage");
            SetupController guicontroller = (SetupController) nameMapController.get(LEADCARDSCHOICE);
            System.out.println("end controller game");
            System.out.println("end");
            guicontroller.setLeads(leaderCardDistribution.getLeadCardsId());
        });
    }

    @Override
    public void initialResourceHandler(GetInitialResourcesAction initialResourcesAction) {
        Platform.runLater(()->{System.out.println("leadcardschoice");
            changeStage(INITIALRESOURCES);
            System.out.println("change stage");
            SetupController guicontroller = (SetupController) nameMapController.get(INITIALRESOURCES);
            System.out.println("end controller game");
            System.out.println("end");
            guicontroller.setLabelResources(initialResourcesAction.getMessage());
            guicontroller.setInitialRes(initialResourcesAction.getNumRes());

        });
    }



    @Override
    public void supplyHandler(ResourceInSupplyRequest resource) {


    }

    @Override
    public void marketHandler(MarketChangeMessage marketChangeMessage) {
        Platform.runLater(()->{System.out.println("market");

            System.out.println("change stage");
            MarketController guicontroller = (MarketController) nameMapController.get(MARKETBOARD);
            System.out.println("end controller game");
            System.out.println("end");
            viewCLI.setMarket(marketChangeMessage.getMarket());
            guicontroller.changeMarket(marketChangeMessage.getMarket());
        });

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

    public void setViewCLI(ViewCLI viewCLI) {
        this.viewCLI = viewCLI;
    }

}
