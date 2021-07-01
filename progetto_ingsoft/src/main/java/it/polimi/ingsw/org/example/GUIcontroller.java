package it.polimi.ingsw.org.example;

import javafx.event.ActionEvent;

public interface GUIcontroller {
    void setGui(GUI gui);

    default void quit(ActionEvent actionEvent){


    }
}
