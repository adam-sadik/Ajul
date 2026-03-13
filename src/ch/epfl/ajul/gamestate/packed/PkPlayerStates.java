package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

public final class PkPlayerStates {

    public static ImmutableIntArray initial(Game game) {
        int [] array = new int[4 * game.playersCount()];
        return ImmutableIntArray.copyOf(array);
    }
    public static int pkPatterns(ReadOnlyIntArray pkPlayerStates, PlayerId playerId) {
        return pkPlayerStates.get(4 * playerId.ordinal());
    }
    public static int pkFloor(ReadOnlyIntArray pkPlayerStates, PlayerId playerId){
        return pkPlayerStates.get(4 * playerId.ordinal() + 1);
    }
    public static int pkWall(ReadOnlyIntArray pkPlayerStates, PlayerId playerId){
        return pkPlayerStates.get(4 * playerId.ordinal() + 2);
    }
    public static int points(ReadOnlyIntArray pkPlayerStates, PlayerId playerId) {
        return pkPlayerStates.get(4 * playerId.ordinal() + 3);
    }
    public static void setPkPatterns(int[] pkPlayerStates, PlayerId playerId, int pkPatterns) {
        pkPlayerStates[playerId.ordinal() * 4] = pkPatterns;
    }
    public static void setPkFloor(int[] pkPlayerStates, PlayerId playerId, int pkFloor){
        pkPlayerStates[playerId.ordinal() * 4 + 1] = pkFloor;
    }
    public static void setPkWall(int[] pkPlayerStates, PlayerId playerId, int pkWall){
        pkPlayerStates[playerId.ordinal() * 4 + 2] = pkWall;
    }
    public static void addPoints(int[] pkPlayerStates, PlayerId playerId, int pointsToAdd){
        pkPlayerStates[playerId.ordinal() * 4 + 3] += pointsToAdd;
    }


}
