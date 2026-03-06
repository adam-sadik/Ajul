package ch.epfl.ajul.gamestate.packed;

public final class PkIntSet32 {

    public static final int EMPTY = 0;

    public static boolean contains (int pkIntSet32, int i) {
        assert i >= 0 && i < Integer.SIZE;
        return ((pkIntSet32 >>> i) & 1) == 1;
    }

    public static boolean containsAll (int pkIntSet32a, int pkIntSet32b){
        return (pkIntSet32a & pkIntSet32b) == pkIntSet32b;
    }

    public static int add (int pkIntSet32, int i){
        assert i >= 0 && i < Integer.SIZE;
        return pkIntSet32 | (1 << i);
    }

    public static int remove (int pkIntSet32, int i){
        assert i >= 0 && i < Integer.SIZE;
        return pkIntSet32 & ~(1 << i);
    }


}
