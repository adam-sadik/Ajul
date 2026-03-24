package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.*;
import ch.epfl.ajul.intarray.MutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

import java.util.random.RandomGenerator;

public final class MutableGameState implements ReadOnlyGameState {

    private final Game game;
    private int pkTileBag;
    private final ReadOnlyIntArray pkTileSources;
    private final int[] pkTileSourcesEditable;
    private int pkUniqueTileSources;
    private final ReadOnlyIntArray pkPlayerStates;
    private final int[] pkPlayerStatesEditable;
    private PlayerId currentPlayerId;
    private final PointsObserver pointsObserver;

    public MutableGameState(ReadOnlyGameState initialState, PointsObserver pointsObserver) {
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

    public MutableGameState(ReadOnlyGameState initialState) {
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
    public ReadOnlyIntArray pkPlayerStates() {
        return pkPlayerStates;
    }

    @Override
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    public void fillFactories(RandomGenerator randomGenerator) {
        //assert isRoundOver();
        int necessaryNbTiles = (pkTileSources.size() - 1) * TileSource.Factory.TILES_PER_FACTORY;
        int drawnTilesMultiset = PkTileSet.EMPTY;

        if (PkTileSet.size(pkTileBag) > necessaryNbTiles) {
            TileKind.Colored[] drawnArray = new TileKind.Colored[necessaryNbTiles];
            PkTileSet.sampleColoredInto(pkTileBag, drawnArray, 0, randomGenerator);

            for (TileKind.Colored tile : drawnArray) {
                drawnTilesMultiset = PkTileSet.add(drawnTilesMultiset, tile);
                pkTileBag = removeColoredTile(pkTileBag, tile);
            }
        } else {
            drawnTilesMultiset = pkTileBag;
            pkTileBag = pkDiscardedTiles();

            int needed = necessaryNbTiles - PkTileSet.size(drawnTilesMultiset);
            int toDraw = Math.min(needed, PkTileSet.size(pkTileBag));

            if (toDraw > 0) {
                TileKind.Colored[] tempArray = new TileKind.Colored[toDraw];
                PkTileSet.sampleColoredInto(pkTileBag, tempArray, 0, randomGenerator);
                for (TileKind.Colored tile : tempArray) {
                    drawnTilesMultiset = PkTileSet.add(drawnTilesMultiset, tile);
                    pkTileBag = removeColoredTile(pkTileBag, tile);
                }
            }
        }

        int totalDrawn = PkTileSet.size(drawnTilesMultiset);
        TileKind.Colored[] finalDrawnArray = new TileKind.Colored[totalDrawn];
        PkTileSet.copyColoredInto(drawnTilesMultiset, finalDrawnArray);
        TileKind.Colored.shuffle(finalDrawnArray, randomGenerator);

        int tileIndex = 0;
        for (int i = 1; i < pkTileSources.size(); i++) {
            int toFill = TileSource.Factory.TILES_PER_FACTORY - PkTileSet.size(pkTileSources.get(i));
            for (int j = 0; j < toFill; j++) {
                if (tileIndex < totalDrawn) {
                    pkTileSourcesEditable[i] = PkTileSet.add(pkTileSourcesEditable[i], finalDrawnArray[tileIndex]);
                    tileIndex++;
                }
            }
        }

        updateUniqueTilesSources();
    }

    private int removeColoredTile(int bag, TileKind.Colored coloredTile) {
        return switch (coloredTile) {
            case A -> PkTileSet.remove(bag, TileKind.A);
            case B -> PkTileSet.remove(bag, TileKind.B);
            case C -> PkTileSet.remove(bag, TileKind.C);
            case D -> PkTileSet.remove(bag, TileKind.D);
            case E -> PkTileSet.remove(bag, TileKind.E);
        };
    }

    public void registerMove(short pkMove) {
        TileSource source = PkMove.source(pkMove);
        TileKind.Colored color = PkMove.color(pkMove);
        TileDestination destination = PkMove.destination(pkMove);
        int countOfColor = PkTileSet.countOf(pkTileSources.get(source.index()), color);
        int pkPatterns = PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId);

// Ne Rajoute pas les tuiles dans la destination
        if (source instanceof TileSource.Factory) {
            //PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, currentPlayerId, PkTileSet.add(PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId), PkMove.color(pkMove)));
            for (int i = 0; i < TileKind.Colored.ALL.size(); i++) {
                TileKind.Colored colorI = TileKind.Colored.ALL.get(i);
                int nbOfColorITiles = PkTileSet.countOf(pkTileSources.get(source.index()), colorI);
                while (nbOfColorITiles > 0
                        && colorI != color) {
                    pkTileSourcesEditable[0] = PkTileSet.add(pkTileSourcesEditable[0], colorI);
                    pkTileSourcesEditable[source.index()] = PkTileSet.remove(pkTileSourcesEditable[source.index()], colorI);
                    --nbOfColorITiles;
                }
            }
        } else if (source instanceof TileSource.CenterArea && PkTileSet.countOf(pkTileSources.get(source.index()), TileKind.FIRST_PLAYER_MARKER) > 0) {
            PkPlayerStates.setPkFloor(pkPlayerStatesEditable, currentPlayerId,
                    PkFloor.withAddedTiles(PkPlayerStates.pkFloor(pkPlayerStates, currentPlayerId), PkTileSet.difference(PkTileSet.FULL, PkTileSet.FULL_COLORED)));
            pkTileSourcesEditable[0] = PkTileSet.remove(pkTileSourcesEditable[0], TileKind.FIRST_PLAYER_MARKER);
        }

        if (destination instanceof TileDestination.Pattern && destination.capacity() > countOfColor) {
            for (int i = 0; i < countOfColor; ++i) {
                if (!PkPatterns.isFull(pkPatterns, (TileDestination.Pattern) destination)) {
                    PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, currentPlayerId,
                            PkPatterns.withAddedTiles(PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId), (TileDestination.Pattern) destination, 1, PkMove.color(pkMove)));
                } else if (PkFloor.size(PkPlayerStates.pkFloor(pkPlayerStates, currentPlayerId)) == TileDestination.FLOOR.capacity()) {
                    PkPlayerStates.setPkFloor(pkPlayerStatesEditable, currentPlayerId,
                            PkFloor.withAddedTiles(PkPlayerStates.pkFloor(pkPlayerStates, currentPlayerId), PkTileSet.of(1, PkMove.color(pkMove))));
                }
                pkTileSourcesEditable[source.index()] = PkTileSet.remove(pkTileSourcesEditable[source.index()], color);
            }
        } else if (destination instanceof TileDestination.Pattern) {
            PkPlayerStates.setPkPatterns(pkPlayerStatesEditable, currentPlayerId,
                    PkPatterns.withAddedTiles(PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId), (TileDestination.Pattern) destination, countOfColor, color));
            int temporaryCountOfColor = countOfColor;
            while (temporaryCountOfColor > 0) {
                pkTileSourcesEditable[source.index()] = PkTileSet.remove(pkTileSourcesEditable[source.index()], color);
                --temporaryCountOfColor;
            }

        } else if (destination instanceof TileDestination.Floor) {
            PkPlayerStates.setPkFloor(pkPlayerStatesEditable, currentPlayerId,
                    PkFloor.withAddedTiles(PkPlayerStates.pkFloor(pkPlayerStates, currentPlayerId), PkTileSet.of(countOfColor, PkMove.color(pkMove))));
            int temporaryCountOfColor = countOfColor;
            while (temporaryCountOfColor > 0) {
                pkTileSourcesEditable[source.index()] = PkTileSet.remove(pkTileSourcesEditable[source.index()], color);
                --temporaryCountOfColor;
            }
        }

        currentPlayerId = game.playerIds().get(currentPlayerId.ordinal() % game.playersCount());

        updateUniqueTilesSources();

    }

    private void updateUniqueTilesSources() {
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

    public void endRound() {
        for (PlayerId playerId : playerIds()) {
            int points = 0;
            int pkPatterns = PkPlayerStates.pkPatterns(pkPlayerStates(), playerId);

            for (TileDestination.Pattern pattern : TileDestination.Pattern.ALL) {
                if (PkPatterns.isFull(pkPatterns, pattern)) {

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

            int currentScore = PkPlayerStates.points(pkPlayerStates, playerId);
            int scoreBeforePenalty = currentScore + points;

            int pkFloor = PkPlayerStates.pkFloor(pkPlayerStates(), playerId);
            int theoreticalPenalty = Points.totalFloorPenalty(PkFloor.size(pkFloor));

            int effectivePenalty = Math.min(scoreBeforePenalty, theoreticalPenalty);

            points -= effectivePenalty;
            PkPlayerStates.addPoints(pkPlayerStatesEditable, playerId, points);

            if (effectivePenalty > 0) {
                pointsObserver.floor(playerId, effectivePenalty);
            }

            if (PkFloor.containsFirstPlayerMarker(pkFloor)) {
                pkTileSourcesEditable[TileSource.CENTER_AREA.index()] =
                        PkTileSet.add(pkTileSourcesEditable[TileSource.CENTER_AREA.index()], TileKind.FIRST_PLAYER_MARKER);
                currentPlayerId = playerId;
            }
            PkPlayerStates.setPkFloor(pkPlayerStatesEditable, playerId, PkFloor.EMPTY);
        }
    }

    public void endGame() {
        for (PlayerId playerId : playerIds()) {
            int bonusPoints = 0;
            int pkWall = PkPlayerStates.pkWall(pkPlayerStates(), playerId);

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


