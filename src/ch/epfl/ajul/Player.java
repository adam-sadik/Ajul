package ch.epfl.ajul;

import ch.epfl.ajul.gamestate.ReadOnlyGameState;

@FunctionalInterface
public interface Player {
    abstract int nextMove(ReadOnlyGameState state);
}
