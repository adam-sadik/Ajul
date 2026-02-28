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

    private static final int SOURCE_OFFSET = 0;
    private static final int COLOR_OFFSET = 4;
    private static final int DESTINATION_OFFSET = 7;
    private static final int UPPER_SIX_BITS_MASK = 0b111111_0000000000;

    @Test
    void packAndExtractRoundTripOnFullDomain() {
        for (var source : TileSource.ALL) {
            for (var color : TileKind.Colored.ALL) {
                for (var destination : TileDestination.ALL) {
                    short packed = PkMove.pack(source, color, destination);
                    assertEquals(source, PkMove.source(packed));
                    assertEquals(color, PkMove.color(packed));
                    assertEquals(destination, PkMove.destination(packed));
                }
            }
        }
    }

    @Test
    void packUsesExpectedBitLayoutForConcreteExamples() {
        short first = PkMove.pack(TileSource.FACTORY_3, TileKind.Colored.D, TileDestination.PATTERN_5);
        int expectedFirst = (TileSource.FACTORY_3.index() << SOURCE_OFFSET)
                | (TileKind.Colored.D.index() << COLOR_OFFSET)
                | (TileDestination.PATTERN_5.index() << DESTINATION_OFFSET);
        assertEquals(expectedFirst, Short.toUnsignedInt(first));

        short second = PkMove.pack(TileSource.CENTER_AREA, TileKind.Colored.E, TileDestination.FLOOR);
        int expectedSecond = (TileSource.CENTER_AREA.index() << SOURCE_OFFSET)
                | (TileKind.Colored.E.index() << COLOR_OFFSET)
                | (TileDestination.FLOOR.index() << DESTINATION_OFFSET);
        assertEquals(expectedSecond, Short.toUnsignedInt(second));
    }

    @Test
    void packedValuesUseOnlyLowerTenBits() {
        for (var source : TileSource.ALL) {
            for (var color : TileKind.Colored.ALL) {
                for (var destination : TileDestination.ALL) {
                    int packed = Short.toUnsignedInt(PkMove.pack(source, color, destination));
                    assertEquals(0, packed & UPPER_SIX_BITS_MASK);
                }
            }
        }
    }
}
