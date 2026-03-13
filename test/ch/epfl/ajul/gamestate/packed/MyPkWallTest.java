package ch.epfl.ajul.gamestate.packed;
import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyPkWallTest {
    @Test
    void emptyWallIsEmpty() {
        assertEquals(0, PkWall.EMPTY);
        assertFalse(PkWall.hasFullRow(PkWall.EMPTY));
        assertEquals("[abcde, eabcd, deabc, cdeab, bcdea]", PkWall.toString(PkWall.EMPTY));
    }

    @Test
    void indexOfAndColorAtAreConsistent() {
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            for (int col = 0; col < PkWall.WALL_WIDTH; col++) {
                TileKind.Colored color = PkWall.colorAt(line, col);
                int expectedIndex = line.index() * 5 + col;

                assertEquals(col, PkWall.column(line, color));
                assertEquals(expectedIndex, PkWall.indexOf(line, color));
            }
        }
    }

    @Test
    void withTileAtAndHasTileAtWork() {
        int wall = PkWall.EMPTY;
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            for (TileKind.Colored color : TileKind.Colored.ALL) {
                wall = PkWall.withTileAt(wall, line, color);
                assertTrue(PkWall.hasTileAt(wall, line, color));
            }
        }
    }

    @Test
    void hasFullRowDoesNotTriggerOnIncompleteRows() {
        // Le "vice" : la ligne 0 a les tuiles aux colonnes 1, 2, 3, 4 mais PAS 0.
        // C'est exactement le bug qu'on a corrigé tout à l'heure !
        int wall = PkWall.EMPTY;
        TileDestination.Pattern line0 = TileDestination.Pattern.ALL.get(0);

        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 1));
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 2));
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 3));
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 4));

        assertFalse(PkWall.hasFullRow(wall));
        assertFalse(PkWall.isRowFull(wall, line0));

        // On ajoute la dernière
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 0));
        assertTrue(PkWall.hasFullRow(wall));
        assertTrue(PkWall.isRowFull(wall, line0));
    }

    @Test
    void isColorFullWorksForAllColors() {
        for (TileKind.Colored targetColor : TileKind.Colored.ALL) {
            int wall = PkWall.EMPTY;
            // On remplit uniquement cette couleur partout
            for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
                wall = PkWall.withTileAt(wall, line, targetColor);
            }
            assertTrue(PkWall.isColorFull(wall, targetColor));

            // On vérifie qu'une autre couleur n'est pas considérée comme pleine
            TileKind.Colored otherColor = targetColor == TileKind.Colored.A ? TileKind.Colored.B : TileKind.Colored.A;
            assertFalse(PkWall.isColorFull(wall, otherColor));
        }
    }

    @Test
    void groupsSizeWorksForIsolatedAndConnectedTiles() {
        int wall = PkWall.EMPTY;
        TileDestination.Pattern line0 = TileDestination.Pattern.ALL.get(0);
        TileDestination.Pattern line1 = TileDestination.Pattern.ALL.get(1);
        TileKind.Colored colorCol0Line0 = PkWall.colorAt(line0, 0);
        TileKind.Colored colorCol1Line0 = PkWall.colorAt(line0, 1);
        TileKind.Colored colorCol0Line1 = PkWall.colorAt(line1, 0);

        // Tuile isolée en (0,0)
        wall = PkWall.withTileAt(wall, line0, colorCol0Line0);
        assertEquals(1, PkWall.hGroupSize(wall, line0, colorCol0Line0));
        assertEquals(1, PkWall.vGroupSize(wall, line0, colorCol0Line0));

        // Ajout d'un voisin horizontal en (0,1)
        wall = PkWall.withTileAt(wall, line0, colorCol1Line0);
        assertEquals(2, PkWall.hGroupSize(wall, line0, colorCol0Line0));
        assertEquals(2, PkWall.hGroupSize(wall, line0, colorCol1Line0)); // Dans les deux sens !

        // Ajout d'un voisin vertical en (1,0)
        wall = PkWall.withTileAt(wall, line1, colorCol0Line1);
        assertEquals(2, PkWall.vGroupSize(wall, line0, colorCol0Line0));
        assertEquals(2, PkWall.vGroupSize(wall, line1, colorCol0Line1));
    }

    @Test
    void constants_areCorrect() {
        assertEquals(0, PkWall.EMPTY);
        assertEquals(5, PkWall.WALL_WIDTH);
        assertEquals(5, PkWall.WALL_HEIGHT);
    }

    @Test
    void indexOf_corners_areCorrect() {
        TileDestination.Pattern firstLine = TileDestination.Pattern.ALL.get(0);
        TileDestination.Pattern lastLine = TileDestination.Pattern.ALL.get(4);

        assertEquals(0, PkWall.indexOf(firstLine, TileKind.Colored.A)); // Haut gauche (L0, C0 = couleur A)
        assertEquals(24, PkWall.indexOf(lastLine, TileKind.Colored.A)); // Bas droite (L4, C4 = couleur A)
    }

    @Test
    void colorAt_diagonal_isAlwaysSameColor() {
        // La diagonale principale (0,0), (1,1), (2,2), (3,3), (4,4) doit toujours être de couleur A
        for (int i = 0; i < 5; i++) {
            TileDestination.Pattern line = TileDestination.Pattern.ALL.get(i);
            assertEquals(TileKind.Colored.A, PkWall.colorAt(line, i));
        }
    }

    // --- Tests d'ajout et de vérification ---
    @Test
    void withTileAt_addsCorrectTile() {
        TileDestination.Pattern line2 = TileDestination.Pattern.ALL.get(2);
        int wall = PkWall.withTileAt(PkWall.EMPTY, line2, TileKind.Colored.C);
        assertTrue(PkWall.hasTileAt(wall, line2, TileKind.Colored.C));
    }

    @Test
    void hasTileAt_onEmptyWall_isAlwaysFalse() {
        TileDestination.Pattern line = TileDestination.Pattern.ALL.get(0);
        assertFalse(PkWall.hasTileAt(PkWall.EMPTY, line, TileKind.Colored.A));
    }

    // --- Tests des groupes (Calcul des points) ---
    @Test
    void hGroupSize_withHole_stopsCountingAtHole() {
        int wall = PkWall.EMPTY;
        TileDestination.Pattern line0 = TileDestination.Pattern.ALL.get(0);

        // On ajoute en colonne 0, 1, et 3 (trou en 2)
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 0));
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 1));
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 3));

        // Si on pose en 1, le groupe fait 2 (col 0 et 1). Il ne doit pas compter la col 3.
        assertEquals(2, PkWall.hGroupSize(wall, line0, PkWall.colorAt(line0, 1)));
    }

    @Test
    void vGroupSize_fullColumn_returnsFive() {
        int wall = PkWall.EMPTY;
        int targetCol = 2;

        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            wall = PkWall.withTileAt(wall, line, PkWall.colorAt(line, targetCol));
        }

        TileDestination.Pattern middleLine = TileDestination.Pattern.ALL.get(2);
        assertEquals(5, PkWall.vGroupSize(wall, middleLine, PkWall.colorAt(middleLine, targetCol)));
    }

    // --- Tests des lignes, colonnes et couleurs pleines ---
    @Test
    void isRowFull_onlyWhenCompletelyFull() {
        int wall = PkWall.EMPTY;
        TileDestination.Pattern line0 = TileDestination.Pattern.ALL.get(0);

        // On remplit 4 cases sur 5
        for (int i = 0; i < 4; i++) {
            wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, i));
        }
        assertFalse(PkWall.isRowFull(wall, line0));

        // Ajout de la dernière
        wall = PkWall.withTileAt(wall, line0, PkWall.colorAt(line0, 4));
        assertTrue(PkWall.isRowFull(wall, line0));
    }

    @Test
    void hasFullRow_withOneFullRow_returnsTrue() {
        int wall = PkWall.EMPTY;
        TileDestination.Pattern line4 = TileDestination.Pattern.ALL.get(4);

        for (int i = 0; i < 5; i++) {
            wall = PkWall.withTileAt(wall, line4, PkWall.colorAt(line4, i));
        }
        assertTrue(PkWall.hasFullRow(wall));
    }

    @Test
    void isColorFull_whenScatteredCorrectly_returnsTrue() {
        int wall = PkWall.EMPTY;
        TileKind.Colored targetColor = TileKind.Colored.E;

        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            wall = PkWall.withTileAt(wall, line, targetColor);
        }
        assertTrue(PkWall.isColorFull(wall, targetColor));
    }

    // --- Tests des conversions ---
    @Test
    void toString_emptyWall_isCorrect() {
        String expected = "[abcde, eabcd, deabc, cdeab, bcdea]";
        assertEquals(expected, PkWall.toString(PkWall.EMPTY));
    }

    @Test
    void toString_withSpecificTiles_isCorrect() {
        int wall = PkWall.EMPTY;
        TileDestination.Pattern line0 = TileDestination.Pattern.ALL.get(0);

        // Ajout de la couleur A (col 0) et C (col 2) sur la première ligne
        wall = PkWall.withTileAt(wall, line0, TileKind.Colored.A);
        wall = PkWall.withTileAt(wall, line0, TileKind.Colored.C);

        // On s'attend à ce que A et C soient en majuscules sur le premier élément
        String result = PkWall.toString(wall);
        assertTrue(result.startsWith("[AbCde,"));
    }
    @Test
    void pkWallConstantsAreCorrect() {
        assertEquals(0, PkWall.EMPTY);
        assertEquals(5, PkWall.WALL_WIDTH);
        assertEquals(5, PkWall.WALL_HEIGHT);
    }

    @Test
    void pkWallColumnAndColorAtWorkInSynch() {
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            for (TileKind.Colored color : TileKind.Colored.ALL) {
                int col = PkWall.column(line, color);
                assertTrue(col >= 0 && col < 5);
                // Vérifier la réversibilité
                assertEquals(color, PkWall.colorAt(line, col));
            }
        }
    }

    @Test
    void pkWallIndexOfWorksProperly() {
        // Test manuel basé sur la figure 2
        assertEquals(24, PkWall.indexOf(TileDestination.Pattern.PATTERN_5, TileKind.Colored.A));
        assertEquals(0, PkWall.indexOf(TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));
        assertEquals(1, PkWall.indexOf(TileDestination.Pattern.PATTERN_1, TileKind.Colored.B));
        assertEquals(5, PkWall.indexOf(TileDestination.Pattern.PATTERN_2, TileKind.Colored.E));
    }

    @Test
    void pkWallWithTileAtAndHasTileAtWork() {
        int wall = PkWall.EMPTY;
        TileDestination.Pattern line = TileDestination.Pattern.PATTERN_3; // Index 2
        TileKind.Colored color = TileKind.Colored.A; // Colonne 2 (Index 12)

        assertFalse(PkWall.hasTileAt(wall, line, color));
        wall = PkWall.withTileAt(wall, line, color);
        assertTrue(PkWall.hasTileAt(wall, line, color));

        // S'assurer qu'aucune autre tuile n'a été ajoutée
        assertFalse(PkWall.hasTileAt(wall, line, TileKind.Colored.B));
    }

    @Test
    void pkWallGroupSizesWork() {
        int wall = PkWall.EMPTY;

        // Construction d'un mur avec un groupe horizontal de 3 et un groupe vertical de 2
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.B); // ligne 0, col 1
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.C); // ligne 0, col 2
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.D); // ligne 0, col 3
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.A); // ligne 1, col 1

        // Testons à la position (Ligne 0, Couleur B)
        assertEquals(3, PkWall.hGroupSize(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.B));
        assertEquals(2, PkWall.vGroupSize(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.B));

        // Testons avec une tuile isolée non ajoutée (devrait valoir 1)
        assertEquals(1, PkWall.hGroupSize(wall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.E));
    }

    @Test
    void pkWallFullCheckersWork() {
        int wall = PkWall.EMPTY;

        // Remplir la première ligne
        for(TileKind.Colored c : TileKind.Colored.ALL) {
            wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, c);
        }
        assertTrue(PkWall.isRowFull(wall, TileDestination.Pattern.PATTERN_1));
        assertTrue(PkWall.hasFullRow(wall));
        assertFalse(PkWall.isRowFull(wall, TileDestination.Pattern.PATTERN_2));

        // Remplir la colonne 0
        int wallCol = PkWall.EMPTY;
        wallCol = PkWall.withTileAt(wallCol, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        wallCol = PkWall.withTileAt(wallCol, TileDestination.Pattern.PATTERN_2, TileKind.Colored.E);
        wallCol = PkWall.withTileAt(wallCol, TileDestination.Pattern.PATTERN_3, TileKind.Colored.D);
        wallCol = PkWall.withTileAt(wallCol, TileDestination.Pattern.PATTERN_4, TileKind.Colored.C);
        wallCol = PkWall.withTileAt(wallCol, TileDestination.Pattern.PATTERN_5, TileKind.Colored.B);
        assertTrue(PkWall.isColumnFull(wallCol, 0));
        assertFalse(PkWall.isColumnFull(wallCol, 1));
    }

    @Test
    void pkWallToStringWorksOnKnownCases() {
        assertEquals("[abcde, eabcd, deabc, cdeab, bcdea]", PkWall.toString(PkWall.EMPTY));

        // Mur de l'exemple Figure 4
        int wall = PkWall.EMPTY;
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.C);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.D);

        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.A);

        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.E);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.B);

        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_4, TileKind.Colored.A);

        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.B);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.C);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.D);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.E);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.A);

        assertEquals("[AbCDe, eAbcd, dEaBc, cdeAb, BCDEA]", PkWall.toString(wall));
    }
}
