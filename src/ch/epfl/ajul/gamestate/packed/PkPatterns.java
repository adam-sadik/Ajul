package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import java.util.StringJoiner;

/// Contient des méthodes statiques permettant de manipuler le contenu des lignes de motif d'un joueur, à savoir:
///
/// les 3 bits de poids faible contiennent le nombre de tuiles présentes sur la ligne de motif 1,
/// les 3 bits suivants contiennent l'index de la couleur des tuiles présentes sur la ligne de motif 1,
/// ou 0 si la ligne de motif est vide,
/// et ainsi de suite pour les lignes de motif 2 à 5,
/// et les 2 bits de poids fort valent toujours 0.
///
/// @author Adam Ghali SADIK (412029)
public final class PkPatterns {

    /// Représente les lignes de motif vides.
    public static final int EMPTY = 0;

    private static final int BITS_PER_LINE = 6;
    private static final int SIZE_BITS = 3;
    private static final int SIZE_MASK = (1 << SIZE_BITS) - 1;
    private static final int LINE_MASK = (1 << BITS_PER_LINE) - 1;



    /// Retourne le nombre de tuiles présentes sur la ligne de motif donnée.
    ///
    /// @param pkPatterns
    ///        les lignes de motif empaquetées
    /// @param line
    ///        la ligne de motif ciblée
    /// @return le nombre de tuiles présentes sur cette ligne
    public static int size(int pkPatterns, TileDestination.Pattern line) {
        return (pkPatterns >>> (BITS_PER_LINE * line.index())) & SIZE_MASK;
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
        int pkColor = (pkPatterns >>> (BITS_PER_LINE * line.index() + SIZE_BITS)) & SIZE_MASK;
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
        assert tileCount >= 0;
        assert canContain(pkPatterns, line, color);
        assert size(pkPatterns, line) + tileCount <= line.capacity();

        int newSize = size(pkPatterns, line) + tileCount;
        int newColorIndex = color.ordinal();
        int newBlock = newSize | (newColorIndex << SIZE_BITS);
        int shift = BITS_PER_LINE * line.index();
        int clearedPatterns = pkPatterns & ~(LINE_MASK << shift);

        return clearedPatterns | (newBlock << shift);
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
        return pkPatterns & ~(LINE_MASK << (BITS_PER_LINE * line.index()));
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

            String coloredPart = currentSize > 0 ? color(pkPatterns, line).name().repeat(currentSize) : "";
            sj.add(coloredPart + ".".repeat(emptySpots));

        }
        return sj.toString();
    }
}