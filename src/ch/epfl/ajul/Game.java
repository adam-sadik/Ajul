package ch.epfl.ajul;

import java.util.List;
import java.util.Objects;

/// Représente une configuration immuable d'une partie d'Ajul.
/// La classe est immuable : toutes les listes retournées sont
/// non modifiables.
///
public final class Game {

    private final List<PlayerDescription> playerDescriptions;

    /// Construit une configuration de partie pour les joueurs donnés.
    ///
    /// La liste doit contenir entre 2 et 4 joueurs, et le joueur
    /// en position i doit avoir pour identité
    /// {@code PlayerId.ALL.get(i)}.
    ///
    /// @param playerDescriptions
    ///        la liste des descriptions des joueurs
    /// @throws IllegalArgumentException
    ///         si le nombre de joueurs n'est pas compris entre 2 et 4
    ///         ou si l'ordre des identités est incorrect
    public Game (List<PlayerDescription> playerDescriptions) {

        assert playerDescriptions != null;
        Preconditions.checkArgument(playerDescriptions.size() >= 2 && playerDescriptions.size() <= 4);
        for (int i = 0; i < playerDescriptions.size(); ++i) {
                Preconditions.checkArgument(playerDescriptions.get(i).id() == PlayerId.ALL.get(i));
            }

        this.playerDescriptions = List.copyOf(playerDescriptions);
       }



    /// Retourne la liste immuable des descriptions des joueurs.
    ///
    /// @return la liste immuable des descriptions des joueurs
    public List<PlayerDescription> playerDescriptions() {
        return playerDescriptions;
    }

    /// Retourne la liste immuable des identités des joueurs.
    ///
    /// @return la liste des identités des joueurs
    public List<PlayerId> playerIds() {
        return PlayerId.ALL.subList(0,playersCount());
    }

    /// Retourne le nombre de joueurs de la partie.
    ///
    /// @return le nombre de joueurs
    public int playersCount() {
        return playerDescriptions.size();
    }

    /// Retourne la liste immuable des fabriques utilisées
    /// dans la partie.
    ///
    /// @return la liste immuable des fabriques utilisées dans la partie
    public List<TileSource.Factory> factories(){
            return TileSource.Factory.ALL.subList(0, 2 * playersCount() + 1);
    }

    /// Retourne le nombre de fabriques utilisées dans la partie.
    ///
    /// @return le nombre de fabriques
    public int factoriesCount(){
            return 2 * playersCount() + 1;
    }

    /// Retourne la liste immuable des sources de tuiles utilisées
    /// dans la partie.
    ///
    /// @return la liste des sources de tuiles
    public List<TileSource> tileSources() {
        return TileSource.ALL.subList(0, tileSourcesCount());
    }

    /// Retourne le nombre de sources de tuiles dans la partie.
    ///
    /// @return le nombre de sources de tuiles
    public int tileSourcesCount() {
            return factoriesCount() + 1;
    }
    /// Retourne le nombre maximum de tuiles pouvant se trouver
    /// dans la zone centrale durant la partie.
    ///
    /// @return la taille maximale de la zone centrale
    public int centralAreaMaxSize() {
            return 3 * factoriesCount() +1;
    }

    /// Décrit un joueur participant à la partie.
    ///
    /// Une description contient son identité, son nom et son type
    /// (humain ou intelligence artificielle).
    public record PlayerDescription(PlayerId id, String name, PlayerKind kind) {

        /// Construit une description de joueur.
        ///
        /// @throws NullPointerException
        ///         si l'un des arguments est nul
        public PlayerDescription{
            Objects.requireNonNull(id);
            Objects.requireNonNull(name);
            Objects.requireNonNull(kind);
        }

        /// Représente le type d'un joueur.
        public enum PlayerKind {
            HUMAN, AI
        }
    }

}
