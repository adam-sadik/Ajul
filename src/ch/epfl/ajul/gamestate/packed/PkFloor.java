package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileKind;
import java.util.StringJoiner;

/// Contient des méthodes statiques permettant de manipuler le contenu d'une ligne plancher empaqueté, à savoir:
///
/// les 3 bits de poids faible contiennent le nombre de tuiles que contient la ligne plancher,
/// les 3 bits suivants contiennent l'index de la première tuile se trouvant sur la ligne plancher,
/// ou 0 s'il n'y en a aucune, et ainsi de suite pour les 6 autres tuiles qui peuvent se trouver sur la ligne plancher,
/// et les 8 bits de poids fort valent toujours 0.
/// @author Adam Ghali SADIK (412029)
public final class PkFloor {

    /// Représente la ligne plancher vide.
    public static final int EMPTY = 0;

    private static final int BITS_PER_TILE = 3;
    private static final int TILE_MASK = (1 << BITS_PER_TILE) - 1;
    private static final int MAX_CAPACITY = 7;

    /// Retourne la taille de la ligne plancher empaquetée.
    ///
    /// @param pkFloor
    ///        la ligne plancher empaquetée
    /// @return le nombre de tuiles qu'elle contient (entre 0 et 7)
    public static int size(int pkFloor) {
        return TILE_MASK & pkFloor;
    }

    /// Retourne la sorte de tuile se trouvant à l'index donné de la ligne plancher empaquetée.
    ///
    /// @param pkFloor
    ///        la ligne plancher empaquetée
    /// @param i
    ///        l'index de la tuile sur la ligne plancher
    /// @return la sorte de tuile correspondante
    public static TileKind tileAt(int pkFloor, int i) {
        assert i >= 0 && i < size(pkFloor);
        int shift = BITS_PER_TILE * (i + 1);
        int tileKindIndex = (pkFloor >>> shift) & TILE_MASK;
        return TileKind.ALL.get(tileKindIndex);
    }

    /// Retourne une ligne plancher empaquetée identique à celle donnée, mais avec
    /// les tuiles de l'ensemble empaqueté ajoutées par ordre de sorte.
    /// Les tuiles excédentaires sont ignorées, sauf le marqueur de premier joueur,
    ///  qui doit toujours être ajouté, en remplaçant au besoin la dernière tuile,
    ///
    /// @param pkFloor
    ///        la ligne plancher empaquetée initiale
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté à ajouter
    /// @return la nouvelle ligne plancher empaquetée
    public static int withAddedTiles(int pkFloor, int pkTileSet) {
        int currentFloor = pkFloor;
        for (TileKind kind : TileKind.ALL) {
            int count = PkTileSet.countOf(pkTileSet, kind);
            for (int k = 0; k < count; k++) {
                int currentSize = size(currentFloor);
                if (currentSize < MAX_CAPACITY) {
                    int shift = BITS_PER_TILE * (currentSize + 1);
                    currentFloor |= (kind.index() << shift);
                    currentFloor = (currentFloor & ~TILE_MASK) | (currentSize + 1);
                } else if (kind == TileKind.FIRST_PLAYER_MARKER) {
                    int shift = BITS_PER_TILE * MAX_CAPACITY;
                    currentFloor &= ~(TILE_MASK << shift);
                    currentFloor |= (kind.index() << shift);
                }
            }
        }
        return currentFloor;
    }

    /// Retourne vrai si et seulement si la ligne plancher contient le marqueur de premier joueur.
    ///
    /// @param pkFloor
    ///        la ligne plancher empaquetée
    /// @return vrai si le marqueur est présent, faux sinon
    public static boolean containsFirstPlayerMarker(int pkFloor) {
        int floorSize = size(pkFloor);
        for (int i = 0; i < floorSize; i++) {
            if (tileAt(pkFloor, i) == TileKind.FIRST_PLAYER_MARKER) {
                return true;
            }
        }
        return false;
    }

    /// Retourne l'ensemble de tuiles empaqueté constitué de toutes les tuiles
    /// se trouvant sur la ligne plancher empaquetée.
    ///
    /// @param pkFloor
    ///        la ligne plancher empaquetée
    /// @return l'ensemble de tuiles empaqueté correspondant
    public static int asPkTileSet(int pkFloor) {
        int packedTileSet = PkTileSet.EMPTY;
        int floorSize = size(pkFloor);

        for (int i = 0; i < floorSize; i++) {
            packedTileSet = PkTileSet.add(packedTileSet, tileAt(pkFloor, i));
        }

        return packedTileSet;
    }

    /// Retourne la représentation textuelle de la ligne plancher empaquetée.
    ///
    /// @param pkFloor
    ///        la ligne plancher empaquetée
    /// @return la représentation textuelle de la ligne
    public static String toString(int pkFloor) {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        int floorSize = size(pkFloor);

        for (int i = 0; i < floorSize; i++) {
            sj.add(tileAt(pkFloor, i).toString());
        }

        return sj.toString();
    }
}