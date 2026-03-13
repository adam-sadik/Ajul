package ch.epfl.ajul.gamestate.packed;
import org.junit.jupiter.api.Test;

import java.util.random.RandomGeneratorFactory;

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
    @Test
    void pkIntSet32EmptyIsCorrectlyDefined() {
        assertEquals(0, PkIntSet32.EMPTY);
    }

    @Test
    void pkIntSet32AddAndContainsWork() {
        int set = PkIntSet32.EMPTY;
        for (int i = 0; i < 32; i++) {
            assertFalse(PkIntSet32.contains(set, i));
            set = PkIntSet32.add(set, i);
            assertTrue(PkIntSet32.contains(set, i));
        }
        // A la fin, tous les bits doivent être à 1 (soit -1 en complément à 2)
        assertEquals(-1, set);
    }

    @Test
    void pkIntSet32RemoveWorks() {
        int set = -1; // Tous les bits à 1
        for (int i = 0; i < 32; i++) {
            assertTrue(PkIntSet32.contains(set, i));
            set = PkIntSet32.remove(set, i);
            assertFalse(PkIntSet32.contains(set, i));
        }
        assertEquals(PkIntSet32.EMPTY, set);
    }

    @Test
    void pkIntSet32ContainsAllWorks() {
        int setA = 0b101010;
        int setB = 0b100010;
        int setC = 0b111010;

        assertTrue(PkIntSet32.containsAll(setA, setB));
        assertFalse(PkIntSet32.containsAll(setB, setA));
        assertTrue(PkIntSet32.containsAll(setC, setA));
        assertTrue(PkIntSet32.containsAll(setA, PkIntSet32.EMPTY));
    }

    @Test
    void RemoveTrivialEmptyTest(){
        int pkIntSet32 = 0b0100;
        int expected = PkIntSet32.EMPTY;
        int i=2;
        assertEquals(expected, PkIntSet32.remove(pkIntSet32, i));
    }

    @Test
    void RemoveTrivialIndexZeroTest(){
        int pkIntSet32 = 0b0111;
        int expected = 0b0110;
        int i=0;
        assertEquals(expected, PkIntSet32.remove(pkIntSet32, i));
    }

    @Test
    void RemoveTrivialTest(){
        int pkIntSet32 = 0b0111;
        int expected = 0b0101;
        int i=1;
        assertEquals(expected, PkIntSet32.remove(pkIntSet32, i));
    }

    @Test
    void AddTrivialTest(){
        int pkTileSet32 = 0b0011000;
        int expected = 0b0011010;
        int i=1;
        assertEquals(expected, PkIntSet32.add(pkTileSet32, i));
    }

    @Test
    void AddTrivialIndexZeroTest(){
        int pkTileSet32 = 0b0011000;
        int expected = 0b0011001;
        int i=0;
        assertEquals(expected, PkIntSet32.add(pkTileSet32, i));
    }

    @Test
    void containsTrivialAndExtremeTests() {
        int pkSet = 0b1010; // Contient 1 et 3

        assertTrue(PkIntSet32.contains(pkSet, 1));
        assertTrue(PkIntSet32.contains(pkSet, 3));
        assertFalse(PkIntSet32.contains(pkSet, 0));
        assertFalse(PkIntSet32.contains(pkSet, 2));
        assertFalse(PkIntSet32.contains(pkSet, 31));
    }

    @Test
    void containsAllTrivialTests() {
        int setA = 0b1110; // Contient 1, 2, 3
        int setB = 0b0110; // Contient 1, 2
        int setC = 0b0101; // Contient 0, 2

        assertTrue(PkIntSet32.containsAll(setA, setB)); // {1,2,3} contient {1,2}
        assertTrue(PkIntSet32.containsAll(setA, setA)); // Tout ensemble se contient lui-même
        assertTrue(PkIntSet32.containsAll(setA, PkIntSet32.EMPTY)); // Tout ensemble contient l'ensemble vide

        assertFalse(PkIntSet32.containsAll(setB, setA)); // {1,2} ne contient pas {1,2,3}
        assertFalse(PkIntSet32.containsAll(setA, setC)); // {1,2,3} ne contient pas {0,2}
    }

    @Test
    void bit31SignBitWorksProperly() {
        // Le bit 31 est le bit de signe en Java (représente les nombres négatifs).
        // Il est crucial de tester qu'il ne corrompt pas la logique.
        int pkSet = PkIntSet32.EMPTY;
        pkSet = PkIntSet32.add(pkSet, 31);

        assertTrue(PkIntSet32.contains(pkSet, 31));
        assertFalse(PkIntSet32.contains(pkSet, 30));

        pkSet = PkIntSet32.remove(pkSet, 31);
        assertEquals(PkIntSet32.EMPTY, pkSet);
    }

    @Test
    void exhaustiveAddAndContainsTest() {
        // On teste l'ajout de CHAQUE index possible un par un.
        int pkSet = PkIntSet32.EMPTY;
        for (int i = 0; i < Integer.SIZE; i++) {
            pkSet = PkIntSet32.add(pkSet, i);
            assertTrue(PkIntSet32.contains(pkSet, i));

            // On s'assure que le bit suivant n'a pas été allumé par erreur
            if (i < Integer.SIZE - 1) {
                assertFalse(PkIntSet32.contains(pkSet, i + 1));
            }
        }

        // Si on a ajouté les 32 bits, le set doit être plein (soit -1 en entier signé Java : 0xFFFFFFFF)
        assertEquals(-1, pkSet);
    }

    @Test
    void randomContainsAllTest() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 5000; i++) {
            int subSet = rng.nextInt(); // N'importe quel int de 32 bits
            int diffSet = rng.nextInt();

            // L'union d'un sous-ensemble et d'un autre donne un super-ensemble
            int superSet = subSet | diffSet;

            assertTrue(PkIntSet32.containsAll(superSet, subSet));
        }
    }



    @Test
    void pkIntSet32EmptyyIsCorrectlyDefined() {
        assertEquals(0, PkIntSet32.EMPTY);
    }

    @Test
    void pkIntSet32ContainsWorksExhaustively() {
        // 32 bits à tester sur 32 ensembles différents créés manuellement
        var remaining = 32 * 32;

        for (var i = 0; i < Integer.SIZE; i += 1) {
            // Création d'un ensemble contenant UNIQUEMENT l'élément i
            var pkSet = 1 << i;

            for (var j = 0; j < Integer.SIZE; j += 1) {
                // L'élément j est dans l'ensemble si et seulement si i == j
                var expected = (i == j);
                assertEquals(expected, PkIntSet32.contains(pkSet, j));
                remaining -= 1;
            }
        }
        assertEquals(0, remaining);
    }

    @Test
    void pkIntSet32AddWorksExhaustively() {
        var pkSet = PkIntSet32.EMPTY;

        for (var i = 0; i < Integer.SIZE; i += 1) {
            // On vérifie qu'il n'y est pas avant de l'ajouter
            assertFalse(PkIntSet32.contains(pkSet, i));

            pkSet = PkIntSet32.add(pkSet, i);

            // On vérifie qu'il y est après l'ajout
            assertTrue(PkIntSet32.contains(pkSet, i));
        }

        // Un entier signé de 32 bits avec tous ses bits à 1 vaut exactement -1
        assertEquals(-1, pkSet);
    }

    @Test
    void pkIntSet32RemoveWorksExhaustively() {
        // On commence avec un ensemble plein (tous les bits à 1)
        var pkSet = -1;

        for (var i = 0; i < Integer.SIZE; i += 1) {
            // On vérifie qu'il y est avant de le retirer
            assertTrue(PkIntSet32.contains(pkSet, i));

            pkSet = PkIntSet32.remove(pkSet, i);

            // On vérifie qu'il n'y est plus après le retrait
            assertFalse(PkIntSet32.contains(pkSet, i));
        }

        // Après avoir retiré les 32 éléments, l'ensemble doit être strictement vide
        assertEquals(PkIntSet32.EMPTY, pkSet);
    }

    @Test
    void pkIntSet32ContainsAllWorksRandomlyAndExhaustively() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);

        for (var i = 0; i < 5000; i += 1) {
            var setA = rng.nextInt(); // Un ensemble aléatoire
            var setB = rng.nextInt(); // Un autre ensemble aléatoire

            // On va recoder manuellement et naïvement la logique "un élément i appartient ssi..."
            // pour valider que notre méthode optimisée (A & B) == B est infaillible.
            var expectedContainsAll = true;

            for (var bit = 0; bit < Integer.SIZE; bit += 1) {
                // Extraction du bit i en le poussant tout à droite et en le masquant
                var inA = ((setA >>> bit) & 1) == 1;
                var inB = ((setB >>> bit) & 1) == 1;

                // Si un élément est dans B mais pas dans A, alors A ne contient pas tout B
                if (inB && !inA) {
                    expectedContainsAll = false;
                    break;
                }
            }

            // On confronte le calcul manuel laborieux avec notre implémentation ultra-rapide
            assertEquals(expectedContainsAll, PkIntSet32.containsAll(setA, setB));

            // Un ensemble créé en faisant l'union de A et B (A | B) contient forcément B.
            // On teste ce cas de figure qui doit toujours retourner true.
            var superSet = setA | setB;
            assertTrue(PkIntSet32.containsAll(superSet, setB));
        }
    }
}
