package ch.epfl.ajul.gamestate.packed;
import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

public class MyPkWallTest {
    @Test
    void emptyWallIsEmpty() {
        Assertions.assertEquals(0, PkWall.EMPTY);
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
    void indexOfWorksOnDiagonal() {
        // La couleur A (index 0) doit être sur la grande diagonale (cases 0, 6, 12, 18, 24)
        assertEquals(0, PkWall.indexOf(TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));
        assertEquals(6, PkWall.indexOf(TileDestination.Pattern.PATTERN_2, TileKind.Colored.A));
        assertEquals(12, PkWall.indexOf(TileDestination.Pattern.PATTERN_3, TileKind.Colored.A));
        assertEquals(18, PkWall.indexOf(TileDestination.Pattern.PATTERN_4, TileKind.Colored.A));
        assertEquals(24, PkWall.indexOf(TileDestination.Pattern.PATTERN_5, TileKind.Colored.A));
    }

    @Test
    void colorAtWorksSymmetrically() {
        for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
            for (TileKind.Colored color : TileKind.Colored.ALL) {
                // Si on demande la colonne d'une couleur sur une ligne,
                // puis qu'on demande la couleur de cette colonne sur cette même ligne,
                // on doit retrouver la couleur de départ.
                int col = PkWall.column(line, color);
                assertEquals(color, PkWall.colorAt(line, col));
            }
        }
    }

    @Test
    void isColorFullWorksCorrectly() {
        int emptyWall = PkWall.EMPTY;
        int fullWall = 0b11111_11111_11111_11111_11111;

        assertFalse(PkWall.isColorFull(emptyWall, TileKind.Colored.C));
        assertTrue(PkWall.isColorFull(fullWall, TileKind.Colored.C));

        // Mur ne contenant QUE la couleur E (index 4) sur toutes les lignes
        // E_MASK : 0b01000_00100_00010_00001_10000
        int wallOnlyE = 0b01000_00100_00010_00001_10000;
        assertTrue(PkWall.isColorFull(wallOnlyE, TileKind.Colored.E));
        assertFalse(PkWall.isColorFull(wallOnlyE, TileKind.Colored.A)); // A n'est pas pleine
    }

    @Test
    void groupSizeWorksOnIsolatedTile() {
        // Une seule tuile A sur la première ligne
        int wall = PkWall.withTileAt(PkWall.EMPTY, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);

        assertEquals(1, PkWall.hGroupSize(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));
        assertEquals(1, PkWall.vGroupSize(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));

    }

    @Test
    void groupSizeCalculatesContinuousGroupsOnly() {
        // Ligne 1 : Tuiles aux colonnes 0, 1 et 3, 4 (Trou à la colonne 2)
        // Les couleurs correspondantes sur PATTERN_1 : A(0), B(1), C(2), D(3), E(4)
        int wall = PkWall.EMPTY;
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.B);
        // On ne met pas C (le trou)
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.D);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.E);

        // Si on interroge A (col 0), le groupe compte A et B = 2
        assertEquals(2, PkWall.hGroupSize(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));

        // Si on interroge B (col 1), le groupe compte B et A = 2
        assertEquals(2, PkWall.hGroupSize(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.B));

        // Si on interroge D (col 3), le groupe compte D et E = 2
        assertEquals(2, PkWall.hGroupSize(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.D));
    }

    @Test
    void asPkTileSetProducesCorrectCounts() {
        // On construit le mur de l'exemple du projet (Figure 3)
        // Ligne 1 : D (case 3), E (case 4)
        // Ligne 2 : C (case 7), D (case 8)
        // Ligne 3 : A (case 12)
        // Les autres sont vides.
        int wall = PkWall.EMPTY;
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.D);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.E);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.C);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.D);
        wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.A);

        int generatedSet = PkWall.asPkTileSet(wall);

        Assertions.assertEquals(1, PkTileSet.countOf(generatedSet, TileKind.Colored.A));
        assertEquals(0, PkTileSet.countOf(generatedSet, TileKind.Colored.B));
        assertEquals(1, PkTileSet.countOf(generatedSet, TileKind.Colored.C));
        assertEquals(2, PkTileSet.countOf(generatedSet, TileKind.Colored.D));
        assertEquals(1, PkTileSet.countOf(generatedSet, TileKind.Colored.E));
    }

    // --- TESTS DES CONSTANTES ---

    @Test
    void pkWallConstantsAreCorrectlyDefined() {
        assertEquals(0, PkWall.EMPTY);
        assertEquals(5, PkWall.WALL_WIDTH);
        assertEquals(5, PkWall.WALL_HEIGHT);
    }

    // --- TESTS DE COORDONNÉES (indexOf, column, colorAt) ---

    @Test
    void pkWallIndexOfWorksExhaustively() {
        var remaining = 5 * 5;
        for (var line : TileDestination.Pattern.ALL) {
            for (var color : TileKind.Colored.ALL) {
                var index = PkWall.indexOf(line, color);
                // L'index doit strictement être compris entre 0 et 24 inclus
                assertTrue(index >= 0 && index <= 24);
                remaining -= 1;
            }
        }
        assertEquals(0, remaining);
    }

    @Test
    void pkWallColumnWorksExhaustively() {
        var remaining = 5 * 5;
        for (var line : TileDestination.Pattern.ALL) {
            for (var color : TileKind.Colored.ALL) {
                var col = PkWall.column(line, color);
                assertTrue(col >= 0 && col < 5);
                // Vérification croisée : la colonne doit correspondre au modulo 5 de l'index
                assertEquals(col, PkWall.indexOf(line, color) % 5);
                remaining -= 1;
            }
        }
        assertEquals(0, remaining);
    }

    @Test
    void pkWallColorAtWorksSymmetrically() {
        var remaining = 5 * 5;
        for (var line : TileDestination.Pattern.ALL) {
            for (int col = 0; col < 5; col++) {
                var color = PkWall.colorAt(line, col);
                // Si on demande la couleur à une colonne, la méthode column() doit renvoyer cette même colonne
                assertEquals(col, PkWall.column(line, color));
                remaining -= 1;
            }
        }
        assertEquals(0, remaining);
    }

    // --- TESTS D'AJOUT ET DE PRÉSENCE (withTileAt, hasTileAt) ---

    @Test
    void pkWallWithTileAtAndHasTileAtWorkExhaustively() {
        var pkWall = PkWall.EMPTY;
        var remaining = 5 * 5;

        for (var line : TileDestination.Pattern.ALL) {
            for (var color : TileKind.Colored.ALL) {
                assertFalse(PkWall.hasTileAt(pkWall, line, color));
                pkWall = PkWall.withTileAt(pkWall, line, color);
                assertTrue(PkWall.hasTileAt(pkWall, line, color));
                remaining -= 1;
            }
        }
        assertEquals(0, remaining);

        // Si on a ajouté les 25 tuiles, le mur doit être plein (les 25 premiers bits à 1)
        var fullWall = (1 << 25) - 1;
        assertEquals(fullWall, pkWall);
    }

    @Test
    void pkWallWithTileAtIdempotent() {
        // Ajouter une tuile déjà présente ne doit pas modifier le mur
        var pkWall = PkWall.withTileAt(PkWall.EMPTY, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        var expected = pkWall;
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        assertEquals(expected, pkWall);
    }

    // --- TESTS DES GROUPES (hGroupSize, vGroupSize) ---

    @Test
    void pkWallGroupSizesTrivialIsolatedTile() {
        // Une seule tuile au centre exact du mur (Ligne 3, Couleur A = Colonne 2)
        var pkWall = PkWall.withTileAt(PkWall.EMPTY, TileDestination.Pattern.PATTERN_3, TileKind.Colored.A);

        assertEquals(1, PkWall.hGroupSize(pkWall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.A));
        assertEquals(1, PkWall.vGroupSize(pkWall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.A));
    }

    @Test
    void pkWallHGroupSizeWithHoles() {
        var pkWall = PkWall.EMPTY;
        // On remplit la ligne 1 sauf la colonne 2
        // Col 0(A), Col 1(B), Col 3(D), Col 4(E)
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.B);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.D);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.E);

        // A (col 0) voit B (col 1). Taille = 2. Le trou en C l'arrête.
        assertEquals(2, PkWall.hGroupSize(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));
        // E (col 4) voit D (col 3). Taille = 2. Le trou en C l'arrête.
        assertEquals(2, PkWall.hGroupSize(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.E));
    }

    @Test
    void pkWallVGroupSizeFullColumn() {
        var pkWall = PkWall.EMPTY;
        // On remplit toute la colonne 0 (les tuiles A, E, D, C, B sur leurs lignes respectives)
        for (var line : TileDestination.Pattern.ALL) {
            var color = PkWall.colorAt(line, 0);
            pkWall = PkWall.withTileAt(pkWall, line, color);
        }

        for (var line : TileDestination.Pattern.ALL) {
            var color = PkWall.colorAt(line, 0);
            assertEquals(5, PkWall.vGroupSize(pkWall, line, color));
            // HGroupSize doit rester 1 pour toutes ces tuiles car elles n'ont pas de voisines horizontales
            assertEquals(1, PkWall.hGroupSize(pkWall, line, color));
        }
    }

    @Test
    void pkWallGroupSizesOnEdges() {
        // Test aux coins du plateau (cas limites pour les boucles qui cherchent les voisins)
        var pkWall = PkWall.EMPTY;

        // Coin supérieur gauche (Ligne 1, Col 0 -> Couleur A)
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        assertEquals(1, PkWall.hGroupSize(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));
        assertEquals(1, PkWall.vGroupSize(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A));

        // Coin inférieur droit (Ligne 5, Col 4 -> Couleur A)
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.A);
        assertEquals(1, PkWall.hGroupSize(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.A));
        assertEquals(1, PkWall.vGroupSize(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.A));
    }

    // --- TESTS DES PLÉNITUDES (isRowFull, isColumnFull, isColorFull, hasFullRow) ---

    @Test
    void pkWallPlenitudeMethodsOnEmptyAndFull() {
        var empty = PkWall.EMPTY;
        var full = (1 << 25) - 1;

        assertFalse(PkWall.hasFullRow(empty));
        assertTrue(PkWall.hasFullRow(full));

        for (var line : TileDestination.Pattern.ALL) {
            assertFalse(PkWall.isRowFull(empty, line));
            assertTrue(PkWall.isRowFull(full, line));
        }

        for (int c = 0; c < 5; c++) {
            assertFalse(PkWall.isColumnFull(empty, c));
            assertTrue(PkWall.isColumnFull(full, c));
        }

        for (var color : TileKind.Colored.ALL) {
            assertFalse(PkWall.isColorFull(empty, color));
            assertTrue(PkWall.isColorFull(full, color));
        }
    }

    @Test
    void pkWallIsRowFullWorksPrecisely() {
        var pkWall = PkWall.EMPTY;

        // On remplit la ligne 2 avec 4 tuiles (il manque E)
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.A);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.B);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.C);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.D);

        assertFalse(PkWall.isRowFull(pkWall, TileDestination.Pattern.PATTERN_2));
        assertFalse(PkWall.hasFullRow(pkWall));

        // On ajoute la dernière tuile
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.E);
        assertTrue(PkWall.isRowFull(pkWall, TileDestination.Pattern.PATTERN_2));
        assertTrue(PkWall.hasFullRow(pkWall));

        // La ligne 1 doit toujours être considérée comme non pleine
        assertFalse(PkWall.isRowFull(pkWall, TileDestination.Pattern.PATTERN_1));
    }

    @Test
    void pkWallIsColorFullWorksPrecisely() {
        var pkWall = PkWall.EMPTY;

        // On ajoute la couleur B sur les lignes 1, 2, 3 et 4. Il manque la ligne 5.
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.B);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.B);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.B);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_4, TileKind.Colored.B);

        assertFalse(PkWall.isColorFull(pkWall, TileKind.Colored.B));

        // On ajoute la dernière tuile B sur la ligne 5
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.B);
        assertTrue(PkWall.isColorFull(pkWall, TileKind.Colored.B));

        // La couleur A doit toujours être considérée comme non pleine
        assertFalse(PkWall.isColorFull(pkWall, TileKind.Colored.A));
    }

    // --- TESTS DE CONVERSION (asPkTileSet) ---

    @Test
    void pkWallAsPkTileSetWorksRandomly() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (var i = 0; i < 2000; i += 1) {
            // On génère un mur totalement aléatoire sur les 25 premiers bits
            var pkWall = rng.nextInt(1 << 25);

            // Recalcul manuel laborieux pour vérifier le bitCount
            var expectedSet = PkTileSet.EMPTY;
            for (var line : TileDestination.Pattern.ALL) {
                for (var color : TileKind.Colored.ALL) {
                    if (PkWall.hasTileAt(pkWall, line, color)) {
                        expectedSet = PkTileSet.add(expectedSet, color);
                    }
                }
            }
            assertEquals(expectedSet, PkWall.asPkTileSet(pkWall));
        }
    }

    @Test
    void pkWallAsPkTileSetOnFullWall() {
        var fullWall = (1 << 25) - 1;

        // Le mur plein contient exactement 5 tuiles de chaque couleur.
        var expectedSet = PkTileSet.EMPTY;
        for (var color : TileKind.Colored.ALL) {
            expectedSet = PkTileSet.union(expectedSet, PkTileSet.of(5, color));
        }

        assertEquals(expectedSet, PkWall.asPkTileSet(fullWall));
        assertEquals(PkTileSet.EMPTY, PkWall.asPkTileSet(PkWall.EMPTY));
    }

    // --- TESTS DE STRING (toString) ---

    @Test
    void pkWallToStringWorksOnConstants() {
        // Validation stricte des constantes de l'énoncé
        assertEquals("[abcde, eabcd, deabc, cdeab, bcdea]", PkWall.toString(PkWall.EMPTY));
        assertEquals("[ABCDE, EABCD, DEABC, CDEAB, BCDEA]", PkWall.toString((1 << 25) - 1));
    }

    @Test
    void pkWallToStringWorksOnSpecificExample() {
        // Validation du mur de la Figure 4
        var expected = "[AbCDe, eAbcd, dEaBc, cdeAb, BCDEA]";

        var pkWall = PkWall.EMPTY;

        // Ligne 1 : A, C, D
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.A);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.C);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_1, TileKind.Colored.D);

        // Ligne 2 : A
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_2, TileKind.Colored.A);

        // Ligne 3 : E, B
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.E);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_3, TileKind.Colored.B);

        // Ligne 4 : A
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_4, TileKind.Colored.A);

        // Ligne 5 : TOUTES (B, C, D, E, A)
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.B);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.C);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.D);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.E);
        pkWall = PkWall.withTileAt(pkWall, TileDestination.Pattern.PATTERN_5, TileKind.Colored.A);

        assertEquals(expected, PkWall.toString(pkWall));
    }

}
