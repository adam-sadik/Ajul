package ch.epfl.ajul;

import java.util.List;
/// Type énuméré qui permet d'identifier les joueurs.
/// @author Rayane Taoufik Benchekroun (412052).
public enum PlayerId {
    /// Joueur 1
    P1,
    /// Joueur 2
    P2,
    ///  Joueur 3
    P3,
    ///  Joeuur 4
    P4;


    /// Liste immuable de tous les joueurs.
    public static final List<PlayerId> ALL = List.of(values());

}
