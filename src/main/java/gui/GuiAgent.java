package gui;

import jade.core.Agent;
import javafx.application.Application;

public class GuiAgent extends Agent {
    @Override
    protected void setup() {
        Application.launch(SurvivingMarsApp.class);
    }
}
