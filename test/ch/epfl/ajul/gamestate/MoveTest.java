package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public record MoveTest() {

    @Test
    void ConstructorThrowsOnNullArguments(){
        TileSource source = TileSource.ALL.get(1);
        TileDestination destination = TileDestination.ALL.get(1);
        TileKind.Colored kind = TileKind.Colored.ALL.get(1);

        assertThrows(NullPointerException.class, () -> new Move(null, kind, destination));
        assertThrows(NullPointerException.class, () -> new Move(source, null, destination));
        assertThrows(NullPointerException.class, () -> new Move(source, kind, null));
    }

    @Test
    void packedAndNotPackedAreEqual(){
        for (TileSource source : TileSource.ALL) {
            for (TileKind.Colored color : TileKind.Colored.ALL) {
                for (TileDestination dest : TileDestination.ALL) {

                    Move move = new Move(source, color, dest);
                    Move reconstructed =
                            Move.ofPacked(move.packed());

                    assertEquals(move, reconstructed);
                }
            }
        }
    }

    @Test
    void maxMovesIsPositive() {
        assertTrue(Move.MAX_MOVES > 0);
    }

}
