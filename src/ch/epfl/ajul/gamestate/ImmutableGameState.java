package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.Game;
import ch.epfl.ajul.PlayerId;
import ch.epfl.ajul.TileKind;
import ch.epfl.ajul.TileSource;
import ch.epfl.ajul.gamestate.packed.PkIntSet32;
import ch.epfl.ajul.gamestate.packed.PkPlayerStates;
import ch.epfl.ajul.gamestate.packed.PkTileSet;
import ch.epfl.ajul.intarray.ImmutableIntArray;

import java.util.Objects;

/// Représente l'état complet d'une partie d'Ajul de manière strictement immuable.
///
/// @param game la configuration de la partie
/// @param pkTileBag le contenu empaqueté du sac
/// @param pkTileSources le contenu empaqueté des sources de tuiles
/// @param pkUniqueTileSources l'ensemble empaqueté des index des sources uniques
/// @param pkPlayerStates les états empaquetés des joueurs
/// @param currentPlayerId l'identité du joueur courant
public record ImmutableGameState(Game game, int pkTileBag, ImmutableIntArray pkTileSources, int pkUniqueTileSources, ImmutableIntArray pkPlayerStates, PlayerId currentPlayerId) implements ReadOnlyGameState {

    /// Construit un état de jeu immuable.
    /// Lève une exception si l'un des arguments objets fournis est null.
    public ImmutableGameState {
        Objects.requireNonNull(game);
        Objects.requireNonNull(pkTileSources);
        Objects.requireNonNull(pkPlayerStates);
        Objects.requireNonNull(currentPlayerId);
    }
    /// Retourne l'état initial d'une nouvelle partie.
    /// Dans cet état, les sources sont vides à l'exception de la zone centrale contenant le marqueur
    /// de premier joueur, le sac contient toutes les tuiles colorées, et le premier joueur est actif.
    ///
    /// @param game la configuration de la partie
    /// @return le statut de jeu immuable initial
    public static ImmutableGameState initial(Game game){
        int[] sourcesArray = new int[TileSource.ALL.size()];
        sourcesArray[0] = PkTileSet.of(1, TileKind.FIRST_PLAYER_MARKER);
        ImmutableIntArray immutableArray = ImmutableIntArray.copyOf(sourcesArray);
        PlayerId firstPlayer = game.playerIds().get(0);
        return new ImmutableGameState(game, PkTileSet.FULL_COLORED, immutableArray, PkIntSet32.EMPTY, PkPlayerStates.initial(game), firstPlayer);
    }

    /// Retourne le récepteur (this) puisqu'il est déjà immuable.
    ///
    /// @return cet état de partie immuable
    @Override
    public ImmutableGameState immutable() {
        return this;
    }
}
