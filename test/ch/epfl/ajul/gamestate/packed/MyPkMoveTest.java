package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyPkMoveTest {

    @Test
    void allTheExtractionsAreCorrect(){

        for(TileSource source : TileSource.ALL){
            for(TileKind.Colored color : TileKind.Colored.ALL){
                for (TileDestination destination : TileDestination.ALL){
                    short test = PkMove.pack(source,color,destination);

                    assertEquals(source,PkMove.source(test));
                    assertEquals(color,PkMove.color(test));
                    assertEquals(destination,PkMove.destination(test));

                }
            }
        }
    }
    @Test
    void packAndUnpackWorksForPatternDestination() {
        TileSource source = TileSource.ALL.get(3);
        TileKind.Colored color = TileKind.Colored.C;
        TileDestination destination = TileDestination.PATTERN_4;

        short pkMove = PkMove.pack(source, color, destination);

        assertEquals(source, PkMove.source(pkMove));
        assertEquals(color, PkMove.color(pkMove));
        assertEquals(destination, PkMove.destination(pkMove));
    }

    @Test
    void packAndUnpackWorksForFloorDestination() {
        TileSource source = TileSource.ALL.get(9);
        TileKind.Colored color = TileKind.Colored.E;
        TileDestination destination = TileDestination.FLOOR;

        short pkMove = PkMove.pack(source, color, destination);

        assertEquals(source, PkMove.source(pkMove));
        assertEquals(color, PkMove.color(pkMove));
        assertEquals(destination, PkMove.destination(pkMove));
    }

    @Test
    void packAndUnpackWorksForZeroIndices() {
        TileSource source = TileSource.ALL.get(0);
        TileKind.Colored color = TileKind.Colored.A;
        TileDestination destination = TileDestination.PATTERN_1;

        short pkMove = PkMove.pack(source, color, destination);

        assertEquals(0, pkMove);
        assertEquals(source, PkMove.source(pkMove));
        assertEquals(color, PkMove.color(pkMove));
        assertEquals(destination, PkMove.destination(pkMove));
    }


}
