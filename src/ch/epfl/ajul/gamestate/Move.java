package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;
import ch.epfl.ajul.gamestate.packed.PkMove;

import java.util.Objects;

/// @author Rayane TAOUFIK BENCHEKROUN (412052)
public record Move(TileSource source, TileKind.Colored tileColor, TileDestination destination) {

    public static final int MAX_MOVES = TileSource.Factory.COUNT*TileDestination.COUNT*TileSource.Factory.TILES_PER_FACTORY;
    public Move {
        Objects.requireNonNull(source);
        Objects.requireNonNull(tileColor);
        Objects.requireNonNull(destination);
    }

    public static Move ofPacked(short pkMove){
        return new Move( PkMove.source(pkMove),PkMove.color(pkMove), PkMove.destination(pkMove));
    }

    public short packed(){
        return PkMove.pack(this.source, this.tileColor, this.destination );
    }


}
