package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileDestination;
import ch.epfl.ajul.TileKind;

public final class PkWall {

    public static int EMPTY = 0;
    public static int WALL_WIDTH = 5;
    public static int WALL_HEIGHT = 5;

    public static int indexOf(TileDestination.Pattern line, TileKind.Colored color){
        return column(line,color) + line.index()*WALL_WIDTH ;
    }

    public static int column(TileDestination.Pattern line, TileKind.Colored color){
        return ((line.index() + color.index()) % WALL_WIDTH);
    }

    public static TileKind.Colored colorAt(TileDestination.Pattern line, int column){
        return TileKind.Colored.ALL.get((line.index()*4 + column)% WALL_HEIGHT);
    }

    public static int withTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color){
        return pkWall | ( 1 << (indexOf(line,color)));
    }

    public static boolean hasTileAt(int pkWall, TileDestination.Pattern line, TileKind.Colored color){
        return ( (pkWall >> (indexOf(line,color)) & 1) == 1);
    }

    public static int hGroupSize(int pkWall, TileDestination.Pattern line, TileKind.Colored color) {
        int colorIndex = indexOf(line,color);
        int hGroupSize = 0;
        for (int i = line.index()*WALL_WIDTH ; i < column(line, color); ++i ){
            if ( hasTileAt(pkWall, line, colorAt(line, i))){
                ++hGroupSize;
            }
            else {
                hGroupSize= 0;
            }
        }
        int hgroupsizeleft = hGroupSize;
        for (int i = column(line, color) +1 ; i < (line.index()+1)*WALL_WIDTH ; ++i) {
            if ( hasTileAt(pkWall, line, colorAt(line, i))){
                ++hGroupSize;
            }
            else {
                hGroupSize = hgroupsizeleft;
            }
        }
        return hGroupSize;
    }


}
