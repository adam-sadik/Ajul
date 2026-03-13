package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;
import ch.epfl.ajul.gamestate.packed.PkMove;

import java.util.Objects;

/// Représente un coup joué par un joueur d'Azul.
/// @param destination la destination sur laquelle les tuiles sont placées
/// @param source la source depuis laquelle les sources ont été prises
/// @param tileColor la couleur des tuiles choisies
/// @author Rayane TAOUFIK BENCHEKROUN (412052)
public record Move(TileSource source, TileKind.Colored tileColor, TileDestination destination) {

    /// Nombre Maximal de coups valides possibles dans une partie d'Ajul
    /// Il correspond au produit du nombre maximal de fabriques,
    /// du nombre maximal couleurs distinctes dans une fabrique,
    /// et du nombre de destinations possibles.
    public static final int MAX_MOVES = TileSource.Factory.COUNT*TileDestination.COUNT*TileSource.Factory.TILES_PER_FACTORY;

    /// Construit un coup avec la source, la couleur et la destination
    /// @throws NullPointerException si l'un des arguments (source,couleur,destination) est null
    public Move {
        Objects.requireNonNull(source);
        Objects.requireNonNull(tileColor);
        Objects.requireNonNull(destination);
    }

    /// Retourne le coup correspondant à la représentaton empaquetée donnée
    /// @param pkMove le coup empaqueté
    /// @return le coup correspondant.
    public static Move ofPacked(short pkMove){
        return new Move(PkMove.source(pkMove),PkMove.color(pkMove), PkMove.destination(pkMove));
    }

    /// Retourne la représentation empaquetée du coup
    /// @return le coup empaqueté correspondant
    public short packed(){
        return PkMove.pack(this.source, this.tileColor, this.destination );
    }


}
