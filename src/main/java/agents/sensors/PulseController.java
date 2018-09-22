package agents.sensors;

import agents.Position;
import logic.MarsMap;
import logic.Tile;

import java.util.HashSet;
import java.util.Set;

public class PulseController {

    private MarsMap map;
    public Position roverPosition;

    public PulseController(MarsMap map) {
        this.map = map;
        roverPosition = new Position(0, 0);
    }

    public void sendPulse(int range) {
        Set<Position> tilePositionsInPulseRange = new HashSet<>();
        for (int i = 0; i <= range; i++) {
            for (int j = 0; j <= range; j++) {
                tilePositionsInPulseRange.add(new Position(roverPosition.x + i, roverPosition.y + j));
                tilePositionsInPulseRange.add(new Position(roverPosition.x + i, roverPosition.y - j));
                tilePositionsInPulseRange.add(new Position(roverPosition.x - i, roverPosition.y + j));
                tilePositionsInPulseRange.add(new Position(roverPosition.x - i, roverPosition.y - j));
            }
        }
        map.flushGradient();
        tilePositionsInPulseRange.stream()
                .filter(this::isValidPosition)
                .map(position -> map.getTileAtPosition(position))
                .forEach(this::updateGradient);
    }

    private boolean isValidPosition(Position position) {
        return position.x < 10 && position.x >= 0 && position.y >= 0 && position.y < 10;
    }

    private void updateGradient(Tile tile) {
        Position tilePosition = tile.getPosition();
        int diffX = Math.abs(tilePosition.x - roverPosition.x);
        int diffY = Math.abs(tilePosition.y - roverPosition.y);
        if (!tile.isObstacle()) {
            tile.setGradient(5 - (diffX > diffY ? diffX : diffY));
        } else {
            tile.setGradient(999);
        }
    }
}
