package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.PkMove;
import ch.epfl.ajul.gamestate.packed.PkPlayerStates;
import ch.epfl.ajul.gamestate.packed.PkTileSet;
import ch.epfl.ajul.intarray.MutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

import java.awt.*;
import java.util.random.RandomGenerator;

public final class MutableGameState implements ReadOnlyGameState  {

    private final Game game;
    private int pkTileBag;
    private final ReadOnlyIntArray pkTileSources;
    private final int[] pkTileSourcesEditable;
    private final int pkUniqueTileSources;
    private final ReadOnlyIntArray pkPlayerStates;
    private final int[] pkPlayerStatesEditable;
    private final PlayerId currentPlayerId;
    private final PointsObserver pointsObserver;

    MutableGameState(ReadOnlyGameState initialState, PointsObserver pointsObserver) {
        this.pointsObserver = pointsObserver;
        game = initialState.game();
        pkTileBag = initialState.pkTileBag();
        pkTileSourcesEditable = initialState.pkTileSources().toArray();
        pkTileSources = MutableIntArray.wrapping(pkTileSourcesEditable);
        pkUniqueTileSources = initialState.pkUniqueTileSources();
        pkPlayerStatesEditable = initialState.pkPlayerStates().toArray();
        pkPlayerStates = MutableIntArray.wrapping(pkPlayerStatesEditable);
        currentPlayerId = initialState.currentPlayerId();
    }

    MutableGameState(ReadOnlyGameState initialState) {
        this(initialState, PointsObserver.EMPTY);
    }


    @Override
    public Game game() {
        return game;
    }

    @Override
    public int pkTileBag() {
        return pkTileBag;
    }

    @Override
    public ReadOnlyIntArray pkTileSources() {
        return pkTileSources;
    }

    @Override
    public int pkUniqueTileSources() {
        return pkUniqueTileSources;
    }

    @Override
    public ReadOnlyIntArray pkPlayerStates() { return pkPlayerStates; }

    @Override
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    public void fillFactories(RandomGenerator randomGenerator) {
       // assert isRoundOver();
        int totalNbOfTiles = 0;
        for (int i = 1; i < pkTileSources.size() ; i++) {
            totalNbOfTiles += PkTileSet.size(pkTileSources.get(i));
        }
        int necessaryNbTiles = (pkTileSources.size()-1) * TileSource.Factory.TILES_PER_FACTORY - totalNbOfTiles;

        TileKind.Colored [] factories = new TileKind.Colored[necessaryNbTiles];

        if (PkTileSet.size(pkTileBag) > necessaryNbTiles ){
            PkTileSet.sampleColoredInto(pkTileBag, factories, 0, randomGenerator);

            for (int i = 0; i < necessaryNbTiles; i++) {
                switch (factories[i]){
                    case TileKind.Colored.A : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.A);
                    case TileKind.Colored.B : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.B);
                    case TileKind.Colored.C : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.C);
                    case TileKind.Colored.D : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.D);
                    case TileKind.Colored.E : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.E);
                }
            }
        }
        else {
            int setOfTilesAdded = pkTileBag;
            pkTileBag = pkDiscardedTiles();
            int necessaryDiscardedTiles = Math.min(PkTileSet.size(pkDiscardedTiles()), necessaryNbTiles - PkTileSet.size(setOfTilesAdded));

            TileKind.Colored [] pkDiscardedTilesTable = new TileKind.Colored [PkTileSet.size(pkDiscardedTiles())];
            PkTileSet.copyColoredInto(pkDiscardedTiles(), pkDiscardedTilesTable);
            TileKind.Colored.shuffle(pkDiscardedTilesTable, randomGenerator);

            for (int i = 0; i < necessaryDiscardedTiles ; i++) {
                setOfTilesAdded = PkTileSet.add(setOfTilesAdded, pkDiscardedTilesTable[i]);
                    switch (pkDiscardedTilesTable[i]){
                        case TileKind.Colored.A : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.A);
                        case TileKind.Colored.B : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.B);
                        case TileKind.Colored.C : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.C);
                        case TileKind.Colored.D : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.D);
                        case TileKind.Colored.E : pkTileBag = PkTileSet.remove(pkTileBag,TileKind.E);
                    }
            }

            PkTileSet.copyColoredInto(setOfTilesAdded,factories);
            TileKind.Colored.shuffle(factories,randomGenerator);

            for (int i = 1; i < pkTileSources.size(); i++) {
                for (int j = 0; j < (TileSource.Factory.TILES_PER_FACTORY - PkTileSet.size(pkTileSources.get(i))) ; j++) {
                    pkTileSourcesEditable[i] =  PkTileSet.add(pkTileSourcesEditable[i], factories[i]);
                }
            }

        }

    }

    public void registerMove(short pkMove){

        if (PkMove.source(pkMove) instanceof TileSource.Factory ){
            PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, currentPlayerId, PkMove.destination(pkMove).index());
        }

    }


}
