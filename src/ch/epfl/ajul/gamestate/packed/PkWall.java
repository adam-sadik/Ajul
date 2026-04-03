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

    private static final int[] COLOR_MASKS = {
            COLOR_MASK_A, COLOR_MASK_B, COLOR_MASK_C, COLOR_MASK_D, COLOR_MASK_E
    };

    private static final int ROW0_MASK = 0b00000_00000_00000_00000_11111;
    private static final int COLUMN_MASK = 0b00001_00001_00001_00001_00001;



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
        return (line.index() + color.index()) % WALL_WIDTH;
    }

    /// Détermine la couleur acceptée par une case spécifique du mur.
    /// @param line La ligne du mur.
    /// @param column La colonne du mur.
    /// @return La couleur correspondante.
    public static TileKind.Colored colorAt(TileDestination.Pattern line, int column) {
        assert column >= 0 && column < WALL_WIDTH;
        int colorIndex = (line.index() * (WALL_WIDTH - 1) + column) % TileKind.Colored.COUNT;
        return TileKind.Colored.ALL.get(colorIndex);
    }

    /// Ajoute une tuile au mur.
    /// @param pkWall Le mur actuel.
    /// @param line La ligne où ajouter la tuile.
    /// @param color La couleur de la tuile à ajouter.
    /// @return Le mur mis à jour.
    public static int withTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        return PkIntSet32.add(pkWall, indexOf(line, color));
    }

    /// Vérifie si une case précise contient déjà une tuile.
    /// @param pkWall Le mur empaqueté.
    /// @param line La ligne de motif.
    /// @param color La couleur de la tuile.
    /// @return Vrai si la case est occupée.
    public static boolean hasTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        return PkIntSet32.contains(pkWall, indexOf(line, color));
    }

    /// Calcule la taille du groupe horizontal auquel appartient une tuile.
    /// @param pkWall Le mur empaqueté.
    /// @param line La ligne de motif.
    /// @param color La couleur de la tuile.
    /// @return La taille du groupe horizontal (1 à 5).
    public static int hGroupSize(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        int row = line.index();
        int col = column(line, color);
        int count = 1;

        // Vers la gauche
        for (int c = col - 1; c >= 0 && PkIntSet32.contains(pkWall, row * WALL_WIDTH + c); c--) count++;
        // Vers la droite
        for (int c = col + 1; c < WALL_WIDTH && PkIntSet32.contains(pkWall, row * WALL_WIDTH + c); c++) count++;

        return count;
    }

    /// Calcule la taille du groupe vertical auquel appartient une tuile.
    /// @param pkWall Le mur empaqueté.
    /// @param line La ligne de motif.
    /// @param color La couleur de la tuile.
    /// @return La taille du groupe vertical (1 à 5).
    public static int vGroupSize(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        int row = line.index();
        int col = column(line, color);
        int count = 1;

        // Vers le haut
        for (int r = row - 1; r >= 0 && PkIntSet32.contains(pkWall, r * WALL_WIDTH + col); r--) count++;
        // Vers le bas
        for (int r = row + 1; r < WALL_HEIGHT && PkIntSet32.contains(pkWall, r * WALL_WIDTH + col); r++) count++;

        return count;
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


    /// Vérifie si une ligne spécifique du mur est complète.
    ///
    /// @param pkWall le mur empaqueté
    /// @param line la ligne à vérifier
    /// @return vrai si les 5 cases de la ligne sont occupées
    public static boolean isRowFull(int pkWall, TileDestination.Pattern line) {
        return PkIntSet32.containsAll(pkWall, ROW0_MASK << (line.index() * WALL_WIDTH));
    }

    /// Vérifie si une colonne spécifique du mur est complète.
    ///
    /// @param pkWall le mur empaqueté
    /// @param column l'index de la colonne (0 à 4) à vérifier
    /// @return vrai si les 5 cases de la colonne sont occupées
    public static boolean isColumnFull(int pkWall, int column) {
        assert column >= 0 && column < WALL_WIDTH;
        return PkIntSet32.containsAll(pkWall, COLUMN_MASK << column);
    }


    /// Vérifie si une couleur spécifique est complète sur le mur (5 tuiles).
    /// @param pkWall Le mur empaqueté.
    /// @param color La couleur à vérifier.
    /// @return Vrai si les 5 tuiles de cette couleur sont posées.
    public static boolean isColorFull(int pkWall, TileKind.Colored color) {
        return PkIntSet32.containsAll(pkWall, COLOR_MASKS[color.index()]);

    }

    /// Convertit le mur empaqueté en un PkTileSet.
    /// @param pkWall Le mur empaqueté.
    /// @return Un entier représentant l'ensemble des tuiles du mur.
    public static int asPkTileSet(int pkWall) {
        int pkTileSet = PkTileSet.EMPTY;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            int count = Integer.bitCount(pkWall & COLOR_MASKS[color.index()]);
            if (count > 0) {
                pkTileSet = PkTileSet.union(pkTileSet, PkTileSet.of(count, color));
            }
        }
        return pkTileSet;
    }

    /// Retourne la représentation textuelle du mur.
    ///
    /// @param pkWall le mur empaqueté
    /// @return une chaîne décrivant le mur (majuscules si occupé, minuscules sinon)
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
