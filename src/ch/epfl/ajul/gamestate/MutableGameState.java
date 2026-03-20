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
        if (PkMove.source(pkMove) instanceof TileSource.Factory){
            PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, currentPlayerId, PkMove.destination(pkMove).index());

        }

    }

    public void endRound() {
        for (PlayerId playerId : playerIds()) {
            int points = 0;
            int pkPatterns = PkPlayerStates.pkPatterns(pkPlayerStates(), playerId);

            for (TileDestination.Pattern pattern : TileDestination.Pattern.ALL) {
                if (PkPatterns.isFull(pkPatterns, pattern)){

                    TileKind.Colored coloredTile = PkPatterns.color(pkPatterns, pattern);
                    int pkWall = PkPlayerStates.pkWall(pkPlayerStates, playerId);
                    int newWall = PkWall.withTileAt(pkWall, pattern, coloredTile);
                    PkPlayerStates.setPkWall(pkPlayerStatesEditable, playerId, newWall);

                    int tilePoints = Points.newWallTilePoints(
                            PkWall.hGroupSize(newWall, pattern, coloredTile),
                            PkWall.vGroupSize(newWall, pattern, coloredTile));
                    points += tilePoints;

                    pointsObserver.newWallTile(playerId, pattern, coloredTile, tilePoints);

                    pkPatterns = PkPatterns.withEmptyLine(pkPatterns, pattern);
                }
            }
            PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, playerId, pkPatterns);

            int pkFloor = PkPlayerStates.pkFloor(pkPlayerStates(), playerId);
            int floorPenalty = Points.totalFloorPenalty(PkFloor.size(pkFloor));
            points -= floorPenalty;
            pointsObserver.floor(playerId, floorPenalty);
            int currentScore = PkPlayerStates.points(pkPlayerStates, playerId);
            if (currentScore + points < 0) {
                points = -currentScore;
            }
            PkPlayerStates.addPoints(pkPlayerStatesEditable, playerId, points);
            if (PkFloor.containsFirstPlayerMarker(pkFloor)) {
                pkTileSourcesEditable[TileSource.CENTER_AREA.index()] =
                        PkTileSet.add(pkTileSourcesEditable[TileSource.CENTER_AREA.index()], TileKind.FIRST_PLAYER_MARKER);
                currentPlayerId = playerId;
            }
            PkPlayerStates.setPkFloor(pkPlayerStatesEditable, playerId,PkFloor.EMPTY);
        }
    }

    public void endGame() {
        for (PlayerId playerId : playerIds()) {
            int bonusPoints = 0;
            int pkWall = PkPlayerStates.pkWall(pkPlayerStates, playerId);

            for (TileDestination.Pattern line : TileDestination.Pattern.ALL) {
                if (PkWall.isRowFull(pkWall, line)) {
                    bonusPoints += Points.FULL_ROW_BONUS_POINTS;
                    pointsObserver.fullRow(playerId, line, Points.FULL_ROW_BONUS_POINTS);
                }
            }

            for (int j = 0; j < PkWall.WALL_WIDTH; ++j) {
                if (PkWall.isColumnFull(pkWall, j)) {
                    bonusPoints += Points.FULL_COLUMN_BONUS_POINTS;
                    pointsObserver.fullColumn(playerId, j, Points.FULL_COLUMN_BONUS_POINTS);
                }
            }

            for (TileKind.Colored color : TileKind.Colored.ALL) {
                if (PkWall.isColorFull(pkWall, color)) {
                    bonusPoints += Points.FULL_COLOR_BONUS_POINTS;
                    pointsObserver.fullColor(playerId, color, Points.FULL_COLOR_BONUS_POINTS);
                }
            }

            PkPlayerStates.addPoints(pkPlayerStatesEditable, playerId, bonusPoints);
        }
    }


}
