package ch.epfl.ajul;

import java.util.List;

public sealed interface TileDestination {

    TileDestination PATTERN_1 = Pattern.PATTERN_1;
    TileDestination PATTERN_2 = Pattern.PATTERN_2;
    TileDestination PATTERN_3 = Pattern.PATTERN_3;
    TileDestination PATTERN_4 = Pattern.PATTERN_4;
    TileDestination PATTERN_5 = Pattern.PATTERN_5;
    TileDestination FLOOR = Floor.FLOOR;

    List<TileDestination> ALL = List.of(PATTERN_1, PATTERN_2, PATTERN_3, PATTERN_4, PATTERN_5, FLOOR);
    int COUNT = ALL.size();

    int index();
    int capacity();

    enum Pattern implements TileDestination {
        PATTERN_1, PATTERN_2, PATTERN_3, PATTERN_4, PATTERN_5;

        public static final List<TileDestination.Pattern> ALL = List.of(values());
        public static final int COUNT = ALL.size();

        @Override
        public int index() {
            return ordinal();
        }

        @Override
        public int capacity() {
            return index() + 1;
        }
    }

    enum Floor implements TileDestination {
        FLOOR;

        @Override
        public int index() {
            return 5;
        }

        @Override
        public int capacity() {
            return 7;
        }
    }
}