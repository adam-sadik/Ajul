package ch.epfl.ajul;

import ch.epfl.ajul.gamestate.ReadOnlyGameState;

public final class RankComputer {

    public static void playersRank(ReadOnlyGameState state, int [] ranks) {
        if (ranks.length != state.game().playersCount()) {
            throw new IllegalArgumentException("La taille du tableau doit être égale au nombre de joueurs.");
        }


    }
}
