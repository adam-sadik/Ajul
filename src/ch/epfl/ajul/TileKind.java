package ch.epfl.ajul;

import java.util.List;
import java.util.random.RandomGenerator;

/// Représente une sorte de tuile du jeu Ajul (tuiles colorées ou marqueur de premier joueur).
///
/// @author Adam Ghali SADIK (412029)
public sealed interface TileKind {

    /// La tuile colorée de type A.
    TileKind A = Colored.A;
    /// La tuile colorée de type B.
    TileKind B = Colored.B;
    /// La tuile colorée de type C.
    TileKind C = Colored.C;
    /// La tuile colorée de type D.
    TileKind D = Colored.D;
    /// La tuile colorée de type E.
    TileKind E = Colored.E;
    /// Le marqueur de premier joueur.
    TileKind FIRST_PLAYER_MARKER = FirstPlayerMarker.FIRST_PLAYER_MARKER;

    /// Liste immuable de toutes les sortes de tuiles dans l'ordre (A à E, puis marqueur).
    List<TileKind> ALL = List.of(A, B, C, D, E, FIRST_PLAYER_MARKER);
    /// Nombre total de sortes de tuiles (6).
    int COUNT = ALL.size();

    /// Retourne l'index de la sorte de tuile.
    /// @return l'index de la tuile (0-4 pour les couleurs, 5 pour le marqueur)
    int index();

    /// Retourne le nombre de tuiles de cette sorte existant dans le jeu.
    /// @return le nombre total de tuiles de cette sorte
    int tilesCount();

    /// Représente les cinq sortes de tuiles colorées.
    ///
    /// @author Adam Ghali SADIK (412029)
    enum Colored implements TileKind {
        A, B, C, D, E;

        /// Liste immuable de toutes les tuiles colorées.
        public static final List<Colored> ALL = List.of(values());
        /// Nombre de types de tuiles colorées (5).
        public static final int COUNT = ALL.size();

        @Override
        public int index() {
            return ordinal();
        }

        @Override
        public int tilesCount() {
            return 20;
        }

        /// Mélange aléatoirement un tableau de tuiles colorées.
        ///
        /// @param tiles
        ///        le tableau à mélanger
        /// @param randomGenerator
        ///        le générateur de nombres aléatoires
        public static void shuffle(Colored[] tiles, RandomGenerator randomGenerator) {
            for (int i = 0; i <= tiles.length - 2; ++i) {
                int j = randomGenerator.nextInt(i, tiles.length);
                Colored temp = tiles[i];
                tiles[i] = tiles[j];
                tiles[j] = temp;
            }
        }
    }

    /// Représente l'unique marqueur de premier joueur.
    ///
    /// @author Adam Ghali SADIK (412029)
    enum FirstPlayerMarker implements TileKind {
        FIRST_PLAYER_MARKER;

        @Override
        public int index() {
            return 5;
        }

        @Override
        public int tilesCount() {
            return 1;
        }
    }
}