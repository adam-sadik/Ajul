package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileKind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

class MyPkTileSetTest {

    @Test
    void emptySetIsCorrectlyDefined() {
        Assertions.assertEquals(0, PkTileSet.EMPTY);
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
    void adddAndRemoveWorkCorrectly() {
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
    void subsettOfExtractsCorrectly() {
        int subset = PkTileSet.subsetOf(PkTileSet.FULL, TileKind.E);
        assertEquals(20, PkTileSet.size(subset));
        assertEquals(20, PkTileSet.countOf(subset, TileKind.E));
        assertEquals(0, PkTileSet.countOf(subset, TileKind.A));
    }

    @Test
    void unionAnddDifferenceWorkCorrectly() {
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

        int nextIndex = PkTileSet.sampleColoredInto(set, destination, 2, rng);

        assertEquals(12, nextIndex);
        assertEquals(TileKind.Colored.E, destination[0]);
        assertEquals(TileKind.Colored.E, destination[1]);
        assertEquals(TileKind.Colored.A, destination[2]);
        assertEquals(TileKind.Colored.A, destination[3]);
        assertEquals(TileKind.Colored.A, destination[4]);
    }

    private static int sumCounts(int pk) {
        int s = 0;
        for (var k : TileKind.ALL) s += PkTileSet.countOf(pk, k);
        return s;
    }

    private static int countInRange(TileKind.Colored c, TileKind.Colored[] a, int from, int to) {
        int n = 0;
        for (int i = from; i < to; i++) if (a[i] == c) n++;
        return n;
    }

    @Test
    void emptyIsCorrect() {
        assertTrue(PkTileSet.isEmpty(PkTileSet.EMPTY));
        assertEquals(0, PkTileSet.size(PkTileSet.EMPTY));
        for (var k : TileKind.ALL) assertEquals(0, PkTileSet.countOf(PkTileSet.EMPTY, k));
        assertEquals("{}", PkTileSet.toString(PkTileSet.EMPTY));
    }

    @Test
    void fullAndFullColoredHaveCorrectSizes() {
        assertEquals(101, PkTileSet.size(PkTileSet.FULL));          // 5*20 + 1
        assertEquals(100, PkTileSet.size(PkTileSet.FULL_COLORED));  // 5*20
    }

    @Test
    void fullStringsMatchSpecExactly() {
        assertEquals("{20*A,20*B,20*C,20*D,20*E,1*FIRST_PLAYER_MARKER}", PkTileSet.toString(PkTileSet.FULL));
        assertEquals("{20*A,20*B,20*C,20*D,20*E}", PkTileSet.toString(PkTileSet.FULL_COLORED));
    }

    @Test
    void ofCreatesSingletonSet_examples() {
        assertEquals(0, PkTileSet.size(PkTileSet.of(0, TileKind.A)));
        assertEquals("{1*A}", PkTileSet.toString(PkTileSet.of(1, TileKind.A)));
        assertEquals("{20*E}", PkTileSet.toString(PkTileSet.of(20, TileKind.E)));
        assertEquals("{1*FIRST_PLAYER_MARKER}", PkTileSet.toString(PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER)));
    }

    @Test
    void countOfMatchesOf_forAllKindsAndSomeCounts() {
        for (var k : TileKind.ALL) {
            int max = k.tilesCount();
            int[] counts = (max == 1) ? new int[]{0, 1} : new int[]{0, 1, 7, 20};
            for (int c : counts) {
                if (c > max) continue;
                int pk = PkTileSet.of(c, k);
                assertEquals(c, PkTileSet.countOf(pk, k));
                assertEquals(c, PkTileSet.size(pk));
            }
        }
    }

    @Test
    void subsetOfExtractsExactlyThatKind() {
        int pk = 0;
        pk = PkTileSet.union(pk, PkTileSet.of(3, TileKind.A));
        pk = PkTileSet.union(pk, PkTileSet.of(5, TileKind.D));
        pk = PkTileSet.union(pk, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));

        assertEquals("{3*A}", PkTileSet.toString(PkTileSet.subsetOf(pk, TileKind.A)));
        assertEquals("{5*D}", PkTileSet.toString(PkTileSet.subsetOf(pk, TileKind.D)));
        assertEquals("{1*FIRST_PLAYER_MARKER}", PkTileSet.toString(PkTileSet.subsetOf(pk, TileKind.FIRST_PLAYER_MARKER)));
        assertEquals("{}", PkTileSet.toString(PkTileSet.subsetOf(pk, TileKind.B)));
    }

    @Test
    void unionAddsCounts_componentWise_examples() {
        int x = 0;
        x = PkTileSet.union(x, PkTileSet.of(2, TileKind.A));
        x = PkTileSet.union(x, PkTileSet.of(4, TileKind.D));

        int y = 0;
        y = PkTileSet.union(y, PkTileSet.of(7, TileKind.A));
        y = PkTileSet.union(y, PkTileSet.of(1, TileKind.B));
        y = PkTileSet.union(y, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));

        int u = PkTileSet.union(x, y);

        assertEquals(9, PkTileSet.countOf(u, TileKind.A));
        assertEquals(1, PkTileSet.countOf(u, TileKind.B));
        assertEquals(4, PkTileSet.countOf(u, TileKind.D));
        assertEquals(1, PkTileSet.countOf(u, TileKind.FIRST_PLAYER_MARKER));
        assertEquals(15, PkTileSet.size(u));
    }

    @Test
    void differenceSubtractsCounts_whenSecondIsSubset_examples() {
        int base = 0;
        base = PkTileSet.union(base, PkTileSet.of(10, TileKind.A));
        base = PkTileSet.union(base, PkTileSet.of(5, TileKind.B));
        base = PkTileSet.union(base, PkTileSet.of(2, TileKind.E));
        base = PkTileSet.union(base, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));

        int sub = 0;
        sub = PkTileSet.union(sub, PkTileSet.of(3, TileKind.A));
        sub = PkTileSet.union(sub, PkTileSet.of(5, TileKind.B));
        sub = PkTileSet.union(sub, PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER));

        int diff = PkTileSet.difference(base, sub);

        assertEquals("{7*A,2*E}", PkTileSet.toString(diff));
        assertEquals(9, PkTileSet.size(diff));
    }

    @Test
    void sizeEqualsSumOfCounts_examples() {
        int[] examples = new int[]{
                PkTileSet.EMPTY,
                PkTileSet.FULL_COLORED,
                PkTileSet.union(PkTileSet.of(7, TileKind.B), PkTileSet.of(9, TileKind.D)),
                PkTileSet.union(PkTileSet.of(1, TileKind.A), PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER))
        };
        for (int pk : examples) {
            assertEquals(sumCounts(pk), PkTileSet.size(pk));
        }
    }

    @Test
    void copyColoredIntoWritesColorsInOrder_examples() {
        int pk = 0;
        pk = PkTileSet.union(pk, PkTileSet.of(1, TileKind.A));
        pk = PkTileSet.union(pk, PkTileSet.of(2, TileKind.B));
        pk = PkTileSet.union(pk, PkTileSet.of(3, TileKind.D));
        pk = PkTileSet.union(pk, PkTileSet.of(1, TileKind.E));

        TileKind.Colored[] dst = new TileKind.Colored[20];
        Arrays.fill(dst, null);

        int end = PkTileSet.copyColoredInto(pk, dst);
        assertEquals(7, end);

        TileKind.Colored[] expectedPrefix = {
                TileKind.Colored.A,
                TileKind.Colored.B, TileKind.Colored.B,
                TileKind.Colored.D, TileKind.Colored.D, TileKind.Colored.D,
                TileKind.Colored.E
        };
        assertArrayEquals(expectedPrefix, Arrays.copyOf(dst, expectedPrefix.length));
    }

    @Test
    void sampleColoredIntoProducesExactlySameMultiset_andReturnsOffsetPlusSize() {
        int pk = 0;
        pk = PkTileSet.union(pk, PkTileSet.of(4, TileKind.A));
        pk = PkTileSet.union(pk, PkTileSet.of(3, TileKind.C));
        pk = PkTileSet.union(pk, PkTileSet.of(2, TileKind.E));

        RandomGenerator rng = RandomGeneratorFactory.getDefault().create(2026);

        TileKind.Colored[] dst = new TileKind.Colored[50];
        Arrays.fill(dst, null);

        int offset = 7;
        int end = PkTileSet.sampleColoredInto(pk, dst, offset, rng);

        assertEquals(offset + PkTileSet.size(pk), end);

        assertEquals(4, countInRange(TileKind.Colored.A, dst, offset, end));
        assertEquals(3, countInRange(TileKind.Colored.C, dst, offset, end));
        assertEquals(2, countInRange(TileKind.Colored.E, dst, offset, end));
        assertEquals(0, countInRange(TileKind.Colored.B, dst, offset, end));
        assertEquals(0, countInRange(TileKind.Colored.D, dst, offset, end));
    }

    @Test
    void addAnddRemoveWorkCorrectly() {
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
}
