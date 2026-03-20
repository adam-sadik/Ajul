package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.*;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import ch.epfl.ajul.intarray.MutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

public class MyImmutableGameStateTest {

    // =========================================================================
    // UTILITAIRES DE TESTS (Garantissent la robustesse)
    // =========================================================================

    private Game createGame(int players) {
        var list = new ArrayList<Game.PlayerDescription>();
        for (int i = 0; i < players; i++) {
            list.add(new Game.PlayerDescription(PlayerId.ALL.get(i), "Player" + i, Game.PlayerDescription.PlayerKind.HUMAN));
        }
        return new Game(list);
    }

    private Game game2Players() {
        return createGame(2);
    }

    // Construit un mur avec la Ligne 1 pleine en utilisant les méthodes officielles (indépendant des bits)
    private int createFullWallRow() {
        int wall = PkWall.EMPTY;
        for (TileKind.Colored c : TileKind.Colored.ALL) {
            wall = PkWall.withTileAt(wall, TileDestination.Pattern.PATTERN_1, c);
        }
        return wall;
    }

    // Calcule exactement les 101 tuiles du jeu (20 de chaque + 1 marqueur)
    private int getExactGameTilesSet() {
        int gameSet = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        for (TileKind.Colored c : TileKind.Colored.ALL) {
            gameSet = PkTileSet.union(gameSet, PkTileSet.of(20, c));
        }
        return gameSet;
    }

    // Compare deux tableaux de coups sans se soucier de l'ordre d'insertion
    private void assertMovesEqualUnordered(short[] expected, int expectedCount, short[] actual, int actualCount) {
        assertEquals(expectedCount, actualCount, "Le nombre de coups générés ne correspond pas");
        short[] expectedSlice = Arrays.copyOf(expected, expectedCount);
        short[] actualSlice = Arrays.copyOf(actual, actualCount);
        Arrays.sort(expectedSlice);
        Arrays.sort(actualSlice);
        assertArrayEquals(expectedSlice, actualSlice, "Les coups générés ne sont pas identiques (indépendamment de l'ordre)");
    }

    // L'USINE À MOCK
    private static class MockGameState implements ReadOnlyGameState {
        Game game;
        int pkTileBag;
        ReadOnlyIntArray pkTileSources;
        int pkUniqueTileSources;
        ReadOnlyIntArray pkPlayerStates;
        PlayerId currentPlayerId;

        @Override public Game game() { return game; }
        @Override public int pkTileBag() { return pkTileBag; }
        @Override public ReadOnlyIntArray pkTileSources() { return pkTileSources; }
        @Override public int pkUniqueTileSources() { return pkUniqueTileSources; }
        @Override public ReadOnlyIntArray pkPlayerStates() { return pkPlayerStates; }
        @Override public PlayerId currentPlayerId() { return currentPlayerId; }
    }

    private MockGameState createEmptyState(int players) {
        MockGameState state = new MockGameState();
        state.game = createGame(players);
        state.pkTileBag = PkTileSet.EMPTY;
        state.pkUniqueTileSources = 0;
        state.pkTileSources = MutableIntArray.wrapping(new int[state.game.tileSourcesCount()]);
        state.pkPlayerStates = MutableIntArray.wrapping(new int[players * 4]);
        state.currentPlayerId = PlayerId.ALL.get(0);
        return state;
    }

    // =========================================================================
    // TESTS DU CONSTRUCTEUR ET INITIALISATION
    // =========================================================================

    @Test
    void constructorAcceptsValidArguments() {
        Game game = game2Players();
        ImmutableIntArray sources = ImmutableIntArray.copyOf(new int[game.tileSourcesCount()]);
        ImmutableIntArray players = ImmutableIntArray.copyOf(new int[8]);
        assertDoesNotThrow(() -> new ImmutableGameState(game, 0, sources, 0, players, PlayerId.ALL.get(0)));
    }

    @Test
    void constructorThrowsNullPointerExceptionOnNullArguments() {
        Game game = game2Players();
        ImmutableIntArray validSources = ImmutableIntArray.copyOf(new int[game.tileSourcesCount()]);
        ImmutableIntArray validPlayers = ImmutableIntArray.copyOf(new int[8]);

        assertThrows(NullPointerException.class, () -> new ImmutableGameState(null, 0, validSources, 0, validPlayers, PlayerId.ALL.get(0)));
        assertThrows(NullPointerException.class, () -> new ImmutableGameState(game, 0, null, 0, validPlayers, PlayerId.ALL.get(0)));
        assertThrows(NullPointerException.class, () -> new ImmutableGameState(game, 0, validSources, 0, null, PlayerId.ALL.get(0)));
        assertThrows(NullPointerException.class, () -> new ImmutableGameState(game, 0, validSources, 0, validPlayers, null));
    }

    @Test
    void immutableReturnsThisInstance() {
        ImmutableGameState state = ImmutableGameState.initial(game2Players());
        assertSame(state, state.immutable(), "La méthode immutable() doit retourner 'this'");
    }

    @Test
    void initialStateIsCorrectlyConstructed() {
        Game game = game2Players();
        ImmutableGameState state = ImmutableGameState.initial(game);

        assertEquals(PkTileSet.FULL_COLORED, state.pkTileBag());
        int expectedCenter = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        assertEquals(expectedCenter, state.pkTileSources().get(0));

        for (int i = 1; i < state.pkTileSources().size(); i++) {
            assertEquals(0, state.pkTileSources().get(i), "La fabrique " + i + " doit être vide");
        }
        assertEquals(PkIntSet32.EMPTY, state.pkUniqueTileSources());
        assertEquals(PlayerId.ALL.get(0), state.currentPlayerId());
    }

    // =========================================================================
    // TESTS : isRoundOver & isGameOver
    // =========================================================================

    @Test
    void isRoundOverWorksWithMarkerInCenter() {
        MockGameState state = createEmptyState(2);
        int[] sources = new int[state.game.tileSourcesCount()];
        sources[0] = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        state.pkTileSources = MutableIntArray.wrapping(sources);

        assertTrue(state.isRoundOver(), "Le marqueur seul ne prolonge pas la manche");
    }

    @Test
    void isRoundOverWorksWhenColoredTilesRemain() {
        MockGameState state = createEmptyState(2);
        int[] sources = new int[state.game.tileSourcesCount()];
        sources[3] = PkTileSet.of(1, TileKind.Colored.C);
        state.pkTileSources = MutableIntArray.wrapping(sources);
        state.pkUniqueTileSources = 0b001000; // La source 3 est unique

        assertFalse(state.isRoundOver());
    }


    @Test
    void isGameOverReturnsTrueIfRoundOverAndWallHasFullRow() {
        MockGameState state = createEmptyState(2);

        // Manche terminée (sources vides)
        int[] sources = new int[state.game.tileSourcesCount()];
        state.pkTileSources = MutableIntArray.wrapping(sources);

        // Mur plein pour le joueur 2
        int[] playersData = new int[8];
        PkPlayerStates.setPkWall(playersData, PlayerId.ALL.get(1), createFullWallRow());
        state.pkPlayerStates = MutableIntArray.wrapping(playersData);

        assertTrue(state.isGameOver());
    }

    // =========================================================================
    // TESTS : pkDiscardedTiles
    // =========================================================================

    @Test
    void pkDiscardedTilesTrivialEmptyState() {
        MockGameState state = createEmptyState(2);
        assertEquals(getExactGameTilesSet(), state.pkDiscardedTiles(), "Si rien n'est en jeu, tout est défaussé");
    }

    @Test
    void pkDiscardedTilesMathCrossCheck() {
        MockGameState state = createEmptyState(2);
        state.pkTileBag = PkTileSet.of(5, TileKind.Colored.A);

        int[] sources = new int[state.game.tileSourcesCount()];
        sources[1] = PkTileSet.of(2, TileKind.Colored.B);
        state.pkTileSources = MutableIntArray.wrapping(sources);

        int[] playersData = new int[8];
        int patternP1 = PkPatterns.withAddedTiles(PkPatterns.EMPTY, TileDestination.Pattern.PATTERN_3, 3, TileKind.Colored.C);
        PkPlayerStates.setPkPatterns(playersData, PlayerId.ALL.get(0), patternP1);

        int floorP2 = PkFloor.withAddedTiles(PkFloor.EMPTY, PkTileSet.of(1, TileKind.Colored.D));
        PkPlayerStates.setPkFloor(playersData, PlayerId.ALL.get(1), floorP2);

        int wallP1 = PkWall.withTileAt(PkWall.EMPTY, TileDestination.Pattern.PATTERN_1, TileKind.Colored.E);
        PkPlayerStates.setPkWall(playersData, PlayerId.ALL.get(0), wallP1);

        state.pkPlayerStates = MutableIntArray.wrapping(playersData);

        int expectedDiscarded = getExactGameTilesSet();
        expectedDiscarded = PkTileSet.difference(expectedDiscarded, PkTileSet.of(5, TileKind.Colored.A));
        expectedDiscarded = PkTileSet.difference(expectedDiscarded, PkTileSet.of(2, TileKind.Colored.B));
        expectedDiscarded = PkTileSet.difference(expectedDiscarded, PkTileSet.of(3, TileKind.Colored.C));
        expectedDiscarded = PkTileSet.difference(expectedDiscarded, PkTileSet.of(1, TileKind.Colored.D));
        expectedDiscarded = PkTileSet.difference(expectedDiscarded, PkTileSet.of(1, TileKind.Colored.E));

        assertEquals(expectedDiscarded, state.pkDiscardedTiles());
    }

    // =========================================================================
    // TESTS : validMoves & uniqueValidMoves
    // =========================================================================

    @Test
    void validMovesPopulatesArrayCorrectlyForSingleTile() {
        MockGameState state = createEmptyState(2);
        int[] sources = new int[state.game.tileSourcesCount()];
        sources[0] = PkTileSet.of(1, TileKind.Colored.A);
        state.pkTileSources = MutableIntArray.wrapping(sources);

        short[] expectedDest = new short[Move.MAX_MOVES];
        int expectedCount = 0;
        TileSource center = TileSource.ALL.get(0);

        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.A, TileDestination.FLOOR);
        for (TileDestination.Pattern pattern : TileDestination.Pattern.ALL) {
            expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.A, pattern);
        }

        short[] actualDest = new short[Move.MAX_MOVES];
        int actualCount = state.validMoves(actualDest);

        assertMovesEqualUnordered(expectedDest, expectedCount, actualDest, actualCount);
    }

    @Test
    void validMovesFiltersOutWrongColorPatternLines() {
        MockGameState state = createEmptyState(2);
        int[] sources = new int[state.game.tileSourcesCount()];
        sources[1] = PkTileSet.of(1, TileKind.Colored.C);
        state.pkTileSources = MutableIntArray.wrapping(sources);

        int[] playersData = new int[8];
        int pattern = PkPatterns.withAddedTiles(PkPatterns.EMPTY, TileDestination.Pattern.PATTERN_2, 1, TileKind.Colored.D);
        PkPlayerStates.setPkPatterns(playersData, PlayerId.ALL.get(0), pattern);
        state.pkPlayerStates = MutableIntArray.wrapping(playersData);

        short[] dest = new short[Move.MAX_MOVES];
        int count = state.validMoves(dest);

        // Plancher + 4 lignes (ligne 2 ignorée) = 5 coups
        assertEquals(5, count);
    }

    @Test
    void uniqueValidMovesArrayContentFiltersDuplicatesExactly() {
        MockGameState state = createEmptyState(2);
        int[] sources = new int[state.game.tileSourcesCount()];

        sources[1] = PkTileSet.of(1, TileKind.Colored.A);
        sources[2] = PkTileSet.of(1, TileKind.Colored.A); // Doublon
        state.pkTileSources = MutableIntArray.wrapping(sources);
        state.pkUniqueTileSources = 0b000010; // Seule la F1 (bit 1) est unique

        short[] expectedDest = new short[Move.MAX_MOVES];
        int expectedCount = 0;
        expectedDest[expectedCount++] = (short) PkMove.pack(TileSource.ALL.get(1), TileKind.Colored.A, TileDestination.FLOOR);
        for (TileDestination.Pattern p : TileDestination.Pattern.ALL) {
            expectedDest[expectedCount++] = (short) PkMove.pack(TileSource.ALL.get(1), TileKind.Colored.A, p);
        }

        short[] actualDest = new short[Move.MAX_MOVES];
        int actualCount = state.uniqueValidMoves(actualDest);

        assertMovesEqualUnordered(expectedDest, expectedCount, actualDest, actualCount);
    }

    @Test
    void validMovesExactContentWithColorRestrictions() {
        MockGameState state = createEmptyState(2);
        int[] sources = new int[state.game.tileSourcesCount()];
        sources[0] = PkTileSet.union(PkTileSet.of(1, TileKind.Colored.A), PkTileSet.of(1, TileKind.Colored.B));
        state.pkTileSources = MutableIntArray.wrapping(sources);

        int[] playersData = new int[8];
        int pattern = PkPatterns.EMPTY;
        pattern = PkPatterns.withAddedTiles(pattern, TileDestination.Pattern.PATTERN_1, 1, TileKind.Colored.A);
        pattern = PkPatterns.withAddedTiles(pattern, TileDestination.Pattern.PATTERN_2, 1, TileKind.Colored.B);
        pattern = PkPatterns.withAddedTiles(pattern, TileDestination.Pattern.PATTERN_3, 1, TileKind.Colored.C);
        PkPlayerStates.setPkPatterns(playersData, PlayerId.ALL.get(0), pattern);
        state.pkPlayerStates = MutableIntArray.wrapping(playersData);

        short[] expectedDest = new short[Move.MAX_MOVES];
        int expectedCount = 0;
        TileSource center = TileSource.ALL.get(0);

        // Couleur A (accepte Floor, P4, P5)
        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.A, TileDestination.FLOOR);
        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.A, TileDestination.Pattern.PATTERN_4);
        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.A, TileDestination.Pattern.PATTERN_5);

        // Couleur B (accepte Floor, P2, P4, P5)
        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.B, TileDestination.FLOOR);
        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.B, TileDestination.Pattern.PATTERN_2);
        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.B, TileDestination.Pattern.PATTERN_4);
        expectedDest[expectedCount++] = (short) PkMove.pack(center, TileKind.Colored.B, TileDestination.Pattern.PATTERN_5);

        short[] actualDest = new short[Move.MAX_MOVES];
        int actualCount = state.validMoves(actualDest);

        assertMovesEqualUnordered(expectedDest, expectedCount, actualDest, actualCount);
    }
}