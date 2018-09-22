import agents.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class PulseControllerTest {

    private Position roverPosition = new Position(0, 0);
    private int range = 4;

    @Test
    public void positionGenerationTest() {
        Set<Position> tilePositionsInPulseRange = new HashSet<>();
        for (int i = 0; i <= range; i++) {
            for (int j = 0; j <= range; j++) {
                tilePositionsInPulseRange.add(new Position(roverPosition.x + i, roverPosition.y + j));
                tilePositionsInPulseRange.add(new Position(roverPosition.x + i, roverPosition.y - j));
                tilePositionsInPulseRange.add(new Position(roverPosition.x - i, roverPosition.y + j));
                tilePositionsInPulseRange.add(new Position(roverPosition.x - i, roverPosition.y - j));
            }
        }
        tilePositionsInPulseRange.forEach(System.out::println);
        Assertions.assertEquals(tilePositionsInPulseRange.size(), 81);
    }

    @Test
    public void streamTest() {
        Optional<Integer> minInt = List.of(4, 4, 2, 2).stream()
                .min(Comparator.comparingInt(value -> value));

        Assertions.assertEquals(Optional.of(2), minInt);
    }

}
