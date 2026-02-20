package ch.epfl.ajul;

import java.util.List;

/// Identifie une destination de tuiles (ligne de motif ou ligne plancher).
///
/// @author Adam Ghali SADIK (412029)
public sealed interface TileDestination {

    /// La ligne de motif 1 (index 0).
    TileDestination PATTERN_1 = Pattern.PATTERN_1;
    /// La ligne de motif 2 (index 1).
    TileDestination PATTERN_2 = Pattern.PATTERN_2;
    /// La ligne de motif 3 (index 2).
    TileDestination PATTERN_3 = Pattern.PATTERN_3;
    /// La ligne de motif 4 (index 3).
    TileDestination PATTERN_4 = Pattern.PATTERN_4;
    /// La ligne de motif 5 (index 4).
    TileDestination PATTERN_5 = Pattern.PATTERN_5;
    /// La ligne plancher (index 5).
    TileDestination FLOOR = Floor.FLOOR;

    /// Liste immuable de toutes les destinations possibles.
    List<TileDestination> ALL = List.of(PATTERN_1, PATTERN_2, PATTERN_3, PATTERN_4, PATTERN_5, FLOOR);
    /// Nombre total de destinations (6).
    int COUNT = ALL.size();

    /// Retourne l'index de la destination.
    /// @return l'index (0-4 pour les motifs, 5 pour le plancher)
    int index();

    /// Retourne la capacité maximale de la destination.
    /// @return le nombre de tuiles maximum acceptées
    int capacity();

    /// Représente les lignes de motif du plateau.
    ///
    /// @author Adam Ghali SADIK (412029)
    enum Pattern implements TileDestination {
        PATTERN_1, PATTERN_2, PATTERN_3, PATTERN_4, PATTERN_5;

        /// Liste immuable de toutes les lignes de motif.
        public static final List<TileDestination.Pattern> ALL = List.of(values());
        /// Nombre de lignes de motif (5).
        public static final int COUNT = ALL.size();

        @Override
        public int index() {
            return ordinal();
        }

        @Override
        public int capacity() {
            return index() + 1;
        }
    }

    /// Représente la ligne plancher du plateau.
    ///
    /// @author Adam Ghali SADIK (412029)
    enum Floor implements TileDestination {
        FLOOR;

        @Override
        public int index() {
            return 5;
        }

        @Override
        public int capacity() {
            return 7;
        }
    }
}