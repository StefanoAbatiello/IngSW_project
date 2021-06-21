package it.polimi.ingsw.org.example;

public class GameController implements GUIcontroller{

    private GUI gui;

    @Override
    public void setGui(GUI gui) {
        this.gui=gui;
    }
}
