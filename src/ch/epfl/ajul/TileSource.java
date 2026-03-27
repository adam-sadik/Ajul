package ch.epfl.ajul;

import java.util.List;

/// Identifie une source de tuiles (la zone centrale ou l'une des fabriques).
///
/// @author Adam Ghali SADIK (412029)
public sealed interface TileSource {

    /// La zone centrale du jeu.
    TileSource CENTER_AREA = CenterArea.CENTER_AREA;
    /// La fabrique numéro 1.
    TileSource FACTORY_1 = Factory.FACTORY_1;
    /// La fabrique numéro 2.
    TileSource FACTORY_2 = Factory.FACTORY_2;
    /// La fabrique numéro 3.
    TileSource FACTORY_3 = Factory.FACTORY_3;
    /// La fabrique numéro 4.
    TileSource FACTORY_4 = Factory.FACTORY_4;
    /// La fabrique numéro 5.
    TileSource FACTORY_5 = Factory.FACTORY_5;
    /// La fabrique numéro 6.
    TileSource FACTORY_6 = Factory.FACTORY_6;
    /// La fabrique numéro 7.
    TileSource FACTORY_7 = Factory.FACTORY_7;
    /// La fabrique numéro 8.
    TileSource FACTORY_8 = Factory.FACTORY_8;
    /// La fabrique numéro 9.
    TileSource FACTORY_9 = Factory.FACTORY_9;

    /// Liste immuable de toutes les sources de tuiles (index 0 pour le centre, puis fabriques).
    List<TileSource> ALL = List.of(CENTER_AREA, FACTORY_1, FACTORY_2, FACTORY_3, FACTORY_4, FACTORY_5, FACTORY_6, FACTORY_7, FACTORY_8, FACTORY_9);
    /// Nombre total de sources possibles (10).
    int COUNT = ALL.size();

    /// Retourne l'index de la source de tuiles.
    /// @return l'index (0 pour la zone centrale, 1-9 pour les fabriques)
    int index();

    /// Représente la zone centrale.
    ///
    /// @author Adam Ghali SADIK (412029)
    enum CenterArea implements TileSource {
        CENTER_AREA;

        @Override
        public int index() {
            return 0;
        }
    }

    /// Représente l'une des fabriques de tuiles.
    ///
    /// @author Adam Ghali SADIK (412029)
    enum Factory implements TileSource {
        FACTORY_1, FACTORY_2, FACTORY_3, FACTORY_4, FACTORY_5, FACTORY_6, FACTORY_7, FACTORY_8, FACTORY_9;

        /// Nombre de tuiles contenues par fabrique au début d'une manche (4).
        public static final int TILES_PER_FACTORY = 4;
        /// Liste immuable de toutes les fabriques.
        public static final List<Factory> ALL = List.of(values());
        /// Nombre total de fabriques (9).
        public static final int COUNT = ALL.size();

        @Override
        public int index() {
            return ordinal() + 1;
        }
    }
}