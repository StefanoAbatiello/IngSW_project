package it.polimi.ingsw.org.example;

import javafx.fxml.FXML;

import java.io.IOException;

public class SecondaryController implements GUIcontroller{

    private GUI gui;


    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }
}