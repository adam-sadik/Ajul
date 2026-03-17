package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.gamestate.packed.PkIntSet32;
import ch.epfl.ajul.gamestate.packed.PkPlayerStates;
import ch.epfl.ajul.gamestate.packed.PkTileSet;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyImmutableGameStateTest {
    private Game game2Players() {
        return new Game(List.of(
                new Game.PlayerDescription(PlayerId.ALL.get(0), "Alice", Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.ALL.get(1), "Bob", Game.PlayerDescription.PlayerKind.AI)
        ));
    }

    @Test
    void constructorThrowsNullPointerExceptionOnNullArguments() {
        Game game = game2Players();
        ImmutableIntArray validSources = ImmutableIntArray.copyOf(new int[6]);
        ImmutableIntArray validPlayers = ImmutableIntArray.copyOf(new int[8]);

        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(null, 0, validSources, 0, validPlayers, PlayerId.ALL.get(0))
        );
        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(game, 0, null, 0, validPlayers, PlayerId.ALL.get(0))
        );
        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(game, 0, validSources, 0, null, PlayerId.ALL.get(0))
        );
        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(game, 0, validSources, 0, validPlayers, null)
        );
    }

    @Test
    void immutableReturnsThisInstance() {
        ImmutableGameState state = ImmutableGameState.initial(game2Players());
        assertSame(state, state.immutable(), "La méthode immutable() doit retourner 'this' pour éviter des copies inutiles");
    }

    @Test
    void isRoundOverReturnsTrueOnlyWhenNoUniqueSourcesLeft() {
        Game game = game2Players();
        ImmutableIntArray emptySources = ImmutableIntArray.copyOf(new int[6]);
        ImmutableIntArray emptyPlayers = ImmutableIntArray.copyOf(new int[8]);

        // Cas 1 : Des sources uniques existent (la manche tourne)
        // 0b101 signifie que les sources d'index 0 et 2 sont uniques/disponibles
        ImmutableGameState statePlaying = new ImmutableGameState(
                game, 0, emptySources, 0b101, emptyPlayers, PlayerId.ALL.get(0));
        assertFalse(statePlaying.isRoundOver(), "La manche n'est pas finie si l'ensemble des sources uniques n'est pas vide");

        // Cas 2 : L'ensemble des sources uniques est vide
        ImmutableGameState stateOver = new ImmutableGameState(
                game, 0, emptySources, PkIntSet32.EMPTY, emptyPlayers, PlayerId.ALL.get(0));
        assertTrue(stateOver.isRoundOver(), "La manche est finie si PkIntSet32.EMPTY");
    }

    @Test
    void isGameOverReturnsFalseIfRoundIsNotOver() {
        Game game = game2Players();

        int[] players = new int[8];
        // On force un mur plein pour P1, MAIS la manche n'est pas finie
        // 0b11111 représente une ligne pleine dans PkWall (à adapter selon ton bit-twiddling si différent)
        PkPlayerStates.setPkWall(players, PlayerId.ALL.get(0), 0b11111);

        ImmutableGameState state = new ImmutableGameState(
                game, 0, ImmutableIntArray.copyOf(new int[6]), 0b1, // 0b1 = source unique dispo = manche en cours
                ImmutableIntArray.copyOf(players), PlayerId.ALL.get(0));

        assertFalse(state.isGameOver(), "La partie ne peut pas être finie si la manche est encore en cours, même avec un mur plein");
    }

    @Test
    void isGameOverReturnsTrueIfRoundOverAndWallHasFullRow() {
        Game game = game2Players();
        int[] players = new int[8];

        // On met une ligne pleine à P2 (pour s'assurer que ça boucle bien sur tous les joueurs)
        // Ligne du haut pleine par exemple
        int fullRowWall = 0b00000_00000_00000_00000_11111;
        PkPlayerStates.setPkWall(players, PlayerId.ALL.get(1), fullRowWall);

        ImmutableGameState state = new ImmutableGameState(
                game, 0, ImmutableIntArray.copyOf(new int[6]), PkIntSet32.EMPTY, // Manche finie
                ImmutableIntArray.copyOf(players), PlayerId.ALL.get(0));

        assertTrue(state.isGameOver(), "La partie doit être finie (manche finie + ligne pleine trouvée)");
    }

    @Test
    void pkDiscardedTilesIsZeroWhenAllTilesAreInBagOrOnBoard() {
        ImmutableGameState initialState = ImmutableGameState.initial(game2Players());

        // À l'état initial, FULL_COLORED est dans le sac et 1 marqueur est au centre.
        // Rien n'a encore été défaussé.
        int discarded = initialState.pkDiscardedTiles();
        assertEquals(PkTileSet.EMPTY, discarded, "À l'état initial, la défausse doit être vide");
    }


    // Helper : Génère une partie valide à 4 joueurs (9 fabriques + 1 centre = 10 sources)
    private Game game4Players() {
        return new Game(List.of(
                new Game.PlayerDescription(PlayerId.ALL.get(0), "Alice", Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.ALL.get(1), "Bob", Game.PlayerDescription.PlayerKind.AI),
                new Game.PlayerDescription(PlayerId.ALL.get(2), "Charlie", Game.PlayerDescription.PlayerKind.AI),
                new Game.PlayerDescription(PlayerId.ALL.get(3), "Diana", Game.PlayerDescription.PlayerKind.AI)
        ));
    }

    @Test
    void constructorThrowsNuullPointerExceptionOnNullArguments() {
        Game game = game2Players();
        ImmutableIntArray validSources = ImmutableIntArray.copyOf(new int[6]);
        ImmutableIntArray validPlayers = ImmutableIntArray.copyOf(new int[8]);

        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(null, 0, validSources, 0, validPlayers, PlayerId.ALL.get(0))
        );
        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(game, 0, null, 0, validPlayers, PlayerId.ALL.get(0))
        );
        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(game, 0, validSources, 0, null, PlayerId.ALL.get(0))
        );
        assertThrows(NullPointerException.class, () ->
                new ImmutableGameState(game, 0, validSources, 0, validPlayers, null)
        );
    }

    @Test
    void immutableReturnnsThisInstance() {
        ImmutableGameState state = ImmutableGameState.initial(game2Players());
        assertSame(state, state.immutable(), "La méthode immutable() doit retourner l'instance courante");
    }

    @Test
    void initialStateIsCorrectlyConstructed() {
        ImmutableGameState state = ImmutableGameState.initial(game2Players());

        // Vérification du sac : FULL_COLORED (20 de chaque, aucun marqueur)
        assertEquals(PkTileSet.FULL_COLORED, state.pkTileBag());

        // Vérification du centre (index 0) : uniquement le marqueur
        int expectedCenter = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        assertEquals(expectedCenter, state.pkTileSources().get(0));

        // Vérification des fabriques : toutes vides
        for (int i = 1; i < state.pkTileSources().size(); i++) {
            assertEquals(0, state.pkTileSources().get(i), "La fabrique " + i + " doit être vide");
        }
    }

    @Test
    void isRounndOverReturnsTrueOnlyWhenNoUniqueSourcesLeft() {
        Game game = game2Players();
        ImmutableIntArray emptySources = ImmutableIntArray.copyOf(new int[6]);
        ImmutableIntArray emptyPlayers = ImmutableIntArray.copyOf(new int[8]);

        // Cas 1 : La manche tourne (0b101 = sources 0 et 2 uniques et disponibles)
        ImmutableGameState statePlaying = new ImmutableGameState(
                game, 0, emptySources, 0b101, emptyPlayers, PlayerId.ALL.get(0));
        assertFalse(statePlaying.isRoundOver());

        // Cas 2 : La manche est finie (ensemble vide)
        ImmutableGameState stateOver = new ImmutableGameState(
                game, 0, emptySources, PkIntSet32.EMPTY, emptyPlayers, PlayerId.ALL.get(0));
        assertTrue(stateOver.isRoundOver());
    }

    @Test
    void isGameOverReturnsFalseIfRoundIsNotOverEvenWithFullWall() {
        Game game = game2Players();
        int[] players = new int[8];

        // On simule une ligne de mur pleine (valeur arbitraire dépendant de ton implémentation de l'étape 4)
        int fakeFullRow = 0b11111;
        PkPlayerStates.setPkWall(players, PlayerId.ALL.get(0), fakeFullRow);

        ImmutableGameState state = new ImmutableGameState(
                game, 0, ImmutableIntArray.copyOf(new int[6]), 0b1, // 0b1 = manche en cours !
                ImmutableIntArray.copyOf(players), PlayerId.ALL.get(0));

        assertFalse(state.isGameOver(), "La partie ne peut pas se terminer au milieu d'une manche");
    }

    @Test
    void pkDiscardedTilesIsCalculatedCorrectlyWhenTilesAreMissing() {
        Game game = game2Players();

        // 1. On crée un sac à moitié vide : 10 de chaque couleur (au lieu de 20)
        int halfBag = 0;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            // CORRECTION ICI : On crée un set de 10 tuiles, et on l'unit au sac
            int tenTilesOfColor = PkTileSet.of(10, color);
            halfBag = PkTileSet.union(halfBag, tenTilesOfColor);
        }

        // 2. Sources vides, sauf le centre qui a le marqueur (pour respecter le nombre de tuiles du jeu)
        int[] sources = new int[6];
        sources[0] = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);

        // 3. Les joueurs n'ont rien sur leurs plateaux
        int[] players = new int[8];

        ImmutableGameState state = new ImmutableGameState(
                game, halfBag, ImmutableIntArray.copyOf(sources), PkIntSet32.EMPTY,
                ImmutableIntArray.copyOf(players), PlayerId.ALL.get(0));

        // 4. Calcul de la défausse
        int discarded = state.pkDiscardedTiles();

        // Sur les 20 tuiles de chaque couleur qui existent dans le jeu, seules 10 sont dans le sac.
        // Les 10 autres DOIVENT être dans la défausse.
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            assertEquals(10, PkTileSet.countOf(discarded, color),
                    "Il manque 10 tuiles de type " + color + ", elles doivent être comptées dans la défausse");
        }

        // Le marqueur est au centre, il ne doit PAS être dans la défausse
        assertEquals(0, PkTileSet.countOf(discarded, TileKind.FIRST_PLAYER_MARKER));
    }

    @Test
    void pkDiscardedTilesWithTilesScatteredEverywhere() {
        Game game = game2Players();

        // Le sac ne contient que 5 tuiles A (A correspond à TileKind.Colored.A)
        int bag = PkTileSet.of(5, TileKind.A);

        // La fabrique 1 contient 2 tuiles B
        int[] sources = new int[6];
        sources[1] = PkTileSet.of(2, TileKind.B);

        // Note : Le centre n'a pas le marqueur ici ! (On va tester s'il se retrouve bien dans la défausse)

        int[] players = new int[8];

        ImmutableGameState state = new ImmutableGameState(
                game, bag, ImmutableIntArray.copyOf(sources), PkIntSet32.EMPTY,
                ImmutableIntArray.copyOf(players), PlayerId.ALL.get(0));

        int discarded = state.pkDiscardedTiles();

        // Sorte A : 20 existantes - 5 dans le sac = 15 défaussées
        assertEquals(15, PkTileSet.countOf(discarded, TileKind.A));

        // Sorte B : 20 existantes - 2 dans une fabrique = 18 défaussées
        assertEquals(18, PkTileSet.countOf(discarded, TileKind.B));

        // Sorte C, D, E : aucune n'est sur le plateau -> toutes les 20 sont défaussées
        assertEquals(20, PkTileSet.countOf(discarded, TileKind.C));
        assertEquals(20, PkTileSet.countOf(discarded, TileKind.D));
        assertEquals(20, PkTileSet.countOf(discarded, TileKind.E));

        // Marqueur : n'est nulle part sur le plateau -> défaussé (1)
        assertEquals(1, PkTileSet.countOf(discarded, TileKind.FIRST_PLAYER_MARKER));
    }
}
