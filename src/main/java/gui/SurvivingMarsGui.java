package gui;

import agents.ExplorerProbe;
import agents.ExplorerRover;
import agents.Position;
import agents.sensors.PulseController;
import agents.sensors.Sensor;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.MarsMap;
import logic.Tile;

import java.net.URL;
import java.util.ResourceBundle;

public class SurvivingMarsGui implements Initializable {

    public static final int squareLength = 50;
    private final ContainerController jadeContainer;

    MarsMap marsMap = new MarsMap();
    AnimationTimer animationTimer;
    ExplorerRover rover;
    ExplorerProbe opportunity;
    ExplorerProbe curiosity;
    ExplorerProbe spirit;

    public SurvivingMarsGui() {
        final Profile myProfile = new ProfileImpl();
        jadeContainer = Runtime.instance().createAgentContainer(myProfile);
    }

    @FXML
    public CheckBox stepByStep;
    @FXML
    public Button nextStep;
    @FXML
    private Canvas canvas;
    @FXML
    public Label roverSamples;
    @FXML
    public Label roverPos;
    @FXML
    public Label roverNoSampleFor;

    @FXML
    public Label curiosityPosition;
    @FXML
    public Label curiosityMode;
    @FXML
    public Label curiosityHasSample;
    @FXML
    public Label curiosityCrumbs;

    @FXML
    public Label spiritPosition;
    @FXML
    public Label spiritMode;
    @FXML
    public Label spiritHasSample;
    @FXML
    public Label spiritCrumbs;

    @FXML
    public Label opportunityPosition;
    @FXML
    public Label opportunityMode;
    @FXML
    public Label opportunityHasSample;
    @FXML
    public Label opportunityCrumbs;
    @FXML
    public Label samplesLeft;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            rover = new ExplorerRover(new PulseController(marsMap), new Position(0, 0), new Sensor(marsMap));
            AgentController roverController = jadeContainer.acceptNewAgent("Rover", rover);
            roverController.start();
            opportunity = new ExplorerProbe(new Sensor(marsMap), new Position(0, 1), rover);
            curiosity = new ExplorerProbe(new Sensor(marsMap), new Position(1, 0), rover);
            spirit = new ExplorerProbe(new Sensor(marsMap), new Position(0, 0), rover);
            AgentController opportunityController = jadeContainer.acceptNewAgent("Opportunity", opportunity);
            AgentController curiosityController = jadeContainer.acceptNewAgent("Curiosity", curiosity);
            AgentController spiritController = jadeContainer.acceptNewAgent("Spirit", spirit);
            opportunityController.start();
            curiosityController.start();
            spiritController.start();
            Platform.runLater(this::drawAll);
            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    drawAll();
                    update();
                }
            };
            animationTimer.start();
        } catch (StaleProxyException e) {
            System.out.println("Agent Creation Error!");
        }
    }

    void drawAll() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Tile tile = marsMap.getTileAtPosition(new Position(i, j));
                GraphicsContext context = canvas.getGraphicsContext2D();
                if (tile.hasRover()) {
                    context.setFill(Color.RED);
                    context.drawImage(new Image("/gui/images/Rover.PNG"), j * squareLength, i * squareLength, squareLength, squareLength);
                } else if (tile.hasProbe()) {
                    context.setFill(Color.GREEN);
                    context.drawImage(new Image("/gui/images/Probe.PNG"), j * squareLength, i * squareLength, squareLength, squareLength);
                } else if (tile.hasSamples()) {
                    context.setFill(Color.BLUE);
                    context.drawImage(new Image("/gui/images/sample.PNG"), j * squareLength, i * squareLength, squareLength, squareLength);
                } else if (tile.isObstacle()) {
                    context.setFill(Color.GOLD);
                    context.drawImage(new Image("/gui/images/obstacle.PNG"), j * squareLength, i * squareLength, squareLength, squareLength);
                } else if (tile.hasCrumbs()) {
                    context.setFill(Color.BLACK);
                    context.drawImage(new Image("/gui/images/crumb.PNG"), j * squareLength, i * squareLength, squareLength, squareLength);
                } else {
                    context.setFill(Color.BROWN);
                    context.drawImage(new Image("/gui/images/sand.PNG"), j * squareLength, i * squareLength, squareLength, squareLength);
                }

            }
        }
    }

    void update() {
        roverSamples.setText("Samples: " + rover.collectedSamples.toString());
        roverPos.setText("Pos: " + rover.position.y + ", " + rover.position.x);
        roverNoSampleFor.setText("No change for: " + rover.howLongNoChange);


        curiosityHasSample.setText("HasSample: " + curiosity.hasASample);
        curiosityMode.setText("Mode: " + curiosity.workMode);
        curiosityPosition.setText("Pos: " + curiosity.position.y + ", " + curiosity.position.x);
        curiosityCrumbs.setText("Crumbs: " + curiosity.crumbCount);

        opportunityHasSample.setText("HasSample: " + opportunity.hasASample);
        opportunityMode.setText("Mode: " + opportunity.workMode);
        opportunityPosition.setText("Pos: " + opportunity.position.y + ", " + opportunity.position.x);
        opportunityCrumbs.setText("Crumbs: " + opportunity.crumbCount);

        spiritHasSample.setText("HasSample: " + spirit.hasASample);
        spiritMode.setText("Mode: " + spirit.workMode);
        spiritPosition.setText("Pos: " + spirit.position.y + ", " + spirit.position.x);
        spiritCrumbs.setText("Crumbs: " + spirit.crumbCount);

        samplesLeft.setText("Samples left: " + (115 - rover.collectedSamples.get()));
    }

    public void switchMode(ActionEvent actionEvent) {
        if (stepByStep.isSelected()) {
            rover.stepByStep = true;
            curiosity.stepByStep = true;
            opportunity.stepByStep = true;
            spirit.stepByStep = true;
        } else {
            rover.stepByStep = false;
            curiosity.stepByStep = false;
            opportunity.stepByStep = false;
            spirit.stepByStep = false;
        }
    }

    public void executeOneStep(ActionEvent actionEvent) {
        if (stepByStep.isSelected()) {
            rover.tickCompleted = false;
            curiosity.tickCompleted = false;
            opportunity.tickCompleted = false;
            spirit.tickCompleted = false;
        }
    }
}
