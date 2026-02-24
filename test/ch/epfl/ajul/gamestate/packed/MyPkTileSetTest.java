package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileKind;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

class MyPkTileSetTest {

    @Test
    void emptySetIsCorrectlyDefined() {
        assertEquals(0, PkTileSet.EMPTY);
        assertTrue(PkTileSet.isEmpty(PkTileSet.EMPTY));
        assertEquals(0, PkTileSet.size(PkTileSet.EMPTY));
    }

    @Test
    void fullSetsAreCorrectlyDefined() {
        assertFalse(PkTileSet.isEmpty(PkTileSet.FULL));
        assertEquals(101, PkTileSet.size(PkTileSet.FULL));
        assertEquals(20, PkTileSet.countOf(PkTileSet.FULL, TileKind.A));
        assertEquals(1, PkTileSet.countOf(PkTileSet.FULL, TileKind.FIRST_PLAYER_MARKER));

        assertEquals(100, PkTileSet.size(PkTileSet.FULL_COLORED));
        assertEquals(0, PkTileSet.countOf(PkTileSet.FULL_COLORED, TileKind.FIRST_PLAYER_MARKER));
    }

    @Test
    void ofCreatesSetWithCorrectCount() {
        int setA = PkTileSet.of(15, TileKind.A);
        assertEquals(15, PkTileSet.countOf(setA, TileKind.A));
        assertEquals(0, PkTileSet.countOf(setA, TileKind.B));

        int setMarker = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        assertEquals(1, PkTileSet.countOf(setMarker, TileKind.FIRST_PLAYER_MARKER));
    }

    @Test
    void addAndRemoveWorkCorrectly() {
        int set = PkTileSet.EMPTY;

        set = PkTileSet.add(set, TileKind.C);
        set = PkTileSet.add(set, TileKind.C);
        set = PkTileSet.add(set, TileKind.FIRST_PLAYER_MARKER);
        assertEquals(2, PkTileSet.countOf(set, TileKind.C));
        assertEquals(1, PkTileSet.countOf(set, TileKind.FIRST_PLAYER_MARKER));
        assertEquals(3, PkTileSet.size(set));

        set = PkTileSet.remove(set, TileKind.C);
        assertEquals(1, PkTileSet.countOf(set, TileKind.C));
        assertEquals(2, PkTileSet.size(set));
    }

    @Test
    void subsetOfExtractsCorrectly() {
        int subset = PkTileSet.subsetOf(PkTileSet.FULL, TileKind.E);
        assertEquals(20, PkTileSet.size(subset));
        assertEquals(20, PkTileSet.countOf(subset, TileKind.E));
        assertEquals(0, PkTileSet.countOf(subset, TileKind.A));
    }

    @Test
    void unionAndDifferenceWorkCorrectly() {
        int set1 = PkTileSet.of(5, TileKind.A);
        int set2 = PkTileSet.of(3, TileKind.A);
        int set3 = PkTileSet.of(2, TileKind.B);

        int unionSet = PkTileSet.union(set1, set3);
        assertEquals(5, PkTileSet.countOf(unionSet, TileKind.A));
        assertEquals(2, PkTileSet.countOf(unionSet, TileKind.B));

        int diffSet = PkTileSet.difference(set1, set2);
        assertEquals(2, PkTileSet.countOf(diffSet, TileKind.A));
    }

    @Test
    void toStringFormatsCorrectly() {
        assertEquals("{}", PkTileSet.toString(PkTileSet.EMPTY));

        int customSet = PkTileSet.union(PkTileSet.of(3, TileKind.A), PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));
        assertEquals("{3*A,1*FIRST_PLAYER_MARKER}", PkTileSet.toString(customSet));

        String fullString = PkTileSet.toString(PkTileSet.FULL);
        assertEquals("{20*A,20*B,20*C,20*D,20*E,1*FIRST_PLAYER_MARKER}", fullString);
    }

    @Test
    void copyColoredIntoFillsArrayCorrectly() {
        TileKind.Colored[] array = new TileKind.Colored[10];
        int set = PkTileSet.union(PkTileSet.of(3, TileKind.A), PkTileSet.of(2, TileKind.C));

        int nextIndex = PkTileSet.copyColoredInto(set, array);

        assertEquals(5, nextIndex);
        assertEquals(TileKind.Colored.A, array[0]);
        assertEquals(TileKind.Colored.A, array[2]);
        assertEquals(TileKind.Colored.C, array[3]);
        assertEquals(TileKind.Colored.C, array[4]);
        assertNull(array[5]);
    }

    @Test
    void sampleColoredIntoWorksWithinBounds() {
        TileKind.Colored[] destination = new TileKind.Colored[4];
        RandomGenerator rng = RandomGeneratorFactory.getDefault().create(2026);

        // L'ensemble a 100 tuiles. On s'attend à ce que ça retourne 100 (l'offset étant 0)
        int nextIndex = PkTileSet.sampleColoredInto(PkTileSet.FULL_COLORED, destination, 0, rng);

        assertEquals(100, nextIndex);
        for (TileKind.Colored c : destination) {
            assertNotNull(c);
        }
    }

    @Test
    void sampleColoredIntoWorksWithEmptySet() {
        TileKind.Colored[] destination = new TileKind.Colored[4];
        RandomGenerator rng = RandomGeneratorFactory.getDefault().create(2026);

        int nextIndex = PkTileSet.sampleColoredInto(PkTileSet.EMPTY, destination, 0, rng);

        assertEquals(0, nextIndex);
        for (TileKind.Colored c : destination) {
            assertNull(c);
        }
    }

    @Test
    void copyColoredIntoWorksWithEmptySet() {
        TileKind.Colored[] array = new TileKind.Colored[10];
        int nextIndex = PkTileSet.copyColoredInto(PkTileSet.EMPTY, array);

        assertEquals(0, nextIndex);
        for (TileKind.Colored c : array) {
            assertNull(c);
        }
    }

    // ----------------------------------------------------
    // Les tests sur les assertions (débordements)
    // On s'attend explicitement à une AssertionError
    // ----------------------------------------------------

    @Test
    void addBeyondMaxCount() {
        int set = PkTileSet.of(20, TileKind.B);
        assertThrows(AssertionError.class, () -> {
            PkTileSet.add(set, TileKind.B);
        });
    }

    @Test
    void removeBelowMinCount() {
        int set = PkTileSet.EMPTY;
        assertThrows(AssertionError.class, () -> {
            PkTileSet.remove(set, TileKind.D);
        });
    }

    @Test
    void differenceWithNonSubset() {
        int set1 = PkTileSet.of(2, TileKind.A);
        int set2 = PkTileSet.of(3, TileKind.A);
        assertThrows(AssertionError.class, () -> {
            PkTileSet.difference(set1, set2);
        });
    }

    @Test
    void unionWithOverflow() {
        int set1 = PkTileSet.of(20, TileKind.A);
        int set2 = PkTileSet.of(1, TileKind.A);
        assertThrows(AssertionError.class, () -> {
            PkTileSet.union(set1, set2);
        });
    }

    // ----------------------------------------------------

    @Test
    void copyColoredIntoWithNonZeroOffset() {
        TileKind.Colored[] array = new TileKind.Colored[5];
        int set = PkTileSet.of(2, TileKind.D);

        array[0] = TileKind.Colored.A;
        int nextIndex = PkTileSet.copyColoredInto(set, array);

        assertEquals(2, nextIndex);
        assertEquals(TileKind.Colored.D, array[0]);
        assertEquals(TileKind.Colored.D, array[1]);
    }

    @Test
    void sampleColoredIntoWorksWithMoreSpaceThanTiles() {
        TileKind.Colored[] destination = new TileKind.Colored[5];
        int set = PkTileSet.union(PkTileSet.of(2, TileKind.A), PkTileSet.of(1, TileKind.C));
        RandomGenerator rng = RandomGeneratorFactory.getDefault().create(2026);

        // On a 3 tuiles et un offset de 0. Résultat attendu : 3
        int nextIndex = PkTileSet.sampleColoredInto(set, destination, 0, rng);

        assertEquals(3, nextIndex);
        assertEquals(TileKind.Colored.A, destination[0]);
        assertEquals(TileKind.Colored.A, destination[1]);
        assertEquals(TileKind.Colored.C, destination[2]);
        assertNull(destination[3]);
        assertNull(destination[4]);
    }

    @Test
    void sampleColoredIntoWorksWithLessSpaceThanTiles() {
        TileKind.Colored[] destination = new TileKind.Colored[3];
        int set = PkTileSet.FULL_COLORED; // 100 tuiles
        RandomGenerator rng = RandomGeneratorFactory.getDefault().create(2026);

        // L'ensemble a 100 tuiles. On s'attend à ce que ça retourne 100 (l'offset étant 0)
        int nextIndex = PkTileSet.sampleColoredInto(set, destination, 0, rng);

        assertEquals(100, nextIndex);
        for (int i = 0; i < 3; i++) {
            assertNotNull(destination[i]);
        }
    }

    @Test
    void sampleColoredIntoWorksWithOffset() {
        TileKind.Colored[] destination = new TileKind.Colored[5];
        Arrays.fill(destination, TileKind.Colored.E);

        int set = PkTileSet.of(10, TileKind.A);
        RandomGenerator rng = RandomGeneratorFactory.getDefault().create(2026);

        // 10 tuiles avec un offset de 2. Résultat attendu : 12.
        int nextIndex = PkTileSet.sampleColoredInto(set, destination, 2, rng);

        assertEquals(12, nextIndex);
        assertEquals(TileKind.Colored.E, destination[0]);
        assertEquals(TileKind.Colored.E, destination[1]);
        assertEquals(TileKind.Colored.A, destination[2]);
        assertEquals(TileKind.Colored.A, destination[3]);
        assertEquals(TileKind.Colored.A, destination[4]);
    }
}