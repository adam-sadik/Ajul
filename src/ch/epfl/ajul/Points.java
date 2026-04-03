package ch.epfl.ajul;

/// Classe utilitaire pour le calcul des points et des pénalités lors d'une partie.
/// @author Rayane Taoufik Benchekroun (412052)
/// @author Adam Ghali SADIK (412029)
public final class Points {

    private static final int BITS_PER_PENALTY = 4;
    private static final int PENALTY_MASK = 0xF;
    private static final int MAX_GROUP_SIZE = 5;
    private static final int MAX_FLOOR_TILES = 7;

    /// Points bonus pour une ligne complète.
    public static final int FULL_ROW_BONUS_POINTS = 2;

    /// Points bonus pour une colonne complète.
    public static final int FULL_COLUMN_BONUS_POINTS = 7;

    /// Points bonus pour une couleur complète.
    public static final int FULL_COLOR_BONUS_POINTS = 10;

    /// Pénalités unitaires par tuile de la ligne plancher, empaquetées.
    private static final int FLOOR_PENALTY = 0x3322211;

    /// Pénalités totales cumulées pour la ligne plancher, empaquetées.
    ///L'utilisation du type long ('L') évite l'overflow, avant le décalage et le cast en (int).
    private static final int TOTAL_FLOOR_PENALTY = (int) ((FLOOR_PENALTY * 0x1111111L) << 4);


    /// Calcule les points obtenus lors de l'ajout d'une tuile au mur.
    /// @param hGroupSize Taille du groupe horizontal auquel appartient la tuile.
    /// @param vGroupSize Taille du groupe vertical auquel appartient la tuile.
    /// @return Le score rapporté par la tuile (h, v ou h+v).
    public static int newWallTilePoints(int hGroupSize, int vGroupSize) {
        assert hGroupSize >= 1 && hGroupSize <= MAX_GROUP_SIZE;
        assert vGroupSize >= 1 && vGroupSize <= MAX_GROUP_SIZE;
        return (hGroupSize == 1 || vGroupSize == 1) ?
                Math.max(hGroupSize, vGroupSize) :
                (hGroupSize + vGroupSize);
    }

    /// Retourne la pénalité associée à une position précise dans la ligne plancher.
    /// @param tileIndex L'index de la tuile (0-6).
    /// @return La valeur de la pénalité (1, 2 ou 3).
    public static int floorPenalty(int tileIndex) {
        assert tileIndex >= 0 && tileIndex < MAX_FLOOR_TILES;
        return (FLOOR_PENALTY >>> (tileIndex * BITS_PER_PENALTY)) & PENALTY_MASK;
    }

    /// Retourne la pénalité totale pour un nombre donné de tuiles dans la ligne plancher.
    /// @param tilesCount Le nombre total de tuiles (0-7).
    /// @return La somme des pénalités (0-14).
    public static int totalFloorPenalty(int tilesCount) {
        assert tilesCount >= 0 && tilesCount <= MAX_FLOOR_TILES;
        return (TOTAL_FLOOR_PENALTY >>> (tilesCount * BITS_PER_PENALTY)) & PENALTY_MASK;
    }
}
