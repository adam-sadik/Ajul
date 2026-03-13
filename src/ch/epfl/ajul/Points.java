package ch.epfl.ajul;

/// Classe utilitaire pour le calcul des points et des pénalités lors d'une partie.
/// @author Rayane Taoufik Benchekroun (412052)
/// @author Adam Ghali SADIK (412029)
public final class Points {

    /// Points bonus pour une ligne complète.
    public static final int FULL_ROW_BONUS_POINTS = 2;

    /// Points bonus pour une colonne complète.
    public static final int FULL_COLUMN_BONUS_POINTS = 7;

    /// Points bonus pour une couleur complète.
    public static final int FULL_COLOR_BONUS_POINTS = 10;

    /// Pénalités unitaires par tuile de la ligne plancher, empaquetées.
    private static final int FLOOR_PENALTY = 0x3322211;

    /// Pénalités totales cumulées pour la ligne plancher, empaquetées.
    private static final int TOTAL_FLOOR_PENALTY = 0xEB864210;

    /// Calcule les points obtenus lors de l'ajout d'une tuile au mur.
    /// @param hGroupSize Taille du groupe horizontal auquel appartient la tuile.
    /// @param vGroupSize Taille du groupe vertical auquel appartient la tuile.
    /// @return Le score rapporté par la tuile (h, v ou h+v).
    public static int newWallTilePoints(int hGroupSize, int vGroupSize) {
        //assert hGroupSize >= 1 && hGroupSize <= 5;
        //assert vGroupSize >= 1 && vGroupSize <= 5;
        if (vGroupSize == 1) { return hGroupSize; }
        else if (hGroupSize == 1) { return vGroupSize; }
        else return hGroupSize + vGroupSize;
    }

    /// Retourne la pénalité associée à une position précise dans la ligne plancher.
    /// @param tileIndex L'index de la tuile (0-6).
    /// @return La valeur de la pénalité (1, 2 ou 3).
    public static int floorPenalty(int tileIndex) {
        //assert tileIndex >= 0 && tileIndex < 7;
        return (FLOOR_PENALTY >>> (tileIndex * 4)) & 0xF;
    }

    /// Retourne la pénalité totale pour un nombre donné de tuiles dans la ligne plancher.
    /// @param tilesCount Le nombre total de tuiles (0-7).
    /// @return La somme des pénalités (0-14).
    public static int totalFloorPenalty(int tilesCount) {
        //assert tilesCount >= 0 && tilesCount <= 7;
        return (TOTAL_FLOOR_PENALTY >>> (tilesCount * 4)) & 0xF;
    }
}
