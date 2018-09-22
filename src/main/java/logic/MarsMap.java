package logic;

import agents.Position;

public class MarsMap {

    public static final int WORLD_SIZE = 10;

    private String[][] mapDefaultTemplate = {
            {"X", "O", "|", "|", "6", "5", "", "|", "", "8"},
            {"O", "O", "|", "|", "6", "5", "", "|", "", ""},
            {"", "", "", "|", "|", "2", "1", "", "|", ""},
            {"", "", "", "", "", "", "", "", "", "7"},
            {"7", "6", "|", "", "|", "|", "", "", "4", ""},
            {"8", "4", "|", "", "|", "|", "", "", "", ""},
            {"", "", "", "|", "", "|", "|", "", "", ""},
            {"", "", "", "", "", "", "", "1", "2", "3"},
            {"", "", "7", "", "", "", "", "4", "6", "8"},
            {"", "", "", "", "", "", "", "5", "9", "1"}
    };

    private Tile[][] map = new Tile[10][10];

    public MarsMap() {
        map = createMap(mapDefaultTemplate);
    }

    public MarsMap(String[][] template) {
        map = createMap(template);
    }

    public Tile getTileAtPosition(Position position) {
        return map[position.x][position.y];
    }

    public void flushGradient() {
        for (Tile[] row : map) {
            for (Tile tile : row) {
                tile.setGradient(999);
            }
        }
    }

    private Tile[][] createMap(String[][] template) {
        Tile[][] toFill = new Tile[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String tileTypeMarker = template[i][j];
                toFill[i][j] = new Tile();
                fillTile(tileTypeMarker, toFill[i][j], new Position(i, j));
            }
        }
        return toFill;
    }

    private void fillTile(String marker, Tile tile, Position position) {
        tile.setPosition(position);
        tile.setHasCrumbs(false);
        tile.setCrumbCount(0);
        tile.setGradient(999);

        switch (marker) {
            case "X":
                tile.setHasRover(true);
                tile.setHasSamples(false);
                tile.setSampleCount(0);
                break;
            case "O":
                tile.setHasProbe(true);
                tile.setHasSamples(false);
                tile.setSampleCount(0);
                break;
            case "|":
                tile.setObstacle(true);
                tile.setHasSamples(false);
                tile.setSampleCount(0);
                break;
            case "":
                tile.setHasSamples(false);
                tile.setSampleCount(0);
                break;
            default:
                tile.setHasSamples(true);
                tile.setSampleCount(Integer.parseInt(marker));
                break;
        }
    }

}
