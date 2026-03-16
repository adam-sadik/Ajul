package ch.epfl.ajul.gamestate;

import ch.epfl.ajul.*;
import ch.epfl.ajul.gamestate.packed.*;
import ch.epfl.ajul.intarray.ImmutableIntArray;
import ch.epfl.ajul.intarray.ReadOnlyIntArray;

import java.util.List;

/// Représente l'état complet d'une partie d'Ajul en lecture seule.
/// @author Rayane Taoufik Benchekroun (412052)
/// @author Adam Ghali SADIK (412029)
public interface ReadOnlyGameState {

    /// Retourne la configuration de la partie.
    ///
    /// @return la configuration de la partie
    Game game();

    /// Retourne le contenu du sac duquel les tuiles sont extraites pour remplir les fabriques.
    ///
    /// @return l'ensemble empaqueté des tuiles contenues dans le sac
    int pkTileBag();

    /// Retourne un tableau décrivant le contenu des sources de tuiles.
    /// L'élément à l'index `i` correspond à l'ensemble de tuiles empaqueté de la source d'index `i`.
    ///
    /// @return le tableau en lecture seule des sources de tuiles
    ReadOnlyIntArray pkTileSources();

    /// Retourne l'ensemble empaqueté des index des sources de tuiles uniques.
    /// Une source est unique si elle contient au moins une tuile colorée et n'a pas
    /// le même contenu qu'une fabrique qui la précède.
    ///
    /// @return l'ensemble empaqueté des index des sources uniques
    int pkUniqueTileSources();

    /// Retourne un tableau contenant les états empaquetés des joueurs.
    ///
    /// @return le tableau en lecture seule des états des joueurs
    ReadOnlyIntArray pkPlayerStates();

    /// Retourne l'identité du joueur courant.
    ///
    /// @return l'identité du joueur devant jouer le prochain coup
    PlayerId currentPlayerId();

    /// Retourne une version immuable de l'état de la partie auquel on l'applique.
    ///
    /// @return l'état de la partie sous forme immuable
    default ImmutableGameState immutable() {
        return new ImmutableGameState(game(),
                pkTileBag(),
                ImmutableIntArray.copyOf(pkTileSources().toArray()),
                pkUniqueTileSources(),
                ImmutableIntArray.copyOf(pkPlayerStates().toArray()),
                currentPlayerId());
    }

    /// Retourne la liste des identités des joueurs de la partie.
    ///
    /// @return la liste des identités des joueurs
    default List<PlayerId> playerIds() {
        return game().playerIds();
    }

    /// Détermine si la manche en cours est terminée.
    /// La manche est terminée si aucune source de tuile ne contient de tuile colorée.
    ///
    /// @return `true` si la manche est terminée, `false` sinon
    default boolean isRoundOver() {
        return pkUniqueTileSources() == PkIntSet32.EMPTY;
    }

    /// Détermine si la partie est terminée.
    /// La partie est terminée si la manche est terminée et qu'au moins un joueur
    /// possède une ligne horizontale complète dans son mur.
    ///
    /// @return `true` si la partie est terminée, `false` sinon
    default boolean isGameOver() {
        boolean isRowFull = false;
        if (!isRoundOver()) {
            return false;
        }
        for (int i = 0; i < playerIds().size(); ++i) {
            if (PkWall.hasFullRow(PkPlayerStates.pkWall(pkPlayerStates(), PlayerId.ALL.get(i)))) {
                isRowFull = true;
                break;
            }
        }
        return isRoundOver() && isRowFull;
    }

    /// Calcule et retourne l'ensemble empaqueté des tuiles sorties du jeu.
    ///
    /// @return l'ensemble empaqueté des tuiles défaussées
    default int pkDiscardedTiles() {

        int total = pkTileBag();

        for (int i = 0; i < pkTileSources().size(); ++i) {
            total = PkTileSet.union(total, pkTileSources().get(i));
        }

        for (PlayerId p : playerIds()) {
            int patterns = PkPlayerStates.pkPatterns(pkPlayerStates(), p);
            int floor = PkPlayerStates.pkFloor(pkPlayerStates(), p);
            int wall = PkPlayerStates.pkWall(pkPlayerStates(), p);

            total = PkTileSet.union(total, PkPatterns.asPkTileSet(patterns));
            total = PkTileSet.union(total, PkFloor.asPkTileSet(floor));
            total = PkTileSet.union(total, PkWall.asPkTileSet(wall));
        }

        return PkTileSet.difference(PkTileSet.FULL, total);
    }

    private int generateMoves(int allowedSources, short[] destination) {
        int i = 0;
        PlayerId me = currentPlayerId();
        int myPatterns = PkPlayerStates.pkPatterns(pkPlayerStates(), me);
        int myWall = PkPlayerStates.pkWall(pkPlayerStates(), me);

        for (TileSource source : TileSource.ALL) {

            if (source.index() >= pkTileSources().size()) {
                continue;
            }

            if (PkIntSet32.contains(allowedSources, source.index())) {
                int sourceTiles = pkTileSources().get(source.index());
                for (TileKind.Colored color : TileKind.Colored.ALL) {
                    if (PkTileSet.countOf(sourceTiles, color) != 0) {
                        for (TileDestination dest : TileDestination.ALL) {
                            if (dest instanceof TileDestination.Pattern pDest) {
                                if (!PkPatterns.isFull(myPatterns, pDest)
                                        && PkPatterns.canContain(myPatterns, pDest, color)
                                        && !PkWall.hasTileAt(myWall, pDest, color)) {
                                    destination[i++] = PkMove.pack(source, color, dest);
                                }
                            } else if (dest instanceof TileDestination.Floor) {
                                destination[i++] = PkMove.pack(source, color, dest);
                            }
                        }
                    }
                }
            }
        }
        return i;
    }

    /// Calcule tous les coups que le joueur courant a le droit de jouer depuis toutes les sources,
    /// les place dans le tableau `destination` et retourne leur nombre.
    ///
    /// @param destination le tableau (de taille au moins Move.MAX_MOVES) recevant les coups empaquetés
    /// @return le nombre de coups valides placés dans le tableau
    default int validMoves(short[] destination) {
        int allSources = PkIntSet32.EMPTY;
        for (int i = 0; i < pkTileSources().size(); i++) {
            allSources = PkIntSet32.add(allSources, i);
        }
        return generateMoves(allSources, destination);
    }

    /// Calcule tous les coups que le joueur courant a le droit de jouer depuis les sources *uniques*,
    /// les place dans le tableau `destination` et retourne leur nombre.
    ///
    /// @param destination le tableau (de taille au moins Move.MAX_MOVES) recevant les coups empaquetés
    /// @return le nombre de coups valides uniques placés dans le tableau
    default int uniqueValidMoves(short[] destination) {
        return generateMoves(pkUniqueTileSources(), destination);
    }
}

