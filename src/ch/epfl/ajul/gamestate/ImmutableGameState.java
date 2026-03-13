package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.gamestate.packed.PkIntSet32;
import ch.epfl.ajul.gamestate.packed.PkPlayerStates;
import ch.epfl.ajul.gamestate.packed.PkTileSet;
import ch.epfl.ajul.intarray.ImmutableIntArray;

import java.util.Objects;

public record ImmutableGameState(Game game, int pkTileBag, ImmutableIntArray pkTileSources, int pkUniqueTileSources, ImmutableIntArray pkPlayerStates, PlayerId currentPlayerId) implements ReadOnlyGameState {

    public ImmutableGameState {
        Objects.requireNonNull(game);
        Objects.requireNonNull(pkTileBag);
        Objects.requireNonNull(pkTileSources);
        Objects.requireNonNull(pkUniqueTileSources);
        Objects.requireNonNull(pkPlayerStates);
        Objects.requireNonNull(currentPlayerId);
    }

    public static ImmutableGameState initial(Game game){
        int [] array = {PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER)};
        ImmutableIntArray immutableArray = ImmutableIntArray.copyOf(array);

        return new ImmutableGameState(game, PkTileSet.FULL_COLORED, immutableArray , PkIntSet32.EMPTY, PkPlayerStates.initial(game), PlayerId.P1);
    }

    @Override
    public ImmutableGameState immutable() {
        return this;
    }
}
