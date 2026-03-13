package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.*;
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

    default int pkDiscardedTiles(){

        int sources = PkTileSet.EMPTY ;
        int wall = PkTileSet.EMPTY;
        int floor = PkTileSet.EMPTY;
        int patterns = PkTileSet.EMPTY;
        int patternsAndFloor;
        int unionBagAndSources;
        int unionWallAndPatternsAndFloor;
        int total;

        for (int i = 0; i < pkTileSources().size(); ++i) {
            sources = PkTileSet.union(sources,pkTileSources().get(i) );
        }

        unionBagAndSources = PkTileSet.union(pkTileBag(), sources );

        for (int i = 0; i < playerIds().size(); ++i) {
            floor = PkTileSet.union(floor, PkPlayerStates.pkPatterns(pkPlayerStates(), PlayerId.ALL.get(i)));
        }

        for (int i = 0; i < playerIds().size(); i++) {
            patterns = PkTileSet.union(patterns,PkPlayerStates.pkFloor(pkPlayerStates(), PlayerId.ALL.get(i)));
        }

        patternsAndFloor = PkTileSet.union ( floor, patterns);

        for (int i = 0; i < playerIds().size() ; i++) {
            wall = PkTileSet.union(wall, PkPlayerStates.pkWall(pkPlayerStates(), PlayerId.ALL.get(i)));
        }

        unionWallAndPatternsAndFloor = PkTileSet.union(wall,patternsAndFloor);
        total = PkTileSet.union(unionBagAndSources, unionWallAndPatternsAndFloor);

        return PkTileSet.difference(PkTileSet.FULL,total);
    }

    default int validMoves(short[] destination) {
        assert destination.length >= Move.MAX_MOVES;
        int index = currentPlayerId().ordinal();
        int i = 0;
        for (TileSource source : TileSource.ALL) {
            for (TileKind.Colored color : TileKind.Colored.ALL) {
                for (TileDestination dest : TileDestination.ALL) {
                    boolean isPatternFull = PkPatterns.isFull(PkPlayerStates.pkPatterns(pkPlayerStates(), currentPlayerId()), (TileDestination.Pattern) dest);

                    if ((dest instanceof TileDestination.Pattern) && !isPatternFull) {
                        if (color == PkPatterns.color(PkPlayerStates.pkPatterns(pkPlayerStates(), currentPlayerId()), (TileDestination.Pattern) dest))
                            destination[i] = PkMove.pack(source, color, dest);
                        ++i;
                    } else if (dest instanceof TileDestination.Floor) {
                        destination[i] = PkMove.pack(source, color, dest);
                        ++i;
                    }
                }
            }
        }
        return i;
    }





}
