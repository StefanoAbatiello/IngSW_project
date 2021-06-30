package it.polimi.ingsw.org.example;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.ViewCLI;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;
import javafx.application.Application;
import javafx.application.Platform;
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
    private final String DEVMATRIX="devMatrix.fxml";
    private final String ZOOMCARD="zoomedCard.fxml";
    private final String SUPPLY="supply.fxml";
    private final String WINNER="winner.fxml";



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
        List<String> fxmList = new ArrayList<>(Arrays.asList(LOGIN, NICKNAME,BOARD,WAITING,NUMOFPLAYER,LEADCARDSCHOICE,INITIALRESOURCES,MARKETBOARD,DEVMATRIX,SUPPLY,ZOOMCARD,WINNER));
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
        stage.centerOnScreen();
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
        guicontroller.setEmptyTextFieldName();
        System.out.println("end");});
    }

    @Override
    public void numOfPlayerHandler(RequestNumOfPlayers requestNumOfPlayers) {
        Platform.runLater(()->{System.out.println("begin of numplayers");
            changeStage(NUMOFPLAYER);
            System.out.println("change stage");
            SetupController guicontroller = (SetupController) nameMapController.get(NUMOFPLAYER);
            System.out.println("end controller setup");
            System.out.println("end");});
    }

    public void waitingRoomHandler(WaitingRoomAction waitingRoomAction) {
        Platform.runLater(()->{System.out.println("waiting room");
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
            BoardController boardController = (BoardController) nameMapController.get(BOARD);
            SetupController leadscontroller = (SetupController) nameMapController.get(LEADCARDSCHOICE);
            DevMatrixController devscontroller = (DevMatrixController) nameMapController.get(DEVMATRIX);
            boardController.setLeads(leadscontroller.getSelectedCards());
            devscontroller.setDevMatrix(viewCLI);
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

    public void faithPositionHandler(FaithPositionChangeMessage message){
        Platform.runLater(()->{
            System.out.println(message.getFaithPosition());
            System.out.println("faithMessage");
            BoardController boardController=(BoardController) nameMapController.get(BOARD);
            boardController.uploadPosition(message.getFaithPosition(), boardController.croce);
        });
    }

    @Override
    public void activePopeMeetingHandler(ActivePopeMeetingMessage message) {
        Platform.runLater(()->{
            BoardController boardController=(BoardController) nameMapController.get(BOARD);
            boardController.activePope(message.getMeetingNumber());
        });
    }

    @Override
    public void shelfAbilityActiveHandler(ShelfAbilityActiveMessage message) {
        Platform.runLater(()->{
            BoardController boardController=(BoardController) nameMapController.get(BOARD);
            boardController.leadShelfActivation(message.getCardId());
        });
    }

    @Override
    public void lorenzoActionHandler(LorenzoActionMessage lorenzoActionMessage) {
        Platform.runLater(()->{
            System.out.println("lorenzoMessage");
            BoardController boardController=(BoardController) nameMapController.get(BOARD);
            boardController.lorenzoUpdate(lorenzoActionMessage.getVal());
        });
    }


    @Override
    public void supplyHandler(ResourceInSupplyRequest resource) {
        Platform.runLater(()->{
            System.out.println("Inizio Supply");
            changeStage(SUPPLY);
            SupplyController supplyController=(SupplyController) nameMapController.get(SUPPLY);
            supplyController.setSupply(resource.getResources());
            System.out.println("Fine Supply");
        });

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
        Platform.runLater(()-> {
            System.out.println("Your warehouse has changed");
            viewCLI.setWarehouse((wareHouseChangeMessage).getWarehouse());
            BoardController boardController = (BoardController) getControllerFromName(BOARD);
            boardController.setWareHouse(viewCLI);
        });
    }

    @Override
    public void personalCardHandler(CardIDChangeMessage cardIDChangeMessage) {
            System.out.println("Your cards have changed");
            cardIDChangeMessage.getCardID().keySet().stream().filter(integer -> integer > 48 && integer < 65).forEach(cardID -> {
                if (viewCLI.getLeadCardsId().get(cardID) != cardIDChangeMessage.getCardID().get(cardID)) {
                    viewCLI.getLeadCardsId().remove(cardID);
                }
                if (!viewCLI.getLeadCardsId().containsKey(cardID)) {
                    viewCLI.addLeadCardsId(cardID, cardIDChangeMessage.getCardID().get(cardID));
                    System.out.println("mi sto salvando la carta " + cardID);
                }
                for (int id : viewCLI.getLeadCardsId().keySet())
                    if (!cardIDChangeMessage.getCardID().containsKey(id))
                        viewCLI.getLeadCardsId().remove(id);
            });
            cardIDChangeMessage.getCardID().keySet().stream().filter(integer -> integer >= 0 && integer <= 48).forEach(cardID -> {
                if (viewCLI.getDevCardsId().get(cardID) != cardIDChangeMessage.getCardID().get(cardID)) {
                    viewCLI.getDevCardsId().remove(cardID);
                }
                if (!viewCLI.getDevCardsId().containsKey(cardID)) {
                    viewCLI.addDevCardId(cardID, cardIDChangeMessage.getCardID().get(cardID));
                    System.out.println("mi sto salvando la carta " + cardID);

                }
            });
        Platform.runLater(()-> {viewCLI.setDevPositions(cardIDChangeMessage.getCardPosition());
            BoardController boardController = (BoardController) getControllerFromName(BOARD);
            boardController.setCards(viewCLI);
        });
    }

    @Override
    public void devMatrixHandler(DevMatrixChangeMessage devMatrixChangeMessage) {
    viewCLI.setDevMatrix(devMatrixChangeMessage.getDevMatrix());
    Platform.runLater(()->{
        DevMatrixController matrixController = (DevMatrixController) getControllerFromName(DEVMATRIX);
        matrixController.setDevMatrix(viewCLI);
    });
    }

    @Override
    public void strongboxHandler(StrongboxChangeMessage strongboxChangeMessage) {
        Platform.runLater(()-> {
            System.out.println("Your strongbox has changed");
            viewCLI.setStrongbox(strongboxChangeMessage.getStrongbox());
            BoardController boardController = (BoardController) getControllerFromName(BOARD);
            boardController.setStrongbox(viewCLI);
        });
    }

    @Override
    public void choosableResourceHandler(ChangeChoosableResourceRequest input) {

    }



    //TODO special shelf

    public void setViewCLI(ViewCLI viewCLI) {
        this.viewCLI = viewCLI;
    }

    public ViewCLI getViewCLI() {
        return viewCLI;
    }
}
