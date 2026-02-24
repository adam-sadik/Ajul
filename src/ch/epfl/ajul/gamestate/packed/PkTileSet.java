package ch.epfl.ajul.gamestate.packed;

import ch.epfl.ajul.TileKind;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.random.RandomGenerator;

/// Classe utilitaire permettant de manipuler des ensembles de tuiles empaquetés.
/// @author Adam Ghali SADIK (412029)
public final class PkTileSet {

    /// L'ensemble de tuiles vide.
    public static final int EMPTY = 0;

    /// L'ensemble de tuiles plein (20 tuiles de chaque couleur et le marqueur de premier joueur).
    public static final int FULL = computeFull(true);

    /// L'ensemble de tuiles plein, sans le marqueur de premier joueur (20 tuiles de chaque couleur).
    public static final int FULL_COLORED = computeFull(false);

    private static final int TILE_KIND_BITS = 6;
    private static final int TILE_KIND_MASK = (1 << TILE_KIND_BITS) - 1;

    private static int computeFull(boolean includeMarker) {
        int set = EMPTY;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            set = union(set, of(20, color));
        }
        if (includeMarker) {
            set = union(set, of(1, TileKind.FIRST_PLAYER_MARKER));
        }
        return set;
    }

    private static boolean isValid(int pkTileSet) {
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            if (countOf(pkTileSet, color) > 20) {
                return false;
            }
        }
        if (countOf(pkTileSet, TileKind.FIRST_PLAYER_MARKER) > 1) {
            return false;
        }
        return true;
    }

    /// Retourne un ensemble de tuiles empaqueté ne contenant que le nombre spécifié de tuiles de la sorte donnée.
    ///
    /// @param count
    ///        le nombre de tuiles
    /// @param tileKind
    ///        la sorte de tuile
    /// @return l'ensemble de tuiles empaqueté
    public static int of(int count, TileKind tileKind) {
        int result = count << (tileKind.index() * TILE_KIND_BITS);
        assert isValid(result);
        return result;
    }

    /// Retourne vrai si et seulement si l'ensemble de tuiles empaqueté est vide.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @return vrai si l'ensemble est vide, faux sinon
    public static boolean isEmpty(int pkTileSet) {
        return pkTileSet == EMPTY;
    }

    /// Retourne la taille de l'ensemble de tuiles empaqueté, c'est-à-dire le nombre total de tuiles qu'il contient.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @return le nombre total de tuiles
    public static int size(int pkTileSet) {
        int shifted = pkTileSet >>> TILE_KIND_BITS;
        int sum = pkTileSet + shifted;

        int aPlusB = sum & TILE_KIND_MASK;
        int cPlusD = (sum >>> (2 * TILE_KIND_BITS)) & TILE_KIND_MASK;
        int ePlusM = (sum >>> (4 * TILE_KIND_BITS)) & TILE_KIND_MASK;

        return aPlusB + cPlusD + ePlusM;
    }

    /// Retourne le nombre de tuiles de la sorte spécifiée que contient l'ensemble de tuiles empaqueté.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @param tileKind
    ///        la sorte de tuile
    /// @return le nombre de tuiles
    public static int countOf(int pkTileSet, TileKind tileKind) {
        return (pkTileSet >>> (tileKind.index() * TILE_KIND_BITS)) & TILE_KIND_MASK;
    }

    /// Retourne le sous-ensemble de l'ensemble de tuiles empaqueté constitué de toutes les tuiles de la sorte donnée.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @param tileKind
    ///        la sorte de tuile
    /// @return le sous-ensemble empaqueté
    public static int subsetOf(int pkTileSet, TileKind tileKind) {
        return of(countOf(pkTileSet, tileKind), tileKind);
    }

    /// Retourne un ensemble de tuiles empaqueté égal à celui donné, avec exactement une tuile de la sorte donnée en plus.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @param tileKind
    ///        la sorte de tuile à ajouter
    /// @return le nouvel ensemble de tuiles empaqueté
    public static int add(int pkTileSet, TileKind tileKind) {
        int result = pkTileSet + of(1, tileKind);
        assert isValid(result);
        return result;
    }

    /// Retourne un ensemble de tuiles empaqueté égal à celui donné, avec exactement une tuile de la sorte donnée en moins.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @param tileKind
    ///        la sorte de tuile à retirer
    /// @return le nouvel ensemble de tuiles empaqueté
    public static int remove(int pkTileSet, TileKind tileKind) {
        int result = pkTileSet - of(1, tileKind);
        assert isValid(result);
        return result;
    }

    /// Retourne l'union de deux ensembles de tuiles empaquetés.
    ///
    /// @param pkTileSet1
    ///        le premier ensemble empaqueté
    /// @param pkTileSet2
    ///        le deuxième ensemble empaqueté
    /// @return l'union des deux ensembles
    public static int union(int pkTileSet1, int pkTileSet2) {
        int result = pkTileSet1 + pkTileSet2;
        assert isValid(result);
        return result;
    }

    /// Retourne la différence de deux ensembles de tuiles empaquetés (le second devant être un sous-ensemble du premier).
    ///
    /// @param pkTileSet1
    ///        l'ensemble empaqueté principal
    /// @param pkTileSet2
    ///        le sous-ensemble empaqueté à soustraire
    /// @return la différence des deux ensembles
    public static int difference(int pkTileSet1, int pkTileSet2) {
        int result = pkTileSet1 - pkTileSet2;
        assert isValid(result);
        return result;
    }

    /// Copie les tuiles colorées de l'ensemble empaqueté dans le tableau de destination ordonnées par couleur.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @param destination
    ///        le tableau de destination
    /// @return l'index, dans le tableau de destination, de l'élément qui suit le dernier écrit
    public static int copyColoredInto(int pkTileSet, TileKind.Colored[] destination) {
        int index = 0;
        for (TileKind.Colored color : TileKind.Colored.ALL) {
            int count = countOf(pkTileSet, color);
            Arrays.fill(destination, index, index + count, color);
            index += count;
        }
        return index;
    }

    /// Obtient un échantillon aléatoire de l'ensemble et le place dans le tableau à partir de l'index donné.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @param destination
    ///        le tableau de destination
    /// @param offset
    ///        l'index de départ dans le tableau
    /// @param randomGenerator
    ///        le générateur de nombres aléatoires
    /// @return la somme de offset et de la taille de l'ensemble
    public static int sampleColoredInto(int pkTileSet, TileKind.Colored[] destination, int offset, RandomGenerator randomGenerator) {
        int i = offset;

        for (TileKind.Colored color : TileKind.Colored.ALL) {
            int count = countOf(pkTileSet, color);
            for (int k = 0; k < count; k++) {
                if (i < destination.length) {
                    destination[i] = color;
                } else {
                    int j = randomGenerator.nextInt(offset, i + 1);
                    if (j < destination.length) {
                        destination[j] = color;
                    }
                }
                i++;
            }
        }
        return i;
    }

    /// Retourne la représentation textuelle de l'ensemble de tuiles.
    ///
    /// @param pkTileSet
    ///        l'ensemble de tuiles empaqueté
    /// @return la représentation textuelle de l'ensemble
    public static String toString(int pkTileSet) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        for (TileKind kind : TileKind.ALL) {
            int count = countOf(pkTileSet, kind);
            if (count > 0) {
                String name = switch (kind) {
                    case TileKind.Colored c -> c.name();
                    case TileKind.FirstPlayerMarker m -> m.name();
                };
                joiner.add(count + "*" + name);
            }
        }
        return joiner.toString();
    }
}