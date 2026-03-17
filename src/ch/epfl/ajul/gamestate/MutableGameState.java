package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.PointsObserver;
import ch.epfl.ajul.intarray.MutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

import java.util.random.RandomGenerator;

public final class MutableGameState implements ReadOnlyGameState  {

    private final Game game;
    private final int pkTileBag;
    private final ReadOnlyIntArray pkTileSources;
    private final int pkUniqueTileSources;
    private final ReadOnlyIntArray pkPlayerStates;
    private final PlayerId currentPlayerId;
    private final MutableIntArray filledFactories;

    private final ReadOnlyGameState initialState;
    private final PointsObserver pointsObserver;

    MutableGameState(ReadOnlyGameState initialState, PointsObserver pointsObserver) {
        this.initialState = initialState;
        this.pointsObserver = pointsObserver;
        game = initialState.game();
        pkTileBag = initialState.pkTileBag();
        pkTileSources = initialState.pkTileSources();
        pkUniqueTileSources = initialState.pkUniqueTileSources();
        pkPlayerStates = initialState.pkPlayerStates();
        currentPlayerId = initialState.currentPlayerId();
        int[] array = new int [game.factoriesCount()];
        filledFactories = MutableIntArray.wrapping(array);
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
    public ReadOnlyIntArray pkPlayerStates() {
        return pkPlayerStates;
    }

    @Override
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    public void fillFactories(RandomGenerator randomGenerator) {
       // assert isRoundOver();
    }


}
