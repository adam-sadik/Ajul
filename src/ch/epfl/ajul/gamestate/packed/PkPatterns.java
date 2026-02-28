package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import java.util.StringJoiner;

/// Contient des méthodes statiques permettant de manipuler le contenu
/// des lignes de motif d'un joueur, représentées de manière empaquetée.
///
/// @author Adam Ghali SADIK (412029)
public final class PkPatterns {

    /// Représente les lignes de motif vides.
    public static final int EMPTY = 0;

    private PkPatterns() {}

    /// Retourne le nombre de tuiles présentes sur la ligne de motif donnée.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @param line
    ///        la ligne de motif ciblée
    /// @return le nombre de tuiles présentes sur cette ligne
    public static int size(int pkPatterns, TileDestination.Pattern line) {
        return (pkPatterns >>> (6 * line.index())) & 0b111;
    }

    /// Retourne la couleur des tuiles présentes sur la ligne de motif donnée.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @param line
    ///        la ligne de motif ciblée
    /// @return la couleur des tuiles
    public static TileKind.Colored color(int pkPatterns, TileDestination.Pattern line) {
        assert size(pkPatterns, line) > 0;
        int pkColor = (pkPatterns >>> (6 * line.index() + 3)) & 0b111;
        return TileKind.Colored.ALL.get(pkColor);
    }

    /// Retourne vrai si et seulement si la ligne de motif donnée est pleine.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @param line
    ///        la ligne de motif ciblée
    /// @return vrai si la ligne est pleine, faux sinon
    public static boolean isFull(int pkPatterns, TileDestination.Pattern line) {
        return size(pkPatterns, line) == line.capacity();
    }

    /// Retourne vrai si et seulement si la ligne de motif peut contenir des tuiles de la couleur donnée.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @param line
    ///        la ligne de motif ciblée
    /// @param color
    ///        la couleur des tuiles à vérifier
    /// @return vrai si la ligne est vide ou de la même couleur, faux sinon
    public static boolean canContain(int pkPatterns, TileDestination.Pattern line, TileKind.Colored color) {
        return size(pkPatterns, line) == 0 || color(pkPatterns, line) == color;
    }

    /// Retourne des lignes de motif empaquetées identiques à celles données,
    /// mais avec le nombre indiqué de tuiles de la couleur précisée ajoutées à la ligne.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @param line
    ///        la ligne de motif ciblée
    /// @param tileCount
    ///        le nombre de tuiles à ajouter
    /// @param color
    ///        la couleur des tuiles ajoutées
    /// @return les nouvelles lignes de motif empaquetées
    public static int withAddedTiles(int pkPatterns, TileDestination.Pattern line, int tileCount, TileKind.Colored color) {
        assert tileCount > 0;
        assert canContain(pkPatterns, line, color);
        assert size(pkPatterns, line) + tileCount <= line.capacity();

        int newSize = size(pkPatterns, line) + tileCount;
        int newColorIndex = color.ordinal();
        int newBlock = newSize | (newColorIndex << 3);
        int clearedPatterns = pkPatterns & ~(0b111111 << (6 * line.index()));

        return clearedPatterns | (newBlock << (6 * line.index()));
    }

    /// Retourne des lignes de motif empaquetées identiques à celles données,
    /// mais avec la ligne ciblée vidée de toutes ses tuiles.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @param line
    ///        la ligne de motif à vider
    /// @return les nouvelles lignes de motif empaquetées
    public static int withEmptyLine(int pkPatterns, TileDestination.Pattern line) {
        return pkPatterns & ~(0b111111 << (6 * line.index()));
    }

    /// Retourne l'ensemble de tuiles empaqueté constitué de toutes les tuiles
    /// se trouvant sur les lignes de motif empaquetées.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @return l'ensemble de tuiles empaqueté correspondant
    public static int asPkTileSet(int pkPatterns) {
        int packedTileSet = PkTileSet.EMPTY;
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            int lineSize = size(pkPatterns, line);
            if (lineSize > 0) {
                TileKind.Colored lineColor = color(pkPatterns, line);
                int lineSet = PkTileSet.of(lineSize, lineColor);
                packedTileSet = PkTileSet.union(packedTileSet, lineSet);
            }
        }
        return packedTileSet;
    }

    /// Retourne la représentation textuelle des lignes de motif empaquetées.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @return la représentation textuelle
    public static String toString(int pkPatterns) {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            int currentSize = size(pkPatterns, line);
            int emptySpots = line.capacity() - currentSize;
            String lineRepresentation = "";

            if (currentSize > 0) {
                TileKind.Colored color = color(pkPatterns, line);
                lineRepresentation += color.name().repeat(currentSize);
            }
            lineRepresentation += ".".repeat(emptySpots);
            sj.add(lineRepresentation);
        }
        return sj.toString();
    }
}