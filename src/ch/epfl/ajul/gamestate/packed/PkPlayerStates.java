package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

/// Contient des méthodes permettant de manipuler les états empaquetés de tous les joueurs d'une partie d'Ajul.
/// @author Rayane Taoufik Benchekroun (412052)
/// @author Adam Ghali SADIK (412029)
public final class PkPlayerStates {

    private static final int ELEMENTS_PER_PLAYER = 4;

    /// Retourne un tableau immuable contenant l'état empaqueté initial des joueurs de la partie donnée,
    /// dans lequel toutes les lignes de motif, lignes plancher et murs sont vides, et tous les points valent 0.
    ///
    /// @param game la configuration de la partie
    /// @return le tableau immuable de l'état initial des joueurs
    public static ImmutableIntArray initial(Game game) {
        int[] array = new int[ELEMENTS_PER_PLAYER * game.playersCount()];
        for (PlayerId playerId : game.playerIds()) {
            setPkPatterns(array, playerId, PkPatterns.EMPTY);
            setPkFloor(array, playerId, PkFloor.EMPTY);
            setPkWall(array, playerId, PkWall.EMPTY);
        }
        return ImmutableIntArray.copyOf(array);
    }

    /// Retourne le contenu empaqueté des lignes de motif du joueur donné.
    ///
    /// @param pkPlayerStates le tableau des états empaquetés de tous les joueurs
    /// @param playerId l'identité du joueur
    /// @return le contenu empaqueté des lignes de motif
    public static int pkPatterns(ReadOnlyIntArray pkPlayerStates, PlayerId playerId) {
        return pkPlayerStates.get(ELEMENTS_PER_PLAYER * playerId.ordinal());
    }

    /// Retourne le contenu empaqueté de la ligne plancher du joueur donné.
    ///
    /// @param pkPlayerStates le tableau des états empaquetés de tous les joueurs
    /// @param playerId l'identité du joueur
    /// @return le contenu empaqueté de la ligne plancher
    public static int pkFloor(ReadOnlyIntArray pkPlayerStates, PlayerId playerId){
        return pkPlayerStates.get(ELEMENTS_PER_PLAYER * playerId.ordinal() + 1);
    }

    /// Retourne le contenu empaqueté du mur du joueur donné.
    ///
    /// @param pkPlayerStates le tableau des états empaquetés de tous les joueurs
    /// @param playerId l'identité du joueur
    /// @return le contenu empaqueté du mur
    public static int pkWall(ReadOnlyIntArray pkPlayerStates, PlayerId playerId){
        return pkPlayerStates.get(ELEMENTS_PER_PLAYER * playerId.ordinal() + 2);
    }

    /// Retourne le nombre de points du joueur donné.
    ///
    /// @param pkPlayerStates le tableau des états empaquetés de tous les joueurs
    /// @param playerId l'identité du joueur
    /// @return le nombre de points actuel
    public static int points(ReadOnlyIntArray pkPlayerStates, PlayerId playerId) {
        return pkPlayerStates.get(ELEMENTS_PER_PLAYER * playerId.ordinal() + 3);
    }

    /// Modifie le contenu empaqueté des lignes de motif du joueur donné dans le tableau modifiable.
    ///
    /// @param pkPlayerStates le tableau modifiable des états empaquetés
    /// @param playerId l'identité du joueur
    /// @param pkPatterns le nouveau contenu empaqueté des lignes de motif
    public static void setPkPatterns(int[] pkPlayerStates, PlayerId playerId, int pkPatterns) {
        pkPlayerStates[playerId.ordinal() * ELEMENTS_PER_PLAYER] = pkPatterns;
    }

    /// Modifie le contenu empaqueté de la ligne plancher du joueur donné dans le tableau modifiable.
    ///
    /// @param pkPlayerStates le tableau modifiable des états empaquetés
    /// @param playerId l'identité du joueur
    /// @param pkFloor le nouveau contenu empaqueté de la ligne plancher
    public static void setPkFloor(int[] pkPlayerStates, PlayerId playerId, int pkFloor){
        pkPlayerStates[playerId.ordinal() * ELEMENTS_PER_PLAYER + 1] = pkFloor;
    }

    /// Modifie le contenu empaqueté du mur du joueur donné dans le tableau modifiable.
    ///
    /// @param pkPlayerStates le tableau modifiable des états empaquetés
    /// @param playerId l'identité du joueur
    /// @param pkWall le nouveau contenu empaqueté du mur
    public static void setPkWall(int[] pkPlayerStates, PlayerId playerId, int pkWall){
        pkPlayerStates[playerId.ordinal() * ELEMENTS_PER_PLAYER + 2] = pkWall;
    }

    /// Ajoute un certain nombre de points au joueur donné dans le tableau modifiable.
    /// Le nombre de points à ajouter peut être négatif.
    ///
    /// @param pkPlayerStates le tableau modifiable des états empaquetés
    /// @param playerId l'identité du joueur
    /// @param pointsToAdd le nombre de points à ajouter (peut être négatif)
    public static void addPoints(int[] pkPlayerStates, PlayerId playerId, int pointsToAdd){
        pkPlayerStates[playerId.ordinal() * ELEMENTS_PER_PLAYER + 3] += pointsToAdd;
    }


}
