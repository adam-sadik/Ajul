package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
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
            Assertions.assertEquals((baseIndex + 1) * 10, PkPlayerStates.pkPatterns(readOnlyArray, player));
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

    private Game game4Players() {
        return new Game(List.of(
                new Game.PlayerDescription(PlayerId.ALL.get(0), "Alice", Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.ALL.get(1), "Bob", Game.PlayerDescription.PlayerKind.AI),
                new Game.PlayerDescription(PlayerId.ALL.get(2), "Charlie", Game.PlayerDescription.PlayerKind.AI),
                new Game.PlayerDescription(PlayerId.ALL.get(3), "Diana", Game.PlayerDescription.PlayerKind.AI)
        ));
    }

    @Test
    void initialCreatesCorrectArraySizeAndValues() {
        // On utilise la partie à 4 joueurs pour avoir 4 * 4 = 16 éléments
        Game game = game4Players();
        ImmutableIntArray initialStates = PkPlayerStates.initial(game);

        assertEquals(16, initialStates.size(), "Pour 4 joueurs, le tableau doit faire 4*4 = 16 éléments");

        for (int i = 0; i < 16; i++) {
            assertEquals(0, initialStates.get(i), "L'état initial doit être rempli de zéros");
        }
    }

    @Test
    void settersAndGettersDoNotInterfereBetweenPlayers() {
        int[] states = new int[16]; // Simulation de 4 joueurs

        // On assigne des valeurs TRÈS spécifiques pour P1 et P4
        PkPlayerStates.setPkPatterns(states, PlayerId.ALL.get(0), 0b101010);
        PkPlayerStates.setPkFloor(states, PlayerId.ALL.get(0), 0b111111);
        PkPlayerStates.setPkWall(states, PlayerId.ALL.get(0), 0b000001);

        // Index de P4 = 3
        PkPlayerStates.setPkPatterns(states, PlayerId.ALL.get(3), 0b111000);
        PkPlayerStates.setPkFloor(states, PlayerId.ALL.get(3), 0b000111);
        PkPlayerStates.setPkWall(states, PlayerId.ALL.get(3), 0b111111);

        ImmutableIntArray readOnlyStates = ImmutableIntArray.copyOf(states);

        // Vérification P1 (Index 0)
        assertEquals(0b101010, PkPlayerStates.pkPatterns(readOnlyStates, PlayerId.ALL.get(0)));
        assertEquals(0b111111, PkPlayerStates.pkFloor(readOnlyStates, PlayerId.ALL.get(0)));
        assertEquals(0b000001, PkPlayerStates.pkWall(readOnlyStates, PlayerId.ALL.get(0)));

        // Vérification P4 (Index 3) (doit être intact et à la bonne place)
        assertEquals(0b111000, PkPlayerStates.pkPatterns(readOnlyStates, PlayerId.ALL.get(3)));
        assertEquals(0b000111, PkPlayerStates.pkFloor(readOnlyStates, PlayerId.ALL.get(3)));
        assertEquals(0b111111, PkPlayerStates.pkWall(readOnlyStates, PlayerId.ALL.get(3)));

        // Vérification P2 et P3 (doivent être restés à 0, pas d'écrasement)
        assertEquals(0, PkPlayerStates.pkPatterns(readOnlyStates, PlayerId.ALL.get(1)));
        assertEquals(0, PkPlayerStates.pkWall(readOnlyStates, PlayerId.ALL.get(2)));
    }

    @Test
    void addPointsWorksWithPossitiveAndNegativeValues() {
        int[] states = new int[8]; // Simulation de 2 joueurs

        // Test addition simple sur P1
        PkPlayerStates.addPoints(states, PlayerId.ALL.get(0), 10);
        ImmutableIntArray read1 = ImmutableIntArray.copyOf(states);
        assertEquals(10, PkPlayerStates.points(read1, PlayerId.ALL.get(0)));

        // Test cumul sur P1
        PkPlayerStates.addPoints(states, PlayerId.ALL.get(0), 15);
        ImmutableIntArray read2 = ImmutableIntArray.copyOf(states);
        assertEquals(25, PkPlayerStates.points(read2, PlayerId.ALL.get(0)));

        // Test points négatifs (pénalités du plancher) sur P1
        PkPlayerStates.addPoints(states, PlayerId.ALL.get(0), -5);
        ImmutableIntArray read3 = ImmutableIntArray.copyOf(states);
        assertEquals(20, PkPlayerStates.points(read3, PlayerId.ALL.get(0)));

        // P2 ne doit pas avoir été impacté
        assertEquals(0, PkPlayerStates.points(read3, PlayerId.ALL.get(1)));
    }

    private Game game2Players() {
        return new Game(List.of(
                new Game.PlayerDescription(PlayerId.ALL.get(0), "Alice", Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.ALL.get(1), "Bob", Game.PlayerDescription.PlayerKind.AI)
        ));
    }

    @Test
    void initialStateHasCorrectSizeFor2Players() {
        ImmutableIntArray states = PkPlayerStates.initial(game2Players());
        assertEquals(8, states.size(), "2 joueurs = 8 entiers");
    }

    @Test
    void initialStateHasCorrectSizeFor4Players() {
        ImmutableIntArray states = PkPlayerStates.initial(game4Players());
        assertEquals(16, states.size(), "4 joueurs = 16 entiers");
    }

    @Test
    void setAndGetPatternsWorksForAllPlayersIndependently() {
        int[] states = new int[16]; // 4 joueurs

        // Valeurs arbitraires complexes (bits alternés)
        int pat0 = 0b1010101010;
        int pat1 = 0b0101010101;
        int pat2 = 0b1111000011;
        int pat3 = 0b0000111100;

        PkPlayerStates.setPkPatterns(states, PlayerId.ALL.get(0), pat0);
        PkPlayerStates.setPkPatterns(states, PlayerId.ALL.get(1), pat1);
        PkPlayerStates.setPkPatterns(states, PlayerId.ALL.get(2), pat2);
        PkPlayerStates.setPkPatterns(states, PlayerId.ALL.get(3), pat3);

        ImmutableIntArray read = ImmutableIntArray.copyOf(states);

        assertEquals(pat0, PkPlayerStates.pkPatterns(read, PlayerId.ALL.get(0)));
        assertEquals(pat1, PkPlayerStates.pkPatterns(read, PlayerId.ALL.get(1)));
        assertEquals(pat2, PkPlayerStates.pkPatterns(read, PlayerId.ALL.get(2)));
        assertEquals(pat3, PkPlayerStates.pkPatterns(read, PlayerId.ALL.get(3)));

        // Vérifier que les autres champs (floor, wall, points) sont toujours à 0
        assertEquals(0, PkPlayerStates.pkFloor(read, PlayerId.ALL.get(1)));
        assertEquals(0, PkPlayerStates.points(read, PlayerId.ALL.get(3)));
    }

    @Test
    void pointsCanReachTheoreticalMaximumAndBeyond() {
        int[] states = new int[8];

        // Ajul a un max théorique d'environ 240, testons cette limite et au-delà
        PkPlayerStates.addPoints(states, PlayerId.ALL.get(0), 240);
        PkPlayerStates.addPoints(states, PlayerId.ALL.get(1), 500);

        ImmutableIntArray read = ImmutableIntArray.copyOf(states);
        assertEquals(240, PkPlayerStates.points(read, PlayerId.ALL.get(0)));
        assertEquals(500, PkPlayerStates.points(read, PlayerId.ALL.get(1)));
    }

    @Test
    void pointsCanGoNegative() {
        int[] states = new int[8];

        PkPlayerStates.addPoints(states, PlayerId.ALL.get(0), 10);
        PkPlayerStates.addPoints(states, PlayerId.ALL.get(0), -24); // Chute brutale

        ImmutableIntArray read = ImmutableIntArray.copyOf(states);
        assertEquals(-14, PkPlayerStates.points(read, PlayerId.ALL.get(0)));
    }

    @Test
    void completeStateUpdateForOnePlayerDoesNotCorruptOthers() {
        int[] states = new int[16];

        // On bourre l'état du joueur 2 (index 2)
        PkPlayerStates.setPkPatterns(states, PlayerId.ALL.get(2), Integer.MAX_VALUE);
        PkPlayerStates.setPkFloor(states, PlayerId.ALL.get(2), Integer.MAX_VALUE);
        PkPlayerStates.setPkWall(states, PlayerId.ALL.get(2), Integer.MAX_VALUE);
        PkPlayerStates.addPoints(states, PlayerId.ALL.get(2), Integer.MAX_VALUE);

        ImmutableIntArray read = ImmutableIntArray.copyOf(states);

        // Vérification des voisins : Joueur 1 (index 1) et Joueur 3 (index 3)
        assertEquals(0, PkPlayerStates.pkPatterns(read, PlayerId.ALL.get(1)));
        assertEquals(0, PkPlayerStates.pkFloor(read, PlayerId.ALL.get(1)));
        assertEquals(0, PkPlayerStates.pkWall(read, PlayerId.ALL.get(1)));
        assertEquals(0, PkPlayerStates.points(read, PlayerId.ALL.get(1)));

        assertEquals(0, PkPlayerStates.pkPatterns(read, PlayerId.ALL.get(3)));
        assertEquals(0, PkPlayerStates.pkFloor(read, PlayerId.ALL.get(3)));
        assertEquals(0, PkPlayerStates.pkWall(read, PlayerId.ALL.get(3)));
        assertEquals(0, PkPlayerStates.points(read, PlayerId.ALL.get(3)));
    }
    // --- Utilitaires de test ---

    private Game createMockGame(int playerCount) {
        // Crée une partie avec le nombre de joueurs demandé
        var players = new java.util.ArrayList<Game.PlayerDescription>();
        for (int i = 0; i < playerCount; i++) {
            players.add(new Game.PlayerDescription(PlayerId.ALL.get(i), "P" + i, Game.PlayerDescription.PlayerKind.HUMAN));
        }
        return new Game(players);
    }

    private PlayerId[] getActivePlayers(int count) {
        PlayerId[] ids = new PlayerId[count];
        for(int i=0; i<count; i++) ids[i] = PlayerId.ALL.get(i);
        return ids;
    }

    // =========================================================================
    // TESTS DE : initial(Game)
    // =========================================================================

    @Test
    void initialArraySizeIsCorrectForTwoPlayers() {
        Game game = createMockGame(2);
        var state = PkPlayerStates.initial(game);
        assertEquals(8, state.size(), "Un jeu à 2 joueurs nécessite 8 entiers");
    }

    @Test
    void initialArraySizeIsCorrectForFourPlayers() {
        Game game = createMockGame(4);
        var state = PkPlayerStates.initial(game);
        assertEquals(16, state.size(), "Un jeu à 4 joueurs nécessite 16 entiers");
    }

    @Test
    void initialArrayIsStrictlyFilledWithZeros() {
        Game game = createMockGame(3); // 3 joueurs = 12 entiers
        var state = PkPlayerStates.initial(game);

        for (int i = 0; i < state.size(); i++) {
            assertEquals(0, state.get(i), "L'index " + i + " doit être 0 à l'initialisation");
        }
    }

    @Test
    void initialWorksOnExtremeBounds() {
        // En théorie, Azul se joue de 2 à 4 joueurs. Testons la limite basse.
        Game game = createMockGame(2);
        assertDoesNotThrow(() -> PkPlayerStates.initial(game));
    }

    @Test
    void initialProvidesReadAccessThroughGettersWithoutCrashing() {
        Game game = createMockGame(2);
        var state = PkPlayerStates.initial(game);

        // S'assure que les getters peuvent lire l'état initial (0) sans lever d'erreur
        assertEquals(0, PkPlayerStates.pkPatterns(state, PlayerId.P1));
        assertEquals(0, PkPlayerStates.pkFloor(state, PlayerId.P2));
        assertEquals(0, PkPlayerStates.points(state, PlayerId.P2));
    }

    // =========================================================================
    // TESTS DE : pkPatterns & setPkPatterns
    // =========================================================================

    @Test
    void pkPatternsSetAndGetTrivial() {
        int[] state = new int[16]; // Partie à 4 joueurs
        int patternVal = 0b101010;

        PkPlayerStates.setPkPatterns(state, PlayerId.P3, patternVal);
        var readOnlyState = ImmutableIntArray.copyOf(state);

        assertEquals(patternVal, PkPlayerStates.pkPatterns(readOnlyState, PlayerId.P3));
    }

    @Test
    void pkPatternsWritesStrictlyToCorrectOffset() {
        int[] state = new int[16];
        int patternVal = 42;

        // L'index de P2 (ordinal 1) pour les patterns (offset 0) doit être 4.
        PkPlayerStates.setPkPatterns(state, PlayerId.P2, patternVal);
        assertEquals(patternVal, state[4]);

        // Tous les autres index doivent être intacts (0)
        for(int i=0; i<16; i++) {
            if(i != 4) assertEquals(0, state[i]);
        }
    }

    @Test
    void pkPatternsDoesNotAffectOtherPlayersOrFields() {
        int[] state = new int[8];
        PkPlayerStates.setPkPatterns(state, PlayerId.P1, 999);
        PkPlayerStates.setPkFloor(state, PlayerId.P2, 888);

        var roState = ImmutableIntArray.copyOf(state);

        // Modifier les patterns de P1 ne touche ni son floor, ni les patterns de P2
        assertEquals(0, PkPlayerStates.pkFloor(roState, PlayerId.P1));
        assertEquals(0, PkPlayerStates.pkPatterns(roState, PlayerId.P2));
        assertEquals(999, PkPlayerStates.pkPatterns(roState, PlayerId.P1));
    }

    @Test
    void pkPatternsReadsCorrectOffsetProfStyle() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            for (int j = 0; j < 16; j++) rawState[j] = rng.nextInt();

            var roState = ImmutableIntArray.copyOf(rawState);

            for (PlayerId id : getActivePlayers(4)) {
                int expectedIndex = id.ordinal() * 4 + 0; // offset 0
                assertEquals(rawState[expectedIndex], PkPlayerStates.pkPatterns(roState, id));
            }
        }
    }

    @Test
    void pkPatternsWritesCorrectOffsetProfStyle() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            PlayerId randomPlayer = PlayerId.ALL.get(rng.nextInt(4));
            int randomVal = rng.nextInt();

            PkPlayerStates.setPkPatterns(rawState, randomPlayer, randomVal);

            int expectedIndex = randomPlayer.ordinal() * 4 + 0;
            assertEquals(randomVal, rawState[expectedIndex]);
        }
    }

    // =========================================================================
    // TESTS DE : pkFloor & setPkFloor
    // =========================================================================

    @Test
    void pkFloorSetAndGetTrivial() {
        int[] state = new int[16];
        int floorVal = 0b111;
        PkPlayerStates.setPkFloor(state, PlayerId.P4, floorVal);
        assertEquals(floorVal, PkPlayerStates.pkFloor(ImmutableIntArray.copyOf(state), PlayerId.P4));
    }

    @Test
    void pkFloorWritesStrictlyToCorrectOffset() {
        int[] state = new int[16];
        PkPlayerStates.setPkFloor(state, PlayerId.P3, 77);
        // P3 (ordinal 2) * 4 + offset 1 = 9
        assertEquals(77, state[9]);
    }

    @Test
    void pkFloorDoesNotAffectOtherFields() {
        int[] state = new int[8];
        PkPlayerStates.setPkFloor(state, PlayerId.P1, 123);
        var roState = ImmutableIntArray.copyOf(state);

        assertEquals(0, PkPlayerStates.pkPatterns(roState, PlayerId.P1));
        assertEquals(0, PkPlayerStates.pkWall(roState, PlayerId.P1));
        assertEquals(123, PkPlayerStates.pkFloor(roState, PlayerId.P1));
    }

    @Test
    void pkFloorReadsCorrectOffsetProfStyle() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            for (int j = 0; j < 16; j++) rawState[j] = rng.nextInt();
            var roState = ImmutableIntArray.copyOf(rawState);

            for (PlayerId id : getActivePlayers(4)) {
                int expectedIndex = id.ordinal() * 4 + 1; // offset 1
                assertEquals(rawState[expectedIndex], PkPlayerStates.pkFloor(roState, id));
            }
        }
    }

    @Test
    void pkFloorWritesCorrectOffsetProfStyle() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            PlayerId target = PlayerId.ALL.get(rng.nextInt(4));
            int val = rng.nextInt();

            PkPlayerStates.setPkFloor(rawState, target, val);
            int expectedIndex = target.ordinal() * 4 + 1;
            assertEquals(val, rawState[expectedIndex]);
        }
    }

    // =========================================================================
    // TESTS DE : pkWall & setPkWall
    // =========================================================================

    @Test
    void pkWallSetAndGetTrivial() {
        int[] state = new int[8];
        int wallVal = (1 << 25) - 1; // Mur plein
        PkPlayerStates.setPkWall(state, PlayerId.P2, wallVal);
        assertEquals(wallVal, PkPlayerStates.pkWall(ImmutableIntArray.copyOf(state), PlayerId.P2));
    }

    @Test
    void pkWallWritesStrictlyToCorrectOffset() {
        int[] state = new int[12]; // 3 joueurs
        PkPlayerStates.setPkWall(state, PlayerId.P1, 55);
        // P1 (ordinal 0) * 4 + offset 2 = 2
        assertEquals(55, state[2]);
    }

    @Test
    void pkWallDoesNotAffectOtherFields() {
        int[] state = new int[8];
        PkPlayerStates.setPkWall(state, PlayerId.P2, 999);
        var roState = ImmutableIntArray.copyOf(state);

        assertEquals(0, PkPlayerStates.pkFloor(roState, PlayerId.P2));
        assertEquals(0, PkPlayerStates.points(roState, PlayerId.P2));
        assertEquals(999, PkPlayerStates.pkWall(roState, PlayerId.P2));
    }

    @Test
    void pkWallReadsCorrectOffsetProfStyle() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            for (int j = 0; j < 16; j++) rawState[j] = rng.nextInt();
            var roState = ImmutableIntArray.copyOf(rawState);

            for (PlayerId id : getActivePlayers(4)) {
                int expectedIndex = id.ordinal() * 4 + 2; // offset 2
                assertEquals(rawState[expectedIndex], PkPlayerStates.pkWall(roState, id));
            }
        }
    }

    @Test
    void pkWallWritesCorrectOffsetProfStyle() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            PlayerId target = PlayerId.ALL.get(rng.nextInt(4));
            int val = rng.nextInt();

            PkPlayerStates.setPkWall(rawState, target, val);
            int expectedIndex = target.ordinal() * 4 + 2;
            assertEquals(val, rawState[expectedIndex]);
        }
    }

    // =========================================================================
    // TESTS DE : points & addPoints
    // =========================================================================

    @Test
    void addPointsTrivialPositiveAndNegative() {
        int[] state = new int[8];

        PkPlayerStates.addPoints(state, PlayerId.P1, 10);
        var roState1 = ImmutableIntArray.copyOf(state);
        assertEquals(10, PkPlayerStates.points(roState1, PlayerId.P1));

        // Ajout d'un score négatif (pénalité du plancher par exemple)
        PkPlayerStates.addPoints(state, PlayerId.P1, -3);
        var roState2 = ImmutableIntArray.copyOf(state);
        assertEquals(7, PkPlayerStates.points(roState2, PlayerId.P1));
    }

    @Test
    void addPointsAccumulattesCorrectly() {
        int[] state = new int[8];

        // Ajouts séquentiels
        PkPlayerStates.addPoints(state, PlayerId.P2, 5);
        PkPlayerStates.addPoints(state, PlayerId.P2, 12);
        PkPlayerStates.addPoints(state, PlayerId.P2, -2);

        var roState = ImmutableIntArray.copyOf(state);
        assertEquals(15, PkPlayerStates.points(roState, PlayerId.P2));
    }

    @Test
    void addPointsDoesNotAffectOtherFields() {
        int[] state = new int[8];
        PkPlayerStates.addPoints(state, PlayerId.P1, 45);
        // P1 (ordinal 0) * 4 + offset 3 = 3
        assertEquals(45, state[3]);

        // Les offsets 0, 1, 2 doivent rester à 0
        assertEquals(0, state[0]);
        assertEquals(0, state[1]);
        assertEquals(0, state[2]);
    }

    @Test
    void pointsReadsCorrectOffsetProfStyle() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            for (int j = 0; j < 16; j++) rawState[j] = rng.nextInt();
            var roState = ImmutableIntArray.copyOf(rawState);

            for (PlayerId id : getActivePlayers(4)) {
                int expectedIndex = id.ordinal() * 4 + 3; // offset 3
                assertEquals(rawState[expectedIndex], PkPlayerStates.points(roState, id));
            }
        }
    }

    @Test
    void addPointsProfStyleRandomized() {
        var rng = RandomGeneratorFactory.getDefault().create(2026);
        for (int i = 0; i < 1000; i++) {
            int[] rawState = new int[16];
            int[] expectedPoints = new int[4];

            // On fait 20 ajouts aléatoires de points
            for(int k = 0; k < 20; k++) {
                PlayerId target = PlayerId.ALL.get(rng.nextInt(4));
                int addVal = rng.nextInt(-20, 50); // Simulation points et pénalités

                PkPlayerStates.addPoints(rawState, target, addVal);
                expectedPoints[target.ordinal()] += addVal;
            }

            // On vérifie que le total cumulé est correct pour chaque joueur
            var roState = ImmutableIntArray.copyOf(rawState);
            for (PlayerId id : getActivePlayers(4)) {
                assertEquals(expectedPoints[id.ordinal()], PkPlayerStates.points(roState, id));
            }
        }
    }
}

