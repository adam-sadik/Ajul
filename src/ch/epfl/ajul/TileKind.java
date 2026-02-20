package ch.epfl.ajul;

import java.util.List;
import java.util.random.RandomGenerator;

public sealed interface TileKind {

    TileKind A = Colored.A;
    TileKind B = Colored.B;
    TileKind C = Colored.C;
    TileKind D = Colored.D;
    TileKind E = Colored.E;
    TileKind FIRST_PLAYER_MARKER = FirstPlayerMarker.FIRST_PLAYER_MARKER;

    List<TileKind> ALL = List.of(A, B, C, D, E, FIRST_PLAYER_MARKER);
    int COUNT = ALL.size();

    int index();
    int tilesCount();

    enum Colored implements TileKind {
        A, B, C, D, E;

        public static final List<Colored> ALL = List.of(values());
        public static final int COUNT = ALL.size();

        @Override
        public int index() {
            return ordinal();
        }

        @Override
        public int tilesCount() {
            return 20;
        }

        public static void shuffle(Colored[] tiles, RandomGenerator randomGenerator) {
            for (int i = 0; i <= tiles.length - 2; ++i) {
                int j = randomGenerator.nextInt(i, tiles.length);
                Colored temp = tiles[i];
                tiles[i] = tiles[j];
                tiles[j] = temp;
            }
        }
    }

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