package ch.epfl.ajul;

/// Interface représentant un observateur des changements de points des joueurs au cours d'une partie.
/// Elle permet d'être notifié lors de l'attribution de points (ajout de tuiles, bonus de fin de partie)
/// ou lors de la déduction de points (pénalités de la ligne plancher).
///
/// Toutes les méthodes possèdent une implémentation par défaut vide, permettant
/// aux implémentations de ne redéfinir que les événements pertinents pour elles.
public interface PointsObserver {

    /// Un observateur de points inactif dont toutes les méthodes sont vides.
    /// Utilisé par défaut lorsqu'aucune observation n'est requise (ex: simulations de l'IA).
    PointsObserver EMPTY = new PointsObserver() {};

    /// Invoquée lorsqu'un joueur place une nouvelle tuile sur son mur à la fin d'une manche.
    ///
    /// @param playerId l'identité du joueur concerné
    /// @param line     la ligne de motif à partir de laquelle la tuile a été déplacée
    /// @param color    la couleur de la tuile ajoutée au mur
    /// @param points   le nombre de points gagnés grâce à cet ajout (basé sur les groupes horizontaux et verticaux)
    default void newWallTile(PlayerId playerId, TileDestination.Pattern line,
                             TileKind.Colored color, int points) {}


    /// Invoquée à la fin d'une manche lorsqu'un joueur perd des points en raison des tuiles
    /// présentes sur sa ligne plancher.
    ///
    /// @param playerId l'identité du joueur concerné
    /// @param penalty  le nombre de points déduits (valeur positive représentant la pénalité)
    default void floor(PlayerId playerId, int penalty){}

    /// Invoquée à la fin de la partie lorsqu'un joueur reçoit un bonus pour avoir complété
    /// une ligne horizontale entière sur son mur.
    ///
    /// @param playerId l'identité du joueur concerné
    /// @param line     la ligne du mur qui a été complétée
    /// @param points   le nombre de points de bonus accordés
    default void fullRow(PlayerId playerId, TileDestination.Pattern line, int points) {}


    /// Invoquée à la fin de la partie lorsqu'un joueur reçoit un bonus pour avoir complété
    /// une colonne verticale entière sur son mur.
    ///
    /// @param playerId l'identité du joueur concerné
    /// @param column   l'index de la colonne complétée (compris entre 0 et 4 inclus)
    /// @param points   le nombre de points de bonus accordés
    default void fullColumn(PlayerId playerId, int column, int points){}


    /// Invoquée à la fin de la partie lorsqu'un joueur reçoit un bonus pour avoir placé
    /// les 5 tuiles d'une même couleur sur son mur.
    ///
    /// @param playerId l'identité du joueur concerné
    /// @param color    la couleur qui a été entièrement complétée
    /// @param points   le nombre de points de bonus accordés
    default void fullColor(PlayerId playerId, TileKind.Colored color, int points) {}
}
