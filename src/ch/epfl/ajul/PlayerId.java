package ch.epfl.ajul;

import java.util.List;
/// Type énuméré qui permet d'identifier les joueurs.
/// @author Rayane Taoufik Benchekroun (412052).
public enum PlayerId {
    P1,P2,P3,P4;

    /// Liste immuable de tous les joueurs.
    public static final List<PlayerId> ALL = List.of(values());

}
