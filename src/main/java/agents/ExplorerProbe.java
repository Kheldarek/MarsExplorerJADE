package agents;

import agents.sensors.Sensor;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import logic.Tile;

import java.util.*;

public class ExplorerProbe extends Agent {

    public boolean stepByStep = false;
    public Mode workMode;
    public Position position;
    Sensor sensor;
    public boolean hasASample = false;
    public ExplorerRover rover;
    public int crumbCount = 60;
    public boolean tickCompleted = false;

    public ExplorerProbe(Sensor sensor, Position position, ExplorerRover explorerRover) {
        this.sensor = sensor;
        workMode = Mode.EXPLORE;
        this.position = position;
        this.rover = explorerRover;
    }

    protected void setup() {
        System.out.println("Hi! ExplorerProbeAgent " + getAID().getName() + " is ready!");
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


    public Position getPosition() {
        return position;
    }


    public void work() {
        pickUpSample();
        move();
        pickUpSample();
        checkMode();
    }

    void checkMode() {
        if (hasASample) {
            workMode = Mode.RETURN;
        }
    }

    void move() {
        Tile currentTile = sensor.getTileUnderProbe(position);
        Tile nextTile = findNextTile();
        if (nextTile == null) {
            List<Tile> tiles = sensor.getAdjacentTiles(position);
            Collections.shuffle(tiles);
            nextTile = tiles.stream()
                    .filter(tile -> !tile.isObstacle())
                    .findAny()
                    .orElse(currentTile);
        }
        if (nextTile != null) {
            currentTile.setHasProbe(false);
            nextTile.setHasProbe(true);
            position = nextTile.getPosition();
            handleCrumbs();
        }

    }


    void pickUpSample() {
        if (!hasASample) {
            Tile tileUnderProbe = sensor.getTileUnderProbe(position);
            if (tileUnderProbe.excavate()) {
                hasASample = true;
                workMode = Mode.RETURN;
            }
        }
    }

    Tile findNextTile() {
        List<Tile> tiles = sensor.getAdjacentTiles(position);
        Tile currentTile = sensor.getTileUnderProbe(position);
        int currentGradient = currentTile.getGradient();
        System.out.println("Hi! ExplorerProbeAgent " + getAID().getName() + " currentTile = " + currentTile);
        System.out.println("Hi! ExplorerProbeAgent " + getAID().getName() + " adjacentTiles = " + Arrays.toString(tiles.toArray()));

        if (!isLost(tiles)) {
            System.out.println("Hi! ExplorerProbeAgent " + getAID().getName() + " is tracking route!");
            if (workMode == Mode.EXPLORE) {
                if (tiles.stream().anyMatch(Tile::hasSamples)) {
                    Collections.shuffle(tiles);
                    return tiles.stream()
                            .filter(Tile::hasSamples)
                            .filter(tile -> !tile.isObstacle())
                            .filter(tile -> tile.getGradient() != 999)
                            .findAny()
                            .orElse(null);
                }
                if (tiles.stream().anyMatch(Tile::hasCrumbs)) {
                    Optional<Tile> maxCrumbs = tiles.stream()
                            .filter(tile -> !tile.isObstacle())
                            .max(Comparator.comparingInt(Tile::getCrumbCount));
                    if (maxCrumbs.isPresent()) {
                        Collections.shuffle(tiles);
                        return tiles.stream()
                                .filter(tile -> tile.getCrumbCount() == maxCrumbs.get().getCrumbCount())
                                .filter(tile -> !tile.isObstacle())
                                .max(Comparator.comparingInt(Tile::getGradient))
                                .orElse(null);
                    }
                } else {
                    Optional<Tile> minGradientTile = tiles.stream()
                            .filter(tile -> !tile.isObstacle())
                            .filter(tile -> tile.getGradient() < currentGradient)
                            .min(Comparator.comparingInt(Tile::getGradient));
                    if (minGradientTile.isPresent()) {
                        Collections.shuffle(tiles);
                        return tiles.stream()
                                .filter(tile -> !tile.isObstacle())
                                .filter(tile -> tile.getGradient() == minGradientTile.get().getGradient())
                                .findAny()
                                .orElse(null);
                    } else {
                        workMode = Mode.RETURN;
                    }
                }
            }
            if (workMode == Mode.RETURN) {
                if (currentGradient == 5) {
                    workMode = Mode.EXPLORE;
                    if (hasASample) {
                        hasASample = false;
                        rover.collectedSamples.incrementAndGet();
                    }
                    return findNextTile();
                } else {
                    Optional<Tile> maxGradientTile = tiles.stream()
                            .filter(tile -> !tile.isObstacle())
                            .filter(tile -> tile.getGradient() != 999)
                            .filter(tile -> tile.getGradient() > currentGradient)
                            .max(Comparator.comparingInt(Tile::getGradient));
                    if (maxGradientTile.isPresent()) {
                        Collections.shuffle(tiles);
                        return tiles.stream()
                                .filter(tile -> tile.getGradient() == maxGradientTile.get().getGradient())
                                .filter(tile -> !tile.isObstacle())
                                .filter(tile -> tile.getGradient() != 999)
                                .max(Comparator.comparingInt(Tile::getCrumbCount))
                                .orElse(null);
                    }
                }
            }
        } else {
            System.out.println("Hi! ExplorerProbeAgent " + getAID().getName() + " is lost!");
            Collections.shuffle(tiles);
            Optional<Tile> randomTile = tiles.stream()
                    .filter(tile -> !tile.isObstacle())
                    .findAny();
            if (randomTile.isPresent()) {
                return randomTile.get();
            }
        }
        Collections.shuffle(tiles);
        Optional<Tile> randomTile = tiles.stream()
                .filter(tile -> !tile.isObstacle())
                .findAny();
        return randomTile.orElse(null);
    }

    void handleCrumbs() {
        Tile currentTile = sensor.getTileUnderProbe(position);
        List<Tile> adjacent = sensor.getAdjacentTiles(position);
        if (hasASample && !adjacent.stream().allMatch(tile -> tile.getGradient() == 999) && crumbCount >= 2 && !currentTile.hasSamples() && currentTile.getCrumbCount() < 11) {
            currentTile.dropCrumbs();
            crumbCount -= 2;
        } else if (currentTile.hasCrumbs()) {
            currentTile.pickUpCrumb();
            crumbCount++;
        }
    }

    private boolean isLost(List<Tile> tiles) {
        return tiles.stream().allMatch(tile -> tile.getGradient() == 999 && !tile.hasCrumbs());
    }


}
