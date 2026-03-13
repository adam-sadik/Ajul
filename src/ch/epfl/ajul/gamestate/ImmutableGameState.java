package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.intarray.ImmutableIntArray;

public record ImmutableGameState(Game game, int pkTileBag, ImmutableIntArray pkTileSources, int pkUniqueTileSources, ImmutableIntArray pkPlayerStates, PlayerId currentPlayerId) implements ReadOnlyGameState {

}
