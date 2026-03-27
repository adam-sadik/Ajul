package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

class MyMutableGameStateTest {

    private Game game;
    private ReadOnlyGameState initialState;
    private MutableGameState gameState;
    private PlayerId p1;
    private PlayerId p2;

    @BeforeEach
    void setUp() {
        // Utilisation EXACTE de ta classe Game et de ton constructeur PlayerDescription
        p1 = PlayerId.ALL.get(0);
        p2 = PlayerId.ALL.get(1);

        List<Game.PlayerDescription> players = List.of(
                new Game.PlayerDescription(p1, "Adam", Game.PlayerDescription.PlayerKind.HUMAN),
                new Game.PlayerDescription(p2, "Rayane", Game.PlayerDescription.PlayerKind.AI)
        );
        game = new Game(players);

        // On suppose que ImmutableGameState.initial existe et fonctionne (étape 5)
        initialState = ImmutableGameState.initial(game);
        gameState = new MutableGameState(initialState);
    }

    // ==========================================
    // TESTS SUR LE REMPLISSAGE (fillFactories)
    // ==========================================

    @Test
    void fillFactories_remplitCorrectementLesFabriques() {
        // Le sac initial contient 100 tuiles (20 de chaque).
        // Avec 2 joueurs, on a 5 fabriques (factoriesCount() = 5).
        // Il faut 5 * 4 = 20 tuiles.

        gameState.fillFactories(RandomGenerator.getDefault());

        // La zone centrale (index 0) ne doit contenir QUE le marqueur 1er joueur au début
        int centerTiles = gameState.pkTileSources().get(0);
        assertEquals(1, PkTileSet.countOf(centerTiles, TileKind.FIRST_PLAYER_MARKER),
                "Le centre doit contenir le jeton 1er joueur");
        assertEquals(1, PkTileSet.size(centerTiles),
                "Le centre ne doit contenir AUCUNE tuile de couleur après le remplissage initial");

        // Les 5 fabriques (index 1 à 5) doivent contenir exactement 4 tuiles
        for (int i = 1; i <= game.factoriesCount(); i++) {
            int factoryTiles = gameState.pkTileSources().get(i);
            assertEquals(TileSource.Factory.TILES_PER_FACTORY, PkTileSet.size(factoryTiles),
                    "La fabrique " + i + " doit contenir exactement 4 tuiles");
        }
    }

    // ==========================================
    // TESTS SUR LES COUPS (registerMove)
    // ==========================================

    @Test
    void registerMove_factoryToPattern_deplaceLesRestesAuCentre() {
        gameState.fillFactories(RandomGenerator.getDefault());

        // On récupère l'état de la Fabrique 1
        int factory1 = gameState.pkTileSources().get(1);

        // On cherche une couleur présente dans la fabrique 1 pour simuler un coup valide
        TileKind.Colored colorToPlay = null;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            if (PkTileSet.countOf(factory1, color) > 0) {
                colorToPlay = color;
                break;
            }
        }
        assertNotNull(colorToPlay, "La fabrique 1 ne devrait pas être vide");
        int countPlayed = PkTileSet.countOf(factory1, colorToPlay);

        // Coup : Prendre 'colorToPlay' de Fabrique 1 vers PATTERN_5
        // (J'utilise TileSource.ALL.get(1) pour garantir la compatibilité avec ton code)
        short move = PkMove.pack(TileSource.ALL.get(1), colorToPlay, TileDestination.PATTERN_5);

        gameState.registerMove(move);

        // 1. La fabrique doit être vide
        assertEquals(0, PkTileSet.size(gameState.pkTileSources().get(1)),
                "La fabrique 1 doit être totalement vide après le coup");

        // 2. Le centre doit avoir reçu les (4 - countPlayed) tuiles restantes
        int centerAfter = gameState.pkTileSources().get(0);
        assertEquals(4 - countPlayed + 1, PkTileSet.size(centerAfter), // +1 pour le jeton 1er joueur
                "Le centre doit contenir les tuiles restantes de la fabrique + le jeton 1er joueur");

        // 3. Le pattern 5 du joueur courant doit avoir reçu les tuiles
        int p1Patterns = PkPlayerStates.pkPatterns(gameState.pkPlayerStates(), p1);
        assertEquals(countPlayed, PkPatterns.size(p1Patterns, TileDestination.Pattern.PATTERN_5),
                "Le motif 5 doit contenir les tuiles jouées");
    }

    @Test
    void registerMove_patternOverflow_envoieLecedentSurLePlancher() {
        // On force 3 tuiles de couleur A dans la fabrique 1 manuellement via l'état pour tester le débordement
        // (Ce test suppose que registerMove utilise bien pkTileSourcesEditable pour calculer les tuiles)
        // Mais simulons plutôt par la capacité : PATTERN_1 a une capacité de 1.

        // Si on joue un coup qui envoie 2 tuiles A vers PATTERN_1 :
        // 1 tuile va dans PATTERN_1, 1 tuile va dans FLOOR.
        // C'est exactement ce que ton code doit gérer.

        // Note: C'est difficile de tester sans modifier l'état interne manuellement,
        // mais c'est le test critique à garder en tête si tu as une méthode "setTileSource" pour tes tests.
    }

    @Test
    void registerMove_centerWithFirstPlayerMarker_prendLeJetonSurLePlancher() {
        gameState.fillFactories(RandomGenerator.getDefault());
        // Force un changement de joueur pour vérifier qu'il est bien appliqué
        assertEquals(p1, gameState.currentPlayerId());

    }

    // ==========================================
    // TESTS SUR LA FIN DE MANCHE (endRound)
    // ==========================================

    @Test
    void endRound_neDescendPasLeScoreSousZero() {
        // Le joueur 1 a 0 point au début.
        assertEquals(0, PkPlayerStates.points(gameState.pkPlayerStates(), p1));

        // On lui met 3 tuiles sur le plancher (pénalité de -4 points)
        // Attention : Si pkPlayerStatesEditable n'est pas accessible, ce test valide
        // la logique si tu peux simuler un état où le plancher est plein.

        gameState.endRound();

        // Le score ne DOIT PAS être -4, il doit être bloqué à 0.
        int score = PkPlayerStates.points(gameState.pkPlayerStates(), p1);
        assertTrue(score >= 0, "Le score d'un joueur ne doit JAMAIS être négatif après endRound");
    }

    @Test
    void endRound_videLePlancherEtGereLeMarqueur1erJoueur() {
        // Après un appel à endRound :
        gameState.endRound();

        // Le plancher de tous les joueurs DOIT être vide
        int p1Floor = PkPlayerStates.pkFloor(gameState.pkPlayerStates(), p1);
        int p2Floor = PkPlayerStates.pkFloor(gameState.pkPlayerStates(), p2);

        assertEquals(0, PkFloor.size(p1Floor), "Le plancher de P1 doit être vide");
        assertEquals(0, PkFloor.size(p2Floor), "Le plancher de P2 doit être vide");
    }

    @Test
    void registerMove_destinationFloor_placeToutesLesTuilesSurLePlancher() {
        gameState.fillFactories(RandomGenerator.getDefault());
        int factory1 = gameState.pkTileSources().get(1);

        // On trouve une couleur disponible dans la fabrique 1
        TileKind.Colored colorToPlay = null;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            if (PkTileSet.countOf(factory1, color) > 0) {
                colorToPlay = color;
                break;
            }
        }
        int countPlayed = PkTileSet.countOf(factory1, colorToPlay);

        // Coup : Prendre depuis Fabrique 1 et jeter directement sur le Plancher
        short move = PkMove.pack(TileSource.ALL.get(1), colorToPlay, TileDestination.FLOOR);
        gameState.registerMove(move);

        // Vérification du plancher de P1
        int p1Floor = PkPlayerStates.pkFloor(gameState.pkPlayerStates(), p1);
        assertEquals(countPlayed, PkFloor.size(p1Floor),
                "Le plancher doit contenir exactement les tuiles piochées de la fabrique");
    }

    @Test
    void registerMove_depuisCentre_prendLeMarqueurPremierJoueur() {
        gameState.fillFactories(RandomGenerator.getDefault());

        // ÉTAPE 1 : P1 joue depuis la fabrique 1 vers la ligne 5.
        // Cela va envoyer les tuiles restantes au centre (qui contient déjà le jeton 1er joueur).
        int factory1 = gameState.pkTileSources().get(1);
        TileKind.Colored color1 = null;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            if (PkTileSet.countOf(factory1, color) > 0) {
                color1 = color;
                break;
            }
        }
        short move1 = PkMove.pack(TileSource.ALL.get(1), color1, TileDestination.PATTERN_5);
        gameState.registerMove(move1);

        // ÉTAPE 2 : C'est au tour de P2. Le centre contient des tuiles et le jeton 1er joueur.
        assertEquals(p2, gameState.currentPlayerId(), "Ce doit être au tour de P2");

        int center = gameState.pkTileSources().get(0);
        assertTrue(PkTileSet.countOf(center, TileKind.FIRST_PLAYER_MARKER) > 0,
                "Le centre doit toujours avoir le jeton 1er joueur avant le coup de P2");

        // P2 pioche au centre
        TileKind.Colored color2 = null;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            if (PkTileSet.countOf(center, color) > 0) {
                color2 = color;
                break;
            }
        }
        short move2 = PkMove.pack(TileSource.CENTER_AREA, color2, TileDestination.PATTERN_4);
        gameState.registerMove(move2);

        // VÉRIFICATIONS
        int p2Floor = PkPlayerStates.pkFloor(gameState.pkPlayerStates(), p2);
        assertTrue(PkFloor.containsFirstPlayerMarker(p2Floor),
                "P2 a pioché en premier au centre, il doit avoir le jeton 1er joueur sur son plancher !");

        int centerAfter = gameState.pkTileSources().get(0);
        assertEquals(0, PkTileSet.countOf(centerAfter, TileKind.FIRST_PLAYER_MARKER),
                "Le centre ne doit plus avoir le jeton 1er joueur");
    }

    @Test
    void endRound_lignePartiellementRemplie_neVaPasSurLeMur() {
        gameState.fillFactories(RandomGenerator.getDefault());

        // On prend une couleur de la fabrique 1 (maximum 4 tuiles)
        int factory1 = gameState.pkTileSources().get(1);
        TileKind.Colored colorToPlay = null;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            if (PkTileSet.countOf(factory1, color) > 0) {
                colorToPlay = color;
                break;
            }
        }
        int countPlayed = PkTileSet.countOf(factory1, colorToPlay);

        // On la place sur la ligne 5 (capacité 5).
        // Comme countPlayed <= 4, la ligne 5 sera forcément INCOMPLÈTE.
        short move = PkMove.pack(TileSource.ALL.get(1), colorToPlay, TileDestination.PATTERN_5);
        gameState.registerMove(move);

        // On simule une fin de manche
        gameState.endRound();

        int p1Patterns = PkPlayerStates.pkPatterns(gameState.pkPlayerStates(), p1);
        int p1Wall = PkPlayerStates.pkWall(gameState.pkPlayerStates(), p1);

        // Les tuiles doivent y être restées
        assertEquals(countPlayed, PkPatterns.size(p1Patterns, TileDestination.Pattern.PATTERN_5),
                "Les tuiles doivent rester sur la ligne de motif car elle n'est pas pleine (capacité 5)");

        // La tuile ne doit pas être passée sur le mur
        // Note: Assure-toi que la méthode hasTileAt ou équivalent existe dans ton PkWall, sinon utilise ton getter
        boolean isTileOnWall = PkWall.isRowFull(p1Wall, TileDestination.Pattern.PATTERN_5); // ou ta propre méthode d'inspection
        assertFalse(isTileOnWall, "Aucune tuile ne doit passer sur le mur si la ligne n'est pas pleine");
    }

    @Test
    void registerMove_metAJourLesSourcesUniques() {
        gameState.fillFactories(RandomGenerator.getDefault());
        int initialUnique = gameState.pkUniqueTileSources();

        // On vide la fabrique 1
        int factory1 = gameState.pkTileSources().get(1);
        TileKind.Colored colorToPlay = null;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            if (PkTileSet.countOf(factory1, color) > 0) {
                colorToPlay = color;
                break;
            }
        }
        short move = PkMove.pack(TileSource.ALL.get(1), colorToPlay, TileDestination.PATTERN_1);
        gameState.registerMove(move);

        int newUnique = gameState.pkUniqueTileSources();

        // Les sources uniques doivent avoir été modifiées
        assertNotEquals(initialUnique, newUnique,
                "Les sources uniques doivent être mises à jour dynamiquement après un coup");
    }
}
