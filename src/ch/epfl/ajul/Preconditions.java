package ch.epfl.ajul;

/// Classe utilitaire permettant de vérifier les préconditions des méthodes.
///
/// @author Rayane TAOUFIK BENCHEKROUN (412052)
public final class Preconditions {

    /// Vérifie qu'une condition donnée est vraie.
    ///
    /// @param shouldBeTrue
    ///        la condition à vérifier
    /// @throws IllegalArgumentException
    ///         si la condition est fausse.
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
