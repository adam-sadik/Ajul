package ch.epfl.ajul;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyPointsTest {
    @Test
    void newWallTilePointsWorksForAllValidCombinations() {
        for (int h = 1; h <= 5; h++) {
            for (int v = 1; v <= 5; v++) {
                int expected;
                if (h == 1 && v == 1) expected = 1;
                else if (h == 1) expected = v;
                else if (v == 1) expected = h;
                else expected = h + v;

                assertEquals(expected, Points.newWallTilePoints(h, v));
            }
        }
    }

    @Test
    void floorPenaltyIsExactlyCorrectForAllIndices() {
        int[] expectedPenalties = {1, 1, 2, 2, 2, 3, 3}; // Index 0 à 6
        for (int i = 0; i < 7; i++) {
            assertEquals(expectedPenalties[i], Points.floorPenalty(i), "Erreur à l'index " + i);
        }
    }

    @Test
    void totalFloorPenaltyIsExactlyCorrectForAllCounts() {
        int[] expectedTotals = {0, 1, 2, 4, 6, 8, 11, 14}; // 0 à 7 tuiles
        for (int count = 0; count <= 7; count++) {
            assertEquals(expectedTotals[count], Points.totalFloorPenalty(count), "Erreur pour " + count + " tuiles");
        }
    }

    @Test
    void newWallTilePoints_isolatedTile_returnsOne() {
        assertEquals(1, Points.newWallTilePoints(1, 1));
    }

    @Test
    void newWallTilePoints_horizontalGroupOnly_returnsHorizontalSize() {
        assertEquals(2, Points.newWallTilePoints(2, 1));
        assertEquals(5, Points.newWallTilePoints(5, 1));
    }

    @Test
    void newWallTilePoints_verticalGroupOnly_returnsVerticalSize() {
        assertEquals(3, Points.newWallTilePoints(1, 3));
        assertEquals(4, Points.newWallTilePoints(1, 4));
    }

    @Test
    void newWallTilePoints_crossGroup_returnsSum() {
        assertEquals(4, Points.newWallTilePoints(2, 2));
        assertEquals(10, Points.newWallTilePoints(5, 5));
        assertEquals(7, Points.newWallTilePoints(3, 4));
    }

    @Test
    void floorPenalty_firstTwoTiles_penaltyIsOne() {
        assertEquals(1, Points.floorPenalty(0));
        assertEquals(1, Points.floorPenalty(1));
    }

    @Test
    void floorPenalty_middleThreeTiles_penaltyIsTwo() {
        assertEquals(2, Points.floorPenalty(2));
        assertEquals(2, Points.floorPenalty(3));
        assertEquals(2, Points.floorPenalty(4));
    }

    @Test
    void floorPenalty_lastTwoTiles_penaltyIsThree() {
        assertEquals(3, Points.floorPenalty(5));
        assertEquals(3, Points.floorPenalty(6));
    }

    @Test
    void totalFloorPenalty_zeroTiles_returnsZero() {
        assertEquals(0, Points.totalFloorPenalty(0));
    }

    @Test
    void totalFloorPenalty_oneTile_returnsOne() {
        assertEquals(1, Points.totalFloorPenalty(1));
    }

    @Test
    void totalFloorPenalty_twoTiles_returnsTwo() {
        assertEquals(2, Points.totalFloorPenalty(2));
    }

    @Test
    void totalFloorPenalty_fourTiles_returnsSix() {

        assertEquals(6, Points.totalFloorPenalty(4));
    }

    @Test
    void totalFloorPenalty_sevenTiles_returnsFourteen() {
        assertEquals(14, Points.totalFloorPenalty(7));
    }

    @Test
    void pointsConstantsAreCorrect() {
        assertEquals(2, Points.FULL_ROW_BONUS_POINTS);
        assertEquals(7, Points.FULL_COLUMN_BONUS_POINTS);
        assertEquals(10, Points.FULL_COLOR_BONUS_POINTS);
    }

    @Test
    void pointsNewWallTilePointsWorks() {
        // Tuile isolée (les deux groupes valent 1)
        assertEquals(1, Points.newWallTilePoints(1, 1));

        // Tuile ajoutant uniquement à l'horizontal
        assertEquals(4, Points.newWallTilePoints(4, 1));

        // Tuile ajoutant uniquement au vertical
        assertEquals(3, Points.newWallTilePoints(1, 3));

        // Tuile connectant à la fois l'horizontal et le vertical
        assertEquals(7, Points.newWallTilePoints(3, 4));
        assertEquals(10, Points.newWallTilePoints(5, 5));
    }

    @Test
    void pointsFloorPenaltyWorks() {
        assertEquals(1, Points.floorPenalty(0));
        assertEquals(1, Points.floorPenalty(1));
        assertEquals(2, Points.floorPenalty(2));
        assertEquals(2, Points.floorPenalty(3));
        assertEquals(2, Points.floorPenalty(4));
        assertEquals(3, Points.floorPenalty(5));
        assertEquals(3, Points.floorPenalty(6));
    }

    @Test
    void pointsTotalFloorPenaltyWorks() {
        assertEquals(0, Points.totalFloorPenalty(0));
        assertEquals(1, Points.totalFloorPenalty(1));
        assertEquals(2, Points.totalFloorPenalty(2));  // 1+1
        assertEquals(4, Points.totalFloorPenalty(3));  // 2+2
        assertEquals(6, Points.totalFloorPenalty(4));  // 4+2
        assertEquals(8, Points.totalFloorPenalty(5));  // 6+2
        assertEquals(11, Points.totalFloorPenalty(6)); // 8+3
        assertEquals(14, Points.totalFloorPenalty(7)); // 11+3
    }

    @Test
    void newWallTilePointsMatchesFigure4Examples() {
        // Exemples tirés explicitement de la donnée du projet (Figure 4)

        // "Si la tuile 0 était ajoutée... elle rapporterait 1 point" (h=1, v=1)
        assertEquals(1, Points.newWallTilePoints(1, 1));

        // "Si la tuile 2 était ajoutée... elle rapporterait 2 points" (groupe {2,3} -> h=2, v=1)
        assertEquals(2, Points.newWallTilePoints(2, 1));

        // "Si la tuile 23 était ajoutée... elle rapporterait 8 points"
        // (groupe H {20..24} -> h=5, groupe V {13,18,23} -> v=3) -> 5+3=8
        assertEquals(8, Points.newWallTilePoints(5, 3));
    }

    // =========================================================================
    // 2. TESTS EXHAUSTIFS DE newWallTilePoints
    // =========================================================================

    @Test
    void newWallTilePointsWorksExhaustivelyForStandardBoard() {
        // Le mur fait 5x5, on teste toutes les combinaisons possibles en jeu
        var remaining = 5 * 5;

        for (int h = 1; h <= 5; h++) {
            for (int v = 1; v <= 5; v++) {
                int expected = (h == 1) ? v : ((v == 1) ? h : h + v);
                assertEquals(expected, Points.newWallTilePoints(h, v));
                remaining -= 1;
            }
        }
        assertEquals(0, remaining);
    }

    // =========================================================================
    // 3. TESTS EXHAUSTIFS DE floorPenalty
    // =========================================================================

    @Test
    void floorPenaltyWorksExhaustively() {
        // On teste les 7 index possibles (de 0 à 6)
        // Règles : 1 pt pour les 2 premières, 2 pts pour les 3 suivantes, 3 pts pour les 2 dernières.
        assertEquals(1, Points.floorPenalty(0));
        assertEquals(1, Points.floorPenalty(1));
        assertEquals(2, Points.floorPenalty(2));
        assertEquals(2, Points.floorPenalty(3));
        assertEquals(2, Points.floorPenalty(4));
        assertEquals(3, Points.floorPenalty(5));
        assertEquals(3, Points.floorPenalty(6));
    }

    // =========================================================================
    // 4. TESTS EXHAUSTIFS DE totalFloorPenalty & PREUVE MATHÉMATIQUE
    // =========================================================================

    @Test
    void totalFloorPenaltyWorksExhaustively() {
        // On teste le cumul pour les tailles de plancher de 0 à 7
        assertEquals(0, Points.totalFloorPenalty(0));
        assertEquals(1, Points.totalFloorPenalty(1));
        assertEquals(2, Points.totalFloorPenalty(2)); // 1+1
        assertEquals(4, Points.totalFloorPenalty(3)); // 1+1+2
        assertEquals(6, Points.totalFloorPenalty(4)); // 1+1+2+2
        assertEquals(8, Points.totalFloorPenalty(5)); // 1+1+2+2+2
        assertEquals(11, Points.totalFloorPenalty(6)); // 1+1+2+2+2+3
        assertEquals(14, Points.totalFloorPenalty(7)); // 1+1+2+2+2+3+3
    }

    @Test
    void totalFloorPenaltyMathematicalProof() {
        // L'énoncé suggère que la constante peut être calculée via :
        // (FLOOR_PENALTY * 0x1111111) << 4
        // Ce test prouve que notre méthode (qui utilise la constante en dur)
        // donne EXACTEMENT le même résultat que si on appliquait cette formule mathématique.

        int floorPenaltyConstant = 0x3322211;
        int mathematicalTotalConstant = (floorPenaltyConstant * 0x1111111) << 4;

        // On boucle sur 0 à 7
        for (int i = 0; i <= 7; i++) {
            // Extraction via la formule mathématique calculée à la volée
            int expectedFromMath = (mathematicalTotalConstant >>> (i * 4)) & 0xF;

            // Extraction via notre méthode de classe
            int actualFromClass = Points.totalFloorPenalty(i);

            assertEquals(expectedFromMath, actualFromClass);
        }
    }

    // --- 1. TESTS DES CONSTANTES ---

    @Test
    void pointsConstantsAreCorrectlyDefined() {
        assertEquals(2, Points.FULL_ROW_BONUS_POINTS);
        assertEquals(7, Points.FULL_COLUMN_BONUS_POINTS);
        assertEquals(10, Points.FULL_COLOR_BONUS_POINTS);
    }

    // --- 2. TESTS DE newWallTilePoints ---

    @Test
    void newWallTilePointsTrivialCases() {
        // Tuile posée toute seule sans aucun voisin
        assertEquals(1, Points.newWallTilePoints(1, 1));

        // Tuile posée avec un voisin horizontal (groupe total de 2)
        assertEquals(2, Points.newWallTilePoints(2, 1));

        // Tuile posée avec deux voisins verticaux (groupe total de 3)
        assertEquals(3, Points.newWallTilePoints(1, 3));

        // Tuile posée à l'intersection d'un groupe H de 2 et V de 2
        assertEquals(4, Points.newWallTilePoints(2, 2));
    }

    @Test
    void newWallTilePointsWorksExhaustively() {
        var remaining = 5 * 5; // Le mur fait 5x5, la taille max d'un groupe est 5

        for (int h = 1; h <= 5; h++) {
            for (int v = 1; v <= 5; v++) {
                int expected;
                if (h == 1 && v == 1) expected = 1;
                else if (h == 1) expected = v;
                else if (v == 1) expected = h;
                else expected = h + v;

                assertEquals(expected, Points.newWallTilePoints(h, v));
                remaining -= 1;
            }
        }
        assertEquals(0, remaining);
    }

    // --- 3. TESTS DE floorPenalty ---

    @Test
    void floorPenaltyTrivialCases() {
        // La première tuile coûte 1 point
        assertEquals(1, Points.floorPenalty(0));

        // La 7ème tuile (index 6) coûte 3 points
        assertEquals(3, Points.floorPenalty(6));
    }

    // --- 4. TESTS DE totalFloorPenalty ---

    @Test
    void totalFloorPenaltyTrivialCases() {
        // Une ligne vide donne 0 de pénalité
        assertEquals(0, Points.totalFloorPenalty(0));

        // Une ligne pleine (7 tuiles) donne 14 de pénalité (1+1+2+2+2+3+3)
        assertEquals(14, Points.totalFloorPenalty(7));
    }

    @Test
    void totalFloorPenaltyCrossCheckWithIndividualPenalties() {
        // Test croisé : la pénalité totale pour N tuiles DOIT être égale
        // à la somme des pénalités individuelles des tuiles de 0 à N-1.

        for (int tilesCount = 1; tilesCount <= 7; tilesCount++) {
            int calculatedSum = 0;
            for (int i = 0; i < tilesCount; i++) {
                calculatedSum += Points.floorPenalty(i);
            }

            assertEquals(calculatedSum, Points.totalFloorPenalty(tilesCount));
        }
    }
}
