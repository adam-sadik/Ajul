package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;

import java.util.StringJoiner;

/// Classe utilitaire pour manipuler le contenu du mur d'un joueur sous forme empaquetée.
/// Le mur est représenté par un ensemble d'index (0 à 24) stockés dans un `int`.
/// @author Rayane Taoufik Benchekroun (412052)
/// @author Adam Ghali SADIK (412029)
public final class PkWall {

    /// Représente un mur vide.
    public static final int EMPTY = 0;
    /// Largeur du mur (nombre de colonnes).
    public static final int WALL_WIDTH = 5;

    /// Hauteur du mur (nombre de lignes).
    public static final int WALL_HEIGHT = 5;

    private static final int COLOR_MASK_A = 0b10000_01000_00100_00010_00001;
    private static final int COLOR_MASK_B = 0b00001_10000_01000_00100_00010;
    private static final int COLOR_MASK_C = 0b00010_00001_10000_01000_00100;
    private static final int COLOR_MASK_D = 0b00100_00010_00001_10000_01000;
    private static final int COLOR_MASK_E = 0b01000_00100_00010_00001_10000;
    private static final int ROW0_MASK = 0b00000_00000_00000_00000_11111;
    private static final int COLUMN_MASK = 0b00001_00001_00001_00001_00001;


    private static int countHorizontal(int pkWall, TileDestination.Pattern line, int startCol, int step) {
        assert startCol >= 0 && startCol < WALL_WIDTH;
        int count = 0;
        int col = startCol + step;
        while (col >= 0 && col < WALL_WIDTH && hasTileAt(pkWall, line, colorAt(line, col))) {
            count++;
            col += step;
        }
        return count;
    }

    private static int countVertical(int pkWall, int col, int startRow, int step) {
        assert col >= 0 && col < WALL_WIDTH;
        assert startRow >= 0 && startRow < WALL_HEIGHT;
        int count = 0;
        int row = startRow + step;
        while (row >= 0 && row < WALL_HEIGHT) {
            TileDestination.Pattern currentLine = TileDestination.Pattern.ALL.get(row);
            if (!hasTileAt(pkWall, currentLine, colorAt(currentLine, col))) {
                break;
            }
            count++;
            row += step;
        }
        return count;
    }

    /// Calcule l'index (0-24) d'une case du mur à partir de sa ligne et sa couleur.
    /// @param line La ligne de motif correspondante.
    /// @param color La couleur de la tuile.
    /// @return L'index de la case.
    public static int indexOf(TileDestination.Pattern line, TileKind.Colored color) {
        return column(line, color) + line.index() * WALL_WIDTH;
    }

    /// Calcule le numéro de colonne (0-4) d'une case à partir de sa ligne et sa couleur.
    /// @param line La ligne de motif.
    /// @param color La couleur de la tuile.
    /// @return L'index de la colonne.
    public static int column(TileDestination.Pattern line, TileKind.Colored color) {
        return ((line.index() + color.index()) % WALL_WIDTH);
    }

    /// Détermine la couleur acceptée par une case spécifique du mur.
    /// @param line La ligne du mur.
    /// @param column La colonne du mur.
    /// @return La couleur correspondante.
    public static TileKind.Colored colorAt(TileDestination.Pattern line, int column) {
        assert column >= 0 && column < WALL_WIDTH;
        return TileKind.Colored.ALL.get((line.index() * 4 + column) % WALL_HEIGHT);
    }

    /// Ajoute une tuile au mur.
    /// @param pkWall Le mur actuel.
    /// @param line La ligne où ajouter la tuile.
    /// @param color La couleur de la tuile à ajouter.
    /// @return Le mur mis à jour.
    public static int withTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        return pkWall | (1 << (indexOf(line, color)));
    }

    /// Vérifie si une case précise contient déjà une tuile.
    /// @param pkWall Le mur empaqueté.
    /// @param line La ligne de motif.
    /// @param color La couleur de la tuile.
    /// @return Vrai si la case est occupée.
    public static boolean hasTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        return ((pkWall >> (indexOf(line, color)) & 1) == 1);
    }

    /// Calcule la taille du groupe horizontal auquel appartient une tuile.
    /// @param pkWall Le mur empaqueté.
    /// @param line La ligne de motif.
    /// @param color La couleur de la tuile.
    /// @return La taille du groupe horizontal (1 à 5).
    public static int hGroupSize(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        int col = column(line, color);
        return 1 + countHorizontal(pkWall, line, col, 1) + countHorizontal(pkWall, line, col, -1);
    }

    /// Calcule la taille du groupe vertical auquel appartient une tuile.
    /// @param pkWall Le mur empaqueté.
    /// @param line La ligne de motif.
    /// @param color La couleur de la tuile.
    /// @return La taille du groupe vertical (1 à 5).
    public static int vGroupSize(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        int col = column(line, color);
        int row = line.index();
        return 1 + countVertical(pkWall, col, row, 1) + countVertical(pkWall, col, row, -1);
    }

    /// Vérifie si le mur possède au moins une ligne complète.
    /// Utile pour déterminer la fin de la partie.
    /// @param pkWall Le mur empaqueté.
    /// @return Vrai si au moins une ligne est pleine.
    public static boolean hasFullRow(int pkWall) {
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL){
            if (isRowFull(pkWall, line)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isRowFull(int pkWall, TileDestination.Pattern line) {
        return PkIntSet32.containsAll(pkWall, ROW0_MASK << line.index() * WALL_WIDTH);
    }

    public static boolean isColumnFull(int pkWall, int column) {
        assert column >= 0 && column < WALL_WIDTH;
        return PkIntSet32.containsAll(pkWall, COLUMN_MASK << column);
    }


    /// Vérifie si une couleur spécifique est complète sur le mur (5 tuiles).
    /// @param pkWall Le mur empaqueté.
    /// @param color La couleur à vérifier.
    /// @return Vrai si les 5 tuiles de cette couleur sont posées.
    public static boolean isColorFull(int pkWall, TileKind.Colored color) {
        return switch (color){
            case TileKind.Colored.A -> PkIntSet32.containsAll(pkWall, COLOR_MASK_A);
            case TileKind.Colored.B -> PkIntSet32.containsAll(pkWall, COLOR_MASK_B);
            case TileKind.Colored.C -> PkIntSet32.containsAll(pkWall, COLOR_MASK_C);
            case TileKind.Colored.D -> PkIntSet32.containsAll(pkWall, COLOR_MASK_D);
            case TileKind.Colored.E -> PkIntSet32.containsAll(pkWall, COLOR_MASK_E);
        };

    }

    /// Convertit le mur empaqueté en un PkTileSet.
    /// @param pkWall Le mur empaqueté.
    /// @return Un entier représentant l'ensemble des tuiles du mur.
    public static int asPkTileSet(int pkWall) {
        int countA = Integer.bitCount(pkWall & COLOR_MASK_A);
        int countB = Integer.bitCount(pkWall & COLOR_MASK_B);
        int countC = Integer.bitCount(pkWall & COLOR_MASK_C);
        int countD = Integer.bitCount(pkWall & COLOR_MASK_D);
        int countE = Integer.bitCount(pkWall & COLOR_MASK_E);

        int packedA = PkTileSet.of(countA, TileKind.Colored.A);
        int packedB = PkTileSet.of(countB, TileKind.Colored.B);
        int packedC = PkTileSet.of(countC, TileKind.Colored.C);
        int packedD = PkTileSet.of(countD, TileKind.Colored.D);
        int packedE = PkTileSet.of(countE, TileKind.Colored.E);

        int unionA_B = PkTileSet.union(packedA, packedB);
        int unionC_D = PkTileSet.union(packedC, packedD);
        int unionA_B_C_D = PkTileSet.union(unionA_B, unionC_D);

        return PkTileSet.union(unionA_B_C_D, packedE);
    }

    public static String toString(int pkWall){
        StringJoiner joiner = new StringJoiner(", ", "[", "]");

        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < WALL_WIDTH; ++i) {
                TileKind.Colored color = colorAt(line, i);
                String name = color.toString();
                builder.append(hasTileAt(pkWall, line, color) ? name : name.toLowerCase());
            }
            joiner.add(builder.toString());
        }

        return joiner.toString();
    }
}
