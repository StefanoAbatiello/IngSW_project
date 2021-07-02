package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.answerMessages.*;

public interface View {

    /**
     * this is the client handel of the nickname message
     * @param message is the message
     */
    void nicknameHandler(NickNameAction message);

    /**
     * this is the client handler of the request of players number message
     * @param message is the message
     */
    void numOfPlayerHandler(RequestNumOfPlayers message);

    /**
     * this the client handler of the waiting room message
     * @param message is the message
     */
    void waitingRoomHandler(WaitingRoomAction message);

    /**
     * this is the client handler of the lobby message
     * @param message is the message
     */
    void lobbyMessageHandler(LobbyMessage message);

    /**
     * this is the client handler of the initial resource message
     * @param message is the message
     */
    void initialResourceHandler(GetInitialResourcesAction message);

    /**
     * this is the client handler of the leader card distribution message
     * @param message is the message
     */
    void leadCardHandler(LeaderCardDistribution message);

    /**
     * this is the client handler of the resource in supply request
     * @param message is the message
     */
    void supplyHandler(ResourceInSupplyRequest message);

    /**
     * this is the client handler of the the market change message
     * @param message is the message
     */
    void marketHandler(MarketChangeMessage message);

    /**
     * this is the client handler of the warehouse change message
     * @param message is the message
     */
    void warehouseHandler(WareHouseChangeMessage message);

    /**
     * this is the client handler of the personal cards id change message
     * @param message is the message
     */
    void personalCardHandler(CardIDChangeMessage message);

    /**
     * this is the client handler of the development matrix change message
     * @param message is the message
     */
    void devMatrixHandler(DevMatrixChangeMessage message);

    /**
     * this is the client handler of the strongbox change message
     * @param message is the message
     */
    void strongboxHandler(StrongboxChangeMessage message);

    /**
     * this is the client handler of the request of change the choosable resources
     * @param message is the message
     */
    void choosableResourceHandler(ChangeChoosableResourceRequest message);

    /**
     * this is the client handler of the starting game message
     * @param simplifiedModel is the client simplified model
     * @param message is the message
     */
    void gameSetupHandler(SimplifiedModel simplifiedModel, StartingGameMessage message);


    /**
     * @param simplifiedModel is the client simplified model
     */
    void setViewCLI(SimplifiedModel simplifiedModel);

    /**
     * this is the client handler of the change of the faith position
     * @param message is the message
     */
    void faithPositionHandler(FaithPositionChangeMessage message);

    /**
     * this is the client handler of the activation of a pope meeting
     * @param message is the message
     */
    void activePopeMeetingHandler(ActivePopeMeetingMessage message);

    /**
     * this is the client handler of the notify of the activation of a special shelf ability
     * @param message is the message
     */
    void shelfAbilityActiveHandler(ShelfAbilityActiveMessage message);

    /**
     * this is the client handler of the change of the black cross position
     * @param message is the message
     */
    void lorenzoActionHandler(LorenzoActionMessage message);

    /**
     * this is the client handler of the change of the winner message
     * @param message is the message
     */
    void winnerHandler(WinnerMessage message);
}
