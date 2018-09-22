package agents;

import agents.sensors.PulseController;
import agents.sensors.Sensor;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import logic.Tile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExplorerRover extends Agent {

    public Position position;
    PulseController pulseController;
    Sensor sensor;
    public AtomicInteger collectedSamples;
    private int previousCounter;
    public int howLongNoChange;
    public boolean tickCompleted = false;
    public boolean stepByStep = false;

    public ExplorerRover(PulseController pulseController, Position position, Sensor sensor) {
        this.pulseController = pulseController;
        this.position = position;
        this.collectedSamples = new AtomicInteger(0);
        this.previousCounter = 0;
        this.howLongNoChange = 0;
        this.sensor = sensor;
    }

    protected void setup() {
        System.out.println("Hi! ExplorerRoverAgent " + getAID().getName() + " is ready!");
        addBehaviour(new TickerBehaviour(this, 100) {
            @Override
            protected void onTick() {
                if (stepByStep) {
                    if (!tickCompleted) {
                        work();
                        tickCompleted = true;
                    }
                } else {
                    work();
                }
            }
        });
    }

    public void work() {
        if (previousCounter == collectedSamples.get()) {
            howLongNoChange++;
            if (howLongNoChange > 12) {
                howLongNoChange = 0;
                move();
            }
        } else {
            howLongNoChange = 0;
        }
        previousCounter = collectedSamples.get();
        sendSoundWave(5);
        System.out.println("ROVER COLLECTED SAMPLES: " + collectedSamples.get());
        System.out.println("Did not change in: " + howLongNoChange);
    }

    private void sendSoundWave(int range) {
        pulseController.sendPulse(range);
    }

    private void move() {
        List<Tile> adjacentTiles = sensor.getAdjacentTiles(position);
        Collections.shuffle(adjacentTiles);
        Tile nextTile = adjacentTiles.stream()
                .filter(tile -> !tile.isObstacle())
                .max(Comparator.comparingInt(Tile::getCrumbCount))
                .orElse(null);
        if (nextTile != null) {
            sensor.getTileUnderProbe(position).setHasRover(false);
            position = nextTile.getPosition();
            nextTile.setHasRover(true);
            pulseController.roverPosition = position;
        }
    }
}
