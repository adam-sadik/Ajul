package ch.epfl.ajul;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.*;

public class MyGameTest {


    @Test
    void idPlayerDescriptionRecordException(){
        assertThrows(NullPointerException.class, () -> {
           new Game.PlayerDescription(null,"test", Game.PlayerDescription.PlayerKind.AI);
        });
    }
    @Test
    void nameDescriptionRecordException(){
        assertThrows(NullPointerException.class, () -> {
            new Game.PlayerDescription(PlayerId.P2,null, Game.PlayerDescription.PlayerKind.AI);
        });
    }


    @Test
    void playerKindDescriptionRecordException(){
        assertThrows(NullPointerException.class, () -> {
            new Game.PlayerDescription(PlayerId.P2,"null", null);
        });
    }

    @Test
    void constructorThrowsIfListIsNull() {
        assertThrows(AssertionError.class, () ->
                new Game(null));
    }

    @Test
    void constructorThrowsIfLessThanTwoPlayers() {
        List<Game.PlayerDescription> list =
                List.of(new Game.PlayerDescription(
                        PlayerId.P1,"A",
                        Game.PlayerDescription.PlayerKind.HUMAN));

        assertThrows(IllegalArgumentException.class,
                () -> new Game(list));
    }

    @Test
    void factoriesIsImmutable() {
        Game g = new Game(List.of(
                new Game.PlayerDescription(PlayerId.P1,"A",
                        Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.P2,"B",
                        Game.PlayerDescription.PlayerKind.AI)
        ));

        assertThrows(UnsupportedOperationException.class, () -> {
            g.factories().clear();
        });
    }

    @Test
    void tilesSourcesIsImmutable() {
        Game g = new Game(List.of(
                new Game.PlayerDescription(PlayerId.P1,"A",
                        Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.P2,"B",
                        Game.PlayerDescription.PlayerKind.AI)
        ));

        assertThrows(UnsupportedOperationException.class, () -> {
            g.tileSources().clear();
        });
    }

    @Test
    void playerIdsIsImmutable() {
        Game g = new Game(List.of(
                new Game.PlayerDescription(PlayerId.P1,"A",
                        Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(PlayerId.P2,"B",
                        Game.PlayerDescription.PlayerKind.AI)
        ));

        assertThrows(UnsupportedOperationException.class, () -> {
            g.playerIds().add(PlayerId.P3);
        });
    }

    // Méthode utilitaire pour créer des joueurs rapidement

    private Game.PlayerDescription createPlayer(int index) {

        return new Game.PlayerDescription(PlayerId.ALL.get(index), "Player " + index, Game.PlayerDescription.PlayerKind.HUMAN);

    }



    @Test

    void gameConstructorValidatesSizes() {

        // Test avec 1 joueur (trop peu)

        assertThrows(IllegalArgumentException.class, () -> {

            new Game(List.of(createPlayer(0)));

        });



        // Test avec 5 joueurs (trop)

        assertThrows(IllegalArgumentException.class, () -> {

            new Game(List.of(createPlayer(0), createPlayer(1), createPlayer(2), createPlayer(3), createPlayer(3)));

        });

    }



    @Test

    void gameConstructorValidatesOrderAndIds() {

        // P2 en premier, P1 en deuxième

        Game.PlayerDescription p1 = createPlayer(0);

        Game.PlayerDescription p2 = createPlayer(1);



        assertThrows(IllegalArgumentException.class, () -> {

            new Game(List.of(p2, p1));

        });

    }



    @Test

    void gameConstructorFailsOnNullArguments() {

        // PlayerDescription interdit les nulls

        assertThrows(NullPointerException.class, () -> {

            new Game.PlayerDescription(null, "Name", Game.PlayerDescription.PlayerKind.HUMAN);

        });

        assertThrows(NullPointerException.class, () -> {

            new Game.PlayerDescription(PlayerId.ALL.get(0), null, Game.PlayerDescription.PlayerKind.AI);

        });

    }



    @Test

    void playerIdsListIsPrefixOfAll() {

        Game game = new Game(List.of(createPlayer(0), createPlayer(1), createPlayer(2))); // 3 joueurs



        List<PlayerId> ids = game.playerIds();

        assertEquals(3, ids.size());

        assertEquals(PlayerId.ALL.get(0), ids.get(0));

        assertEquals(PlayerId.ALL.get(1), ids.get(1));

        assertEquals(PlayerId.ALL.get(2), ids.get(2));

    }



    @Test

    void mathematicsAndCountsAreCorrectFor2Players() {

        Game game = new Game(List.of(createPlayer(0), createPlayer(1))); // 2 joueurs



        assertEquals(2, game.playersCount());



        // 2n + 1 fabriques -> 5

        assertEquals(5, game.factoriesCount());

        assertEquals(5, game.factories().size());



        // 5 fabriques + 1 zone centrale -> 6

        assertEquals(6, game.tileSourcesCount());

        assertEquals(6, game.tileSources().size());



        // 3m + 1 au centre -> 3*5 + 1 -> 16

        assertEquals(16, game.centralAreaMaxSize());

    }



    @Test

    void mathematicsAndCountsAreCorrectFor4Players() {

        Game game = new Game(List.of(createPlayer(0), createPlayer(1), createPlayer(2), createPlayer(3))); // 4 joueurs



        assertEquals(4, game.playersCount());



        // 2n + 1 fabriques -> 9

        assertEquals(9, game.factoriesCount());

        assertEquals(9, game.factories().size());



        // 9 fabriques + 1 zone centrale -> 10

        assertEquals(10, game.tileSourcesCount());

        assertEquals(10, game.tileSources().size());



        // 3m + 1 au centre -> 3*9 + 1 -> 28

        assertEquals(28, game.centralAreaMaxSize());

    }



    @Test

    void returnedListsAreStrictlyImmutable() {

        Game game = new Game(List.of(createPlayer(0), createPlayer(1)));



        // Tentative d'ajout dans la liste des descriptions

        assertThrows(UnsupportedOperationException.class, () -> {

            game.playerDescriptions().add(createPlayer(2));

        });



        // Tentative de suppression dans la liste des IDs

        assertThrows(UnsupportedOperationException.class, () -> {

            game.playerIds().remove(0);

        });



        // Tentative de vider la liste des fabriques

        assertThrows(UnsupportedOperationException.class, () -> {

            game.factories().clear();

        });



        // Tentative d'altération de la liste des sources

        assertThrows(UnsupportedOperationException.class, () -> {

            game.tileSources().remove(0);

        });

    }



    @Test

    void constructorFailsIfPlayerIdsAreNotStrictPrefixOfAll() {



        // Cas 1 : Ne commence pas par P1 (Index 0). Ici on met P2 (1) et P3 (2).

        assertThrows(IllegalArgumentException.class, () -> {

            new Game(List.of(createPlayer(1), createPlayer(2)));

        });



        // Cas 2 : Saute un identifiant (Pas un préfixe continu). Ici P1 (0) et P3 (2).

        assertThrows(IllegalArgumentException.class, () -> {

            new Game(List.of(createPlayer(0), createPlayer(2)));

        });



        // Cas 3 : Identifiants présents mais dans le désordre. Ici P2 (1) puis P1 (0).

        assertThrows(IllegalArgumentException.class, () -> {

            new Game(List.of(createPlayer(1), createPlayer(0)));

        });



        // Vérification qu'un préfixe correct passe sans exception (P1, P2, P3)

        assertDoesNotThrow(() -> {

            new Game(List.of(createPlayer(0), createPlayer(1), createPlayer(2)));

        });

    }

    private final Game.PlayerDescription p1 = new Game.PlayerDescription(PlayerId.P1, "Alice", Game.PlayerDescription.PlayerKind.HUMAN);
        private final Game.PlayerDescription p2 = new Game.PlayerDescription(PlayerId.P2, "Bob", Game.PlayerDescription.PlayerKind.AI);
        private final Game.PlayerDescription p3 = new Game.PlayerDescription(PlayerId.P3, "Charlie", Game.PlayerDescription.PlayerKind.HUMAN);
        private final Game.PlayerDescription p4 = new Game.PlayerDescription(PlayerId.P4, "Diana", Game.PlayerDescription.PlayerKind.AI);

        @Test
        void constructorAcceptsValidPlayerCounts() {
            assertDoesNotThrow(() -> new Game(List.of(p1, p2)), "2 joueurs devrait être valide");
            assertDoesNotThrow(() -> new Game(List.of(p1, p2, p3)), "3 joueurs devrait être valide");
            assertDoesNotThrow(() -> new Game(List.of(p1, p2, p3, p4)), "4 joueurs devrait être valide");
        }

        @Test
        void constructorRejectsInvalidPlayerCounts() {
            assertThrows(IllegalArgumentException.class, () -> new Game(List.of())); // 0
            assertThrows(IllegalArgumentException.class, () -> new Game(List.of(p1))); // 1
            // Si un hypothétique P5 existait, on testerait 5 ici.
        }

        @Test
        void constructorRejectsWrongPlayerIdOrder() {
            // P2 à la place de P1
            assertThrows(IllegalArgumentException.class, () -> new Game(List.of(p2, p1)));
            // P1 puis P3 (saute P2)
            assertThrows(IllegalArgumentException.class, () -> new Game(List.of(p1, p3)));
        }

        @Test
        void playerDescriptionRejectsNulls() {
            assertThrows(NullPointerException.class, () -> new Game.PlayerDescription(null, "A", Game.PlayerDescription.PlayerKind.HUMAN));
            assertThrows(NullPointerException.class, () -> new Game.PlayerDescription(PlayerId.P1, null, Game.PlayerDescription.PlayerKind.HUMAN));
            assertThrows(NullPointerException.class, () -> new Game.PlayerDescription(PlayerId.P1, "A", null));
        }

        @Test
        void returnedListsAreUnmodifiable() {
            Game game = new Game(List.of(p1, p2));
            assertThrows(UnsupportedOperationException.class, () -> game.playerDescriptions().add(p3));
            assertThrows(UnsupportedOperationException.class, () -> game.playerIds().remove(0));
        }

        @Test
        void gameCalculatesCorrectMetricsFor2Players() {
            Game game = new Game(List.of(p1, p2));
            assertEquals(2, game.playersCount());
            assertEquals(5, game.factoriesCount()); // 2*2 + 1
            assertEquals(6, game.tileSourcesCount()); // 5 + 1
            assertEquals(16, game.centralAreaMaxSize()); // 3*5 + 1
        }

        @Test
        void gameCalculatesCorrectMetricsFor4Players() {
            Game game = new Game(List.of(p1, p2, p3, p4));
            assertEquals(4, game.playersCount());
            assertEquals(9, game.factoriesCount()); // 2*4 + 1
            assertEquals(10, game.tileSourcesCount()); // 9 + 1
            assertEquals(28, game.centralAreaMaxSize()); // 3*9 + 1
        }

    }

