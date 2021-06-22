package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;

public interface View {
    void nicknameHandler(NickNameAction nickNameAction);
    void numOfPlayerHandler(RequestNumOfPlayers requestNumOfPlayers);
    void waitingRoomHandler(WaitingRoomAction waitingRoomAction);
    void lobbyMessageHandler(LobbyMessage lobbyMessage);
    void initialResourceHandler(GetInitialResourcesAction initialResourcesAction);
    void leadCardHandler(LeaderCardDistribution leaderCardDistribution);
    void supplyHandler(ResourceInSupplyRequest resource);
    void marketHandler(MarketChangeMessage marketChangeMessage);
    void warehouseHandler(WareHouseChangeMessage wareHouseChangeMessage);
    void personalCardHandler(CardIDChangeMessage cardIDChangeMessage);
    void devMatrixHandler(DevMatrixChangeMessage devMatrixChangeMessage);
    void strongboxHandler(StrongboxChangeMessage strongboxChangeMessage);
    void choosableResourceHandler(ChangeChoosableResourceRequest input);
    void gameSetupHandler(ViewCLI viewCLI, SerializedMessage input);
}
