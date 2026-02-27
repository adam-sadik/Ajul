package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PkMoveTest {

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

}
