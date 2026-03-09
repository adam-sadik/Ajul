package ch.epfl.ajul.gamestate.packed;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyPkIntSet32Test {
    @Test
    void emptySetContainsNothing() {
        for (int i = 0; i < 32; i++) {
            assertFalse(PkIntSet32.contains(PkIntSet32.EMPTY, i));
        }
    }

    @Test
    void addAndContainsWorkForAllBits() {
        int set = PkIntSet32.EMPTY;
        for (int i = 0; i < 32; i++) {
            set = PkIntSet32.add(set, i);
            assertTrue(PkIntSet32.contains(set, i));
        }
        assertEquals(0xFFFFFFFF, set);
    }

    @Test
    void removeWorksCorrectly() {
        int set = 0xFFFFFFFF;
        for (int i = 0; i < 32; i++) {
            set = PkIntSet32.remove(set, i);
            assertFalse(PkIntSet32.contains(set, i));
        }
        assertEquals(PkIntSet32.EMPTY, set);
    }

    @Test
    void containsAllWorksForSubsets() {
        int setA = PkIntSet32.add(PkIntSet32.add(PkIntSet32.EMPTY, 5), 10);
        int setB = PkIntSet32.add(PkIntSet32.EMPTY, 5);

        assertTrue(PkIntSet32.containsAll(setA, setB));
        assertFalse(PkIntSet32.containsAll(setB, setA));
        assertTrue(PkIntSet32.containsAll(setA, PkIntSet32.EMPTY));
    }

    @Test
    void emptySet_hasZeroValue() {
        assertEquals(0, PkIntSet32.EMPTY);
    }

    @Test
    void contains_onEmptySet_returnsFalseForExtremes() {
        assertFalse(PkIntSet32.contains(PkIntSet32.EMPTY, 0));
        assertFalse(PkIntSet32.contains(PkIntSet32.EMPTY, 31));
    }

    @Test
    void add_firstBit_works() {
        int set = PkIntSet32.add(PkIntSet32.EMPTY, 0);
        assertTrue(PkIntSet32.contains(set, 0));
        assertEquals(1, set); // 2^0
    }

    @Test
    void add_lastBit_worksAndHandlesSignBitCorrectly() {
        int set = PkIntSet32.add(PkIntSet32.EMPTY, 31);
        assertTrue(PkIntSet32.contains(set, 31));
        assertEquals(1 << 31, set); // Vérifie que le bit de signe ne pose pas de problème
    }

    @Test
    void add_existingBit_doesNotCorruptSet() {
        int set = PkIntSet32.add(PkIntSet32.EMPTY, 15);
        int setDouble = PkIntSet32.add(set, 15);
        assertEquals(set, setDouble);
    }

    @Test
    void remove_existingBit_works() {
        int set = PkIntSet32.add(PkIntSet32.EMPTY, 10);
        set = PkIntSet32.remove(set, 10);
        assertFalse(PkIntSet32.contains(set, 10));
        assertEquals(PkIntSet32.EMPTY, set);
    }

    @Test
    void remove_nonExistingBit_doesNothing() {
        int set = PkIntSet32.add(PkIntSet32.EMPTY, 5);
        int modifiedSet = PkIntSet32.remove(set, 8);
        assertEquals(set, modifiedSet);
    }

    @Test
    void containsAll_withSelf_isTrue() {
        int set = PkIntSet32.add(PkIntSet32.add(PkIntSet32.EMPTY, 1), 2);
        assertTrue(PkIntSet32.containsAll(set, set));
    }

    @Test
    void containsAll_emptySetInNonEmpty_isTrue() {
        int set = PkIntSet32.add(PkIntSet32.EMPTY, 10);
        assertTrue(PkIntSet32.containsAll(set, PkIntSet32.EMPTY));
    }

    @Test
    void containsAll_nonEmptyInEmpty_isFalse() {
        int set = PkIntSet32.add(PkIntSet32.EMPTY, 10);
        assertFalse(PkIntSet32.containsAll(PkIntSet32.EMPTY, set));
    }
}
