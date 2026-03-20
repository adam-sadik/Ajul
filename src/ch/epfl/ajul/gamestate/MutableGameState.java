package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.*;
import ch.epfl.ajul.intarray.MutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

import java.awt.*;
import java.util.random.RandomGenerator;

public final class MutableGameState implements ReadOnlyGameState  {

    private final Game game;
    private int pkTileBag;
    private final ReadOnlyIntArray pkTileSources;
    private final int[] pkTileSourcesEditable;
    private int pkUniqueTileSources;
    private final ReadOnlyIntArray pkPlayerStates;
    private final int[] pkPlayerStatesEditable;
    private PlayerId currentPlayerId;
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
        TileSource source = PkMove.source(pkMove);
        TileKind.Colored color = PkMove.color(pkMove);
        TileDestination destination = PkMove.destination(pkMove);
        int countOfColor = PkTileSet.countOf(pkTileSources.get(source.index()),color);
        int pkPatterns = PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId);

// Ne Rajoute pas les tuiles dans la destination
        if (source instanceof TileSource.Factory) {
            //PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, currentPlayerId, PkTileSet.add(PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId), PkMove.color(pkMove)));
            for (int i = 0; i < TileKind.Colored.ALL.size(); i++) {
                TileKind.Colored colorI = TileKind.Colored.ALL.get(i);
                int nbOfColorITiles =  PkTileSet.countOf(pkTileSources.get(source.index()), colorI);
                while (nbOfColorITiles > 0
                        && colorI != color) {
                   pkTileSourcesEditable[0] = PkTileSet.add(pkTileSourcesEditable[0], colorI);
                   pkTileSourcesEditable[source.index()] = PkTileSet.remove(pkTileSourcesEditable[source.index()], colorI);
                   --nbOfColorITiles;
                }
            }
        }
        else if ( source instanceof TileSource.CenterArea && PkTileSet.countOf(pkTileSources.get(source.index()), TileKind.FIRST_PLAYER_MARKER) > 0) {
            PkPlayerStates.setPkFloor(pkPlayerStatesEditable, currentPlayerId,
                    PkFloor.withAddedTiles(PkPlayerStates.pkFloor(pkPlayerStates,currentPlayerId), PkTileSet.difference(PkTileSet.FULL,PkTileSet.FULL_COLORED)));
            pkTileSourcesEditable[0] = PkTileSet.remove(pkTileSourcesEditable[0], TileKind.FIRST_PLAYER_MARKER);
        }

        if ( destination instanceof TileDestination.Pattern && destination.capacity() > countOfColor) {
            for ( int i = 0 ; i < countOfColor ; ++i){
                if ( !PkPatterns.isFull(pkPatterns,(TileDestination.Pattern) destination)){
                    PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, currentPlayerId,
                            PkPatterns.withAddedTiles(PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId), (TileDestination.Pattern) destination, 1,  PkMove.color(pkMove)));
                }
                else if ( PkFloor.size(PkPlayerStates.pkFloor(pkPlayerStates, currentPlayerId)) == 7) {
                    PkPlayerStates.setPkFloor(pkPlayerStatesEditable, currentPlayerId,
                            PkFloor.withAddedTiles(PkPlayerStates.pkFloor(pkPlayerStates, currentPlayerId),PkTileSet.of(1, PkMove.color(pkMove))));
                }
                pkTileSourcesEditable[source.index()] = PkTileSet.remove( pkTileSourcesEditable[source.index()],color);
            }
        }

        else if ( destination instanceof TileDestination.Pattern) {
            PkPlayerStates.setPkPatterns(pkPlayerStatesEditable,currentPlayerId,
                    PkPatterns.withAddedTiles(PkPlayerStates.pkPatterns(pkPlayerStates,currentPlayerId), (TileDestination.Pattern) destination , countOfColor, color));
            int temporaryCountOfColor = countOfColor;
            while ( temporaryCountOfColor > 0) {
                pkTileSourcesEditable[source.index()] = PkTileSet.remove(pkTileSourcesEditable[source.index()], color);
                --temporaryCountOfColor;
            }

        }
        else if ( destination instanceof  TileDestination.Floor) {
            PkPlayerStates.setPkFloor(pkPlayerStatesEditable, currentPlayerId,
                    PkFloor.withAddedTiles(PkPlayerStates.pkFloor(pkPlayerStates, currentPlayerId),PkTileSet.of(countOfColor, PkMove.color(pkMove))));
            int temporaryCountOfColor = countOfColor;
            while ( temporaryCountOfColor > 0) {
                pkTileSourcesEditable[source.index()] = PkTileSet.remove(pkTileSourcesEditable[source.index()], color);
                --temporaryCountOfColor;
            }
        }

        currentPlayerId = game.playerIds().get(currentPlayerId.ordinal() % game.playersCount());

        updateUniqueTilesSources();

    }

    private void updateUniqueTilesSources(){
        int newPkUniqueTileSource = PkIntSet32.EMPTY;
        int firstPlayerMarkerSet = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);

        for (int i = 0; i < pkTileSourcesEditable.length; i++) {
            int currentTiles = pkTileSourcesEditable[i];
            int coloredTilesOnly = PkTileSet.difference(currentTiles, firstPlayerMarkerSet);

            if (PkTileSet.isEmpty(coloredTilesOnly)) {
                continue;
            }

            boolean isDuplicate = false;
            for (int j = 1; j < i; j++) {
                if (pkTileSourcesEditable[j] == currentTiles) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                newPkUniqueTileSource = PkIntSet32.add(newPkUniqueTileSource, i);
            }
        }

        this.pkUniqueTileSources = newPkUniqueTileSource;

    }


}
