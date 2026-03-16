package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import org.junit.jupiter.api.Test;

import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

public class MyPkPlayerStates {
    @Test
    void gettersWorkCorrectlyForMultiplePlayers() {
        int numberOfPlayers = PlayerId.values().length;
        int[] rawArray = new int[4 * numberOfPlayers];

        // Remplissage avec des valeurs uniques pour s'assurer que les index ne se croisent pas
        for (int i = 0; i < rawArray.length; i++) {
            rawArray[i] = (i + 1) * 10;
        }

        ImmutableIntArray readOnlyArray = ImmutableIntArray.copyOf(rawArray);

        for (PlayerId player : PlayerId.values()) {
            int baseIndex = 4 * player.ordinal();
            assertEquals((baseIndex + 1) * 10, PkPlayerStates.pkPatterns(readOnlyArray, player));
            assertEquals((baseIndex + 2) * 10, PkPlayerStates.pkFloor(readOnlyArray, player));
            assertEquals((baseIndex + 3) * 10, PkPlayerStates.pkWall(readOnlyArray, player));
            assertEquals((baseIndex + 4) * 10, PkPlayerStates.points(readOnlyArray, player));
        }
    }

    @Test
    void settersModifyOnlyTheCorrectPlayerAndField() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);

        for (int i = 0; i < 1000; i++) {
            int numberOfPlayers = PlayerId.values().length;
            int[] actualArray = new int[4 * numberOfPlayers];
            int[] expectedArray = new int[4 * numberOfPlayers];

            // Choix aléatoire d'un joueur et de valeurs
            PlayerId randomPlayer = PlayerId.values()[rng.nextInt(numberOfPlayers)];
            int randomPatterns = rng.nextInt(10000);
            int randomFloor = rng.nextInt(10000);
            int randomWall = rng.nextInt(10000);

            // Application des setters
            PkPlayerStates.setPkPatterns(actualArray, randomPlayer, randomPatterns);
            PkPlayerStates.setPkFloor(actualArray, randomPlayer, randomFloor);
            PkPlayerStates.setPkWall(actualArray, randomPlayer, randomWall);

            // Préparation du tableau attendu
            int baseIndex = 4 * randomPlayer.ordinal();
            expectedArray[baseIndex] = randomPatterns;
            expectedArray[baseIndex + 1] = randomFloor;
            expectedArray[baseIndex + 2] = randomWall;

            // Vérification stricte
            assertArrayEquals(expectedArray, actualArray, "Les setters ont modifié les mauvaises cases !");
        }
    }

    @Test
    void addPointsWorksWithPositiveAndNegativeValues() {
        int numberOfPlayers = PlayerId.values().length;
        int[] array = new int[4 * numberOfPlayers];

        PlayerId p1 = PlayerId.values()[0];

        // Test d'ajout positif
        PkPlayerStates.addPoints(array, p1, 15);
        assertEquals(15, array[p1.ordinal() * 4 + 3]);

        // Test d'ajout multiple cumulatif
        PkPlayerStates.addPoints(array, p1, 10);
        assertEquals(25, array[p1.ordinal() * 4 + 3]);

        // Test d'ajout négatif
        PkPlayerStates.addPoints(array, p1, -8);
        assertEquals(17, array[p1.ordinal() * 4 + 3]);

        // Vérification qu'aucun autre champ n'a été touché
        assertEquals(0, array[p1.ordinal() * 4]); // patterns
        assertEquals(0, array[p1.ordinal() * 4 + 1]); // floor
        assertEquals(0, array[p1.ordinal() * 4 + 2]); // wall

        // Vérification qu'aucun autre joueur n'a été touché (si on a plus d'un joueur)
        if (numberOfPlayers > 1) {
            PlayerId p2 = PlayerId.values()[1];
            assertEquals(0, array[p2.ordinal() * 4 + 3]);
        }
    }
    @Test
    void pkPatternsReturnsCorrectValue() {
        int[] raw = new int[8];
        raw[0] = 42; // Patterns J1
        raw[4] = 84; // Patterns J2
        ImmutableIntArray array = ImmutableIntArray.copyOf(raw);
        assertEquals(42, PkPlayerStates.pkPatterns(array, PlayerId.values()[0]));
        if (PlayerId.values().length > 1) {
            assertEquals(84, PkPlayerStates.pkPatterns(array, PlayerId.values()[1]));
        }
    }

    @Test
    void pkFloorReturnsCorrectValue() {
        int[] raw = new int[8];
        raw[1] = 15; // Floor J1
        raw[5] = 30; // Floor J2
        ImmutableIntArray array = ImmutableIntArray.copyOf(raw);
        assertEquals(15, PkPlayerStates.pkFloor(array, PlayerId.values()[0]));
        if (PlayerId.values().length > 1) {
            assertEquals(30, PkPlayerStates.pkFloor(array, PlayerId.values()[1]));
        }
    }

    @Test
    void pkWallReturnsCorrectValue() {
        int[] raw = new int[8];
        raw[2] = 99; // Wall J1
        raw[6] = 198; // Wall J2
        ImmutableIntArray array = ImmutableIntArray.copyOf(raw);
        assertEquals(99, PkPlayerStates.pkWall(array, PlayerId.values()[0]));
        if (PlayerId.values().length > 1) {
            assertEquals(198, PkPlayerStates.pkWall(array, PlayerId.values()[1]));
        }
    }

    @Test
    void pointsReturnsCorrectValue() {
        int[] raw = new int[8];
        raw[3] = 7; // Points J1
        raw[7] = 14; // Points J2
        ImmutableIntArray array = ImmutableIntArray.copyOf(raw);
        assertEquals(7, PkPlayerStates.points(array, PlayerId.values()[0]));
        if (PlayerId.values().length > 1) {
            assertEquals(14, PkPlayerStates.points(array, PlayerId.values()[1]));
        }
    }

    // --- TESTS DES SETTERS (Vérification de l'insertion) ---

    @Test
    void setPkPatternsUpdatesCorrectIndex() {
        int[] array = new int[4];
        PkPlayerStates.setPkPatterns(array, PlayerId.values()[0], 123);
        assertEquals(123, array[0]);
    }

    @Test
    void setPkFloorUpdatesCorrectIndex() {
        int[] array = new int[4];
        PkPlayerStates.setPkFloor(array, PlayerId.values()[0], 456);
        assertEquals(456, array[1]);
    }

    @Test
    void setPkWallUpdatesCorrectIndex() {
        int[] array = new int[4];
        PkPlayerStates.setPkWall(array, PlayerId.values()[0], 789);
        assertEquals(789, array[2]);
    }

    // --- TESTS SUR ADDPOINTS ---

    @Test
    void addPointsAddsPositiveCorrectly() {
        int[] array = new int[4];
        PkPlayerStates.addPoints(array, PlayerId.values()[0], 20);
        assertEquals(20, array[3]);
    }

    @Test
    void addPointsAddsNegativeCorrectly() {
        int[] array = new int[4];
        array[3] = 50; // Set initial points
        PkPlayerStates.addPoints(array, PlayerId.values()[0], -15);
        assertEquals(35, array[3]);
    }

    @Test
    void addPointsAccumulatesCorrectly() {
        int[] array = new int[4];
        PkPlayerStates.addPoints(array, PlayerId.values()[0], 10);
        PkPlayerStates.addPoints(array, PlayerId.values()[0], 5);
        PkPlayerStates.addPoints(array, PlayerId.values()[0], -2);
        assertEquals(13, array[3]);
    }

    // --- TESTS D'ISOLATION DES CHAMPS (S'assurer qu'un setter ne déborde pas) ---

    @Test
    void setPkPatternsDoesNotAffectFloor() {
        int[] array = new int[4];
        PkPlayerStates.setPkPatterns(array, PlayerId.values()[0], 100);
        assertEquals(0, array[1]); // Floor must remain 0
    }

    @Test
    void setPkPatternsDoesNotAffectWall() {
        int[] array = new int[4];
        PkPlayerStates.setPkPatterns(array, PlayerId.values()[0], 100);
        assertEquals(0, array[2]); // Wall must remain 0
    }

    @Test
    void setPkPatternsDoesNotAffectPoints() {
        int[] array = new int[4];
        PkPlayerStates.setPkPatterns(array, PlayerId.values()[0], 100);
        assertEquals(0, array[3]); // Points must remain 0
    }

    @Test
    void setPkFloorDoesNotAffectPatterns() {
        int[] array = new int[4];
        PkPlayerStates.setPkFloor(array, PlayerId.values()[0], 100);
        assertEquals(0, array[0]); // Patterns must remain 0
    }

    @Test
    void setPkWallDoesNotAffectPatterns() {
        int[] array = new int[4];
        PkPlayerStates.setPkWall(array, PlayerId.values()[0], 100);
        assertEquals(0, array[0]); // Patterns must remain 0
    }

    @Test
    void addPointsDoesNotAffectPatterns() {
        int[] array = new int[4];
        PkPlayerStates.addPoints(array, PlayerId.values()[0], 100);
        assertEquals(0, array[0]); // Patterns must remain 0
    }

    // --- TESTS D'ISOLATION DES JOUEURS ---

    @Test
    void playerOneStateDoesNotAffectPlayerTwoState() {
        if (PlayerId.values().length < 2) return;
        int[] array = new int[8];
        PkPlayerStates.setPkPatterns(array, PlayerId.values()[0], 999);
        PkPlayerStates.setPkFloor(array, PlayerId.values()[0], 999);
        assertEquals(0, array[4]); // P2 patterns
        assertEquals(0, array[5]); // P2 floor
    }

    @Test
    void playerTwoStateDoesNotAffectPlayerOneState() {
        if (PlayerId.values().length < 2) return;
        int[] array = new int[8];
        PkPlayerStates.setPkPatterns(array, PlayerId.values()[1], 888);
        PkPlayerStates.addPoints(array, PlayerId.values()[1], 50);
        assertEquals(0, array[0]); // P1 patterns
        assertEquals(0, array[3]); // P1 points
    }

    // --- TESTS DE FUZZING (Intensifs) ---

    @Test
    void fuzzingTestGettersExtractFromCorrectOffsets() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        int playersCount = PlayerId.values().length;

        for (int i = 0; i < 1000; i++) {
            int[] raw = new int[4 * playersCount];
            for (int j = 0; j < raw.length; j++) {
                raw[j] = rng.nextInt();
            }
            ImmutableIntArray immutableArray = ImmutableIntArray.copyOf(raw);

            for (PlayerId player : PlayerId.values()) {
                int base = player.ordinal() * 4;
                assertEquals(raw[base], PkPlayerStates.pkPatterns(immutableArray, player));
                assertEquals(raw[base + 1], PkPlayerStates.pkFloor(immutableArray, player));
                assertEquals(raw[base + 2], PkPlayerStates.pkWall(immutableArray, player));
                assertEquals(raw[base + 3], PkPlayerStates.points(immutableArray, player));
            }
        }
    }

    @Test
    void fuzzingTestSettersWriteToCorrectOffsetsOnly() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        int playersCount = PlayerId.values().length;

        for (int i = 0; i < 1000; i++) {
            int[] array = new int[4 * playersCount];
            PlayerId p = PlayerId.values()[rng.nextInt(playersCount)];

            int patterns = rng.nextInt();
            int floor = rng.nextInt();
            int wall = rng.nextInt();

            PkPlayerStates.setPkPatterns(array, p, patterns);
            PkPlayerStates.setPkFloor(array, p, floor);
            PkPlayerStates.setPkWall(array, p, wall);

            assertEquals(patterns, array[p.ordinal() * 4]);
            assertEquals(floor, array[p.ordinal() * 4 + 1]);
            assertEquals(wall, array[p.ordinal() * 4 + 2]);

            // Vérifier que les points sont intacts
            assertEquals(0, array[p.ordinal() * 4 + 3]);
        }
    }

    @Test
    void fuzzingTestAddPointsCumulatesAccurately() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        int playersCount = PlayerId.values().length;

        for (int i = 0; i < 1000; i++) {
            int[] array = new int[4 * playersCount];
            PlayerId p = PlayerId.values()[rng.nextInt(playersCount)];

            int totalExpected = 0;
            int operationsCount = rng.nextInt(5, 20);

            for (int op = 0; op < operationsCount; op++) {
                int pointsToAdd = rng.nextInt(-50, 50);
                totalExpected += pointsToAdd;
                PkPlayerStates.addPoints(array, p, pointsToAdd);
            }

            assertEquals(totalExpected, array[p.ordinal() * 4 + 3]);
        }
    }
}
