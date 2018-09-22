package agents.sensors;

import agents.Position;
import logic.MarsMap;
import logic.Tile;

import java.util.List;
import java.util.stream.Collectors;

public class Sensor {

    MarsMap marsMap;

    public Sensor(MarsMap map) {
        marsMap = map;
    }

    public Tile getTileUnderProbe(Position roverPosition) {
        return marsMap.getTileAtPosition(roverPosition);
    }

    public List<Tile> getAdjacentTiles(Position roverPosition) {
        Position tileLeft = new Position(roverPosition.x - 1, roverPosition.y);
        Position tileRight = new Position(roverPosition.x + 1, roverPosition.y);
        Position tileUp = new Position(roverPosition.x, roverPosition.y + 1);
        Position tileDown = new Position(roverPosition.x, roverPosition.y - 1);
        Position tileUpRight = new Position(roverPosition.x + 1, roverPosition.y + 1);
        Position tileDownRight = new Position(roverPosition.x + 1, roverPosition.y - 1);
        Position tileUpLeft = new Position(roverPosition.x - 1, roverPosition.y + 1);
        Position tileDownLeft = new Position(roverPosition.x - 1, roverPosition.y - 1);

        return List.of(tileDown, tileLeft, tileRight, tileUp, tileUpLeft, tileDownLeft, tileDownRight, tileUpRight).stream()
                .filter(this::isValidPosition)
                .map(position -> marsMap.getTileAtPosition(position))
                .collect(Collectors.toList());
    }

    private boolean isValidPosition(Position position) {
        return position.x < 10 && position.x >= 0 && position.y >= 0 && position.y < 10;
    }
}
