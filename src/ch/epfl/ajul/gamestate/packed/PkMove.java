package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;

/// Classe permettant de manipuler les coups empaquetés
/// dans une valeur de type short.
/// La représentation empaquetée utilise 10 bits répartis comme suit :
///  - 4 bits pour l’index de la source (bits 0 à 3),
///  - 3 bits pour l’index de la couleur (bits 4 à 6),
///  - 3 bits pour l’index de la destination (bits 7 à 9).
/// Les 6 bits de poids fort sont toujours égaux à 0.
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




    /// Retourne la représentation empaquetée du coup défini par la source, la couleur et la destination données
    /// @param source la source
    /// @param destination la destination
    /// @param color la couleur
    /// @return le coup empaqueté correspondant
    public static short pack(TileSource source, TileKind.Colored color, TileDestination destination) {
    int sourceIndex = source.index() << SOURCE_OFFSET;
    int colorIndex = color.index() << COLOR_OFFSET;
    int destinationIndex = destination.index() << DESTINATION_OFFSET;

    return (short) ((sourceIndex | colorIndex)|destinationIndex);
    }

    /// Retoune la source du coup empaqueté donnée
    /// @return la source correspondante
    public static TileSource source(short pkMove) {
        int sourceIndex = (pkMove & SOURCE_MASK) >> SOURCE_OFFSET;
        //assert sourceIndex < TileSource.ALL.size();
        return TileSource.ALL.get(sourceIndex);
    }

    /// Retourne la couleur du coup empaqueté donné
    /// @return la couleur courrespondante
    public static TileKind.Colored color(short pkMove){
        int colorIndex = ( pkMove & COLOR_MASK) >> COLOR_OFFSET;
        //assert colorIndex < TileKind.Colored.ALL.size();
        return TileKind.Colored.ALL.get(colorIndex);
    }

    /// Retourne la destination du coup empqueté donné
    /// @return la destination correspondante
    public static TileDestination destination(short pkMove){
        int destinationIndex = ( pkMove & DESTINATION_MASK) >> DESTINATION_OFFSET;
        //assert destinationIndex < TileDestination.ALL.size();
        return TileDestination.ALL.get(destinationIndex);
    }


}
