package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;

/// @author Rayane Taoufik Benchekroun (412052)
public final class PkMove {


    private static final int SOURCE_OFFSET = 0;
    private static final int SOURCE_BITS = 4;
    private static final int SOURCE_MASK = (1 << SOURCE_BITS) - 1;
    private static final int COLOR_OFFSET = SOURCE_OFFSET + SOURCE_BITS;
    private static final int COLOR_BITS = 3;
    private static final int COLOR_MASK = ((1 << COLOR_BITS) - 1) << COLOR_OFFSET ;
    private static final int DESTINATION_OFFSET = SOURCE_OFFSET + SOURCE_BITS + COLOR_BITS;
    private static final int DESTINATION_BITS = 3;
    private static final int DESTINATION_MASK =  ((1 << DESTINATION_BITS) - 1) << DESTINATION_OFFSET;


    public static short pack(TileSource source, TileKind.Colored color, TileDestination destination) {
    int sourceIndex = source.index() << SOURCE_OFFSET;
    int colorIndex = color.index() << COLOR_OFFSET;
    int destinationIndex = destination.index() << DESTINATION_OFFSET;

    return (short) ((sourceIndex | colorIndex)|destinationIndex);
    }

    public static TileSource source(short pkMove) {
        int sourceIndex = (pkMove & SOURCE_MASK) >> SOURCE_OFFSET;
        assert sourceIndex < TileSource.ALL.size();
        return TileSource.ALL.get(sourceIndex);
    }

    public static TileKind.Colored color(short pkMove){
        int colorIndex = ( pkMove & COLOR_MASK) >> COLOR_OFFSET;
        assert colorIndex < TileKind.Colored.ALL.size();
        return TileKind.Colored.ALL.get(colorIndex);
    }

    public static TileDestination destination(short pkMove){
        int destinationIndex = ( pkMove & DESTINATION_MASK) >> DESTINATION_OFFSET;
        assert destinationIndex < TileDestination.ALL.size();
        return TileDestination.ALL.get(destinationIndex);
    }


}
