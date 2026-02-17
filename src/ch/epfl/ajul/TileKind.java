package ch.epfl.ajul;

import java.util.List;
import java.util.random.RandomGenerator;

public interface TileKind {
    public final static TileKind A;
    public final static TileKind B;
    public final static TileKind C;
    public final static TileKind D;
    public final static TileKind E;
    public final static TileKind FIRST_PLAYER_MAKER;
    List<TileKind> ALL = List.of(A,B,C,D,E,FIRST_PLAYER_MAKER);
    int COUNT = 6;
    public abstract int index();
    public abstract int tilesCount();
    enum Colored implements TileKind{
        A,B,C,D,E;
        int COUNT = 5;
        public static void shuffle(Colored[] tiles, RandomGenerator randomGenerator){

        }
    }
    enum FirstPlayerMarker implements TileKind{

    }
}
