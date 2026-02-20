package ch.epfl.ajul;

import java.util.List;

public sealed interface TileSource {

    TileSource CENTER_AREA = CenterArea.CENTER_AREA;
    TileSource FACTORY_1 = Factory.FACTORY_1;
    TileSource FACTORY_2 = Factory.FACTORY_2;
    TileSource FACTORY_3 = Factory.FACTORY_3;
    TileSource FACTORY_4 = Factory.FACTORY_4;
    TileSource FACTORY_5 = Factory.FACTORY_5;
    TileSource FACTORY_6 = Factory.FACTORY_6;
    TileSource FACTORY_7 = Factory.FACTORY_7;
    TileSource FACTORY_8 = Factory.FACTORY_8;
    TileSource FACTORY_9 = Factory.FACTORY_9;

    List<TileSource> ALL = List.of(CENTER_AREA, FACTORY_1, FACTORY_2, FACTORY_3, FACTORY_4, FACTORY_5, FACTORY_6, FACTORY_7, FACTORY_8, FACTORY_9);
    int COUNT = ALL.size();

    int index();

    enum CenterArea implements TileSource {
        CENTER_AREA;

        @Override
        public int index() {
            return 0;
        }
    }

    enum Factory implements TileSource {
        FACTORY_1, FACTORY_2, FACTORY_3, FACTORY_4, FACTORY_5, FACTORY_6, FACTORY_7, FACTORY_8, FACTORY_9;

        public static final int TILES_PER_FACTORY = 4;
        public static final List<TileSource.Factory> ALL = List.of(values());
        public static final int COUNT = ALL.size();

        @Override
        public int index() {
            return ordinal() + 1;
        }
    }
}