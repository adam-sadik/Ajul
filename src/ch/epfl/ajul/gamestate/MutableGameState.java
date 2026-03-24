package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.*;
import ch.epfl.ajul.intarray.MutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

import java.util.random.RandomGenerator;

/// Représente l'état modifiable d'une partie d'Ajul.
/// Cette classe permet de faire évoluer l'état du jeu (remplissage des fabriques,
/// application des coups, calcul des scores de fin de manche et de fin de partie).
/// @author Rayane Taoufik Benchekroun (412052)
/// @author Adam Ghali SADIK (412029)
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

    /// Construit un état de partie modifiable à partir d'un état initial et d'un observateur de points.
    ///
    /// @param initialState   l'état initial de la partie (en lecture seule)
    /// @param pointsObserver l'observateur notifié lors des changements de points des joueurs
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

    /// Construit un état de partie modifiable à partir d'un état initial,
    /// avec un observateur de points vide par défaut.
    ///
    /// @param initialState l'état initial de la partie (en lecture seule)
    public MutableGameState(ReadOnlyGameState initialState) {
        this(initialState, PointsObserver.EMPTY);
    }

    /// Retourne les paramètres de la configuration de la partie.
    ///
    /// @return la configuration de la partie (instance de `Game`)
    @Override
    public Game game() {
        return game;
    }

    /// Retourne le contenu actuel du sac de tuiles sous forme empaquetée.
    ///
    /// @return le sac de tuiles empaqueté
    @Override
    public int pkTileBag() {
        return pkTileBag;
    }

    /// Retourne les sources de tuiles (zone centrale et fabriques) sous forme de tableau en lecture seule.
    ///
    /// @return le tableau des sources de tuiles empaquetées
    @Override
    public ReadOnlyIntArray pkTileSources() {
        return pkTileSources;
    }

    /// Retourne l'ensemble des index des sources de tuiles uniques sous forme empaquetée.
    ///
    /// @return les sources de tuiles uniques empaquetées
    @Override
    public int pkUniqueTileSources() {
        return pkUniqueTileSources;
    }

    /// Retourne l'état de tous les joueurs de la partie sous forme de tableau en lecture seule.
    ///
    /// @return le tableau des états empaquetés des joueurs
    @Override
    public ReadOnlyIntArray pkPlayerStates() {
        return pkPlayerStates;
    }

    /// Retourne l'identité du joueur dont c'est le tour de jouer.
    ///
    /// @return l'identité du joueur courant
    @Override
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }


    /// Remplit les fabriques avec des tuiles tirées aléatoirement du sac.
    /// Si le sac est vide, il est préalablement rempli avec les tuiles sorties du jeu.
    /// Met également à jour l'ensemble des sources de tuiles uniques.
    ///
    /// @param randomGenerator le générateur de nombres aléatoires utilisé pour tirer les tuiles
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
    /// Enregistre et applique un coup joué par le joueur courant.
    /// Cette méthode déplace les tuiles de la source choisie vers la destination,
    /// gère les tuiles restantes (vers la zone centrale ou le plancher),
    /// met à jour le joueur courant, et actualise les sources uniques.
    ///
    /// @param pkMove le coup joué sous forme empaquetée

    public void registerMove(short pkMove) {
        TileSource source = PkMove.source(pkMove);
        TileKind.Colored color = PkMove.color(pkMove);
        TileDestination destination = PkMove.destination(pkMove);
        int countOfColor = PkTileSet.countOf(pkTileSources.get(source.index()), color);
        int pkPatterns = PkPlayerStates.pkPatterns(pkPlayerStates, currentPlayerId);

        if (source instanceof TileSource.Factory) {
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

        currentPlayerId = game.playerIds().get((currentPlayerId.ordinal() + 1) % game.playersCount());

        updateUniqueTilesSources();

    }

    /// Met à jour l'ensemble empaqueté représentant les sources de tuiles uniques.
    /// Une source est considérée unique si elle contient des tuiles colorées et que
    /// son contenu diffère de toutes les fabriques la précédant.
    private void updateUniqueTilesSources() {
        int newPkUniqueTileSource = PkIntSet32.EMPTY;

        int centralArea = pkTileSources.get(TileSource.CENTER_AREA.index());
        boolean centerAreaContainsFirstPlayerMarker = PkTileSet.countOf(centralArea, TileKind.FIRST_PLAYER_MARKER ) == TileKind.FIRST_PLAYER_MARKER.tilesCount();
        if ( centerAreaContainsFirstPlayerMarker){
            centralArea = PkTileSet.remove(centralArea, TileKind.FIRST_PLAYER_MARKER);
        }
        if ( !PkTileSet.isEmpty(centralArea)){
            newPkUniqueTileSource = PkIntSet32.add(newPkUniqueTileSource,TileSource.CENTER_AREA.index() );
        }

        for (int i = 1; i <= game.factoriesCount() ; i++) {
            int factoryNbI = pkTileSources.get(i);
            int factoryNbIWithoutFirstPlayerMaker = factoryNbI;
            boolean containsAColorTile = false;
            if ( PkTileSet.countOf(factoryNbI, TileKind.FIRST_PLAYER_MARKER) == TileKind.FIRST_PLAYER_MARKER.tilesCount()) {
                factoryNbIWithoutFirstPlayerMaker = PkTileSet.remove(factoryNbI, TileKind.FIRST_PLAYER_MARKER);
            }
            if ( !PkTileSet.isEmpty(factoryNbIWithoutFirstPlayerMaker) ){
                containsAColorTile = true;
            }
            if ( containsAColorTile) {
                    boolean duplicate = false;
                for (int j = 1; j < i; j++) {
                    if ( factoryNbI == pkTileSources.get(j)){
                        duplicate = true;
                        break;
                    }
                }
                if ( !duplicate){
                    newPkUniqueTileSource = PkIntSet32.add(newPkUniqueTileSource, i);
                }
            }

        }
        this.pkUniqueTileSources = newPkUniqueTileSource;

    }


    /// Termine la manche en cours.
    /// Cette méthode parcourt les lignes de motif de tous les joueurs. Si une ligne est pleine,
    /// une tuile est déplacée sur le mur et les points correspondants sont ajoutés.
    /// Les pénalités de la ligne plancher sont ensuite déduites (sans descendre sous 0 point).
    /// Les observateurs de points sont notifiés de chaque événement.
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

    /// Termine la partie de jeu.
    /// Cette méthode inspecte le mur de chaque joueur pour identifier les lignes, colonnes
    /// et couleurs complètes, puis leur attribue les points de bonus finaux correspondants.
    /// Les observateurs de points sont notifiés de chaque bonus.

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


