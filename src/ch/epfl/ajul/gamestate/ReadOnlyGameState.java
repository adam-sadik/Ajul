package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.gamestate.packed.PkIntSet32;
import ch.epfl.ajul.gamestate.packed.PkPlayerStates;
import ch.epfl.ajul.gamestate.packed.PkWall;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

import java.util.List;

public interface ReadOnlyGameState {

    Game game();

    int pkTileBag();

    ReadOnlyIntArray pkTileSources();

    int pkUniqueTileSources();

    ReadOnlyIntArray pkPlayerStates();

    PlayerId currentPlayerId();

    default ImmutableGameState immutable() {
        return new ImmutableGameState( game(),
                pkTileBag(),
                (ImmutableIntArray) pkTileSources(),
                pkUniqueTileSources(),
                (ImmutableIntArray) pkPlayerStates(),
                currentPlayerId() );
    }

    default List<PlayerId> playerIds(){
        return game().playerIds();
    }

    default boolean isRoundOver() {
        return pkUniqueTileSources() == PkIntSet32.EMPTY;
    }

    default boolean isGameOver() {
        boolean isRowFull = false;
        for (int i = 0; i < playerIds().size() ; ++i) {
            if(PkWall.hasFullRow(PkPlayerStates.pkWall(pkPlayerStates(),PlayerId.ALL.get(i)))) {
                isRowFull = true;
                break;
            }
        }
        return isRoundOver() && isRowFull;
    }

//    default int pkDiscardedTiles(){
//
//        int sources = PkTileSet.EMPTY ;
//        for (int i = 0; i < pkTileSources().size(); ++i) {
//            sources = PkTileSet.union(sources,pkTileSources().get(i) );
//        }
//        int unionBagAndPatterns = PkTileSet.union(pkTileBag(), sources );
//
//        int patternsAndFloor = PkTileSet.EMPTY;
//        int wall = PkTileSet.EMPTY;
//
//        int unionPatternsAndFloor;
//
//        for (int i = 0; i < playerIds().size(); ++i) {
//            patternsAndFloor = PkTileSet.union(PkPlayerStates.pkFloor(pkPlayerStates(), PlayerId.ALL.get(i)), PkPlayerStates.pkPatterns(pkPlayerStates(), PlayerId.ALL.get(i)));
//        }
//
//        return PkTileSet.difference(PkTileSet.FULL,PkTileSet.union(pkTileBag())
//    }




}
