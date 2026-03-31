package ch.epfl.ajul.gamestate.packed;


/// Classe utilitaire permettant de manipuler des ensembles d'entiers empaquetés dans un `int`.
/// L'ensemble peut contenir des entiers compris entre 0 et 31 inclus.
/// Le bit d'index i correspond à la présence de l'entier i dans l'ensemble.
/// @author Rayane Taoufik Benchekroun (412052)
/// @author Adam Ghali SADIK (412029)
public final class PkIntSet32 {

    /// Représente un ensemble vide.
    public static final int EMPTY = 0;

    /// Indique si l'ensemble contient l'entier donné.
    /// @param pkIntSet32 L'ensemble empaqueté.
    /// @param i L'entier dont on veut vérifier la présence (doit être entre 0 et 31).
    /// @return Vrai si l'entier i est présent dans l'ensemble.
    public static boolean contains(int pkIntSet32, int i) {
        assert i >= 0 && i < Integer.SIZE;
        return ((pkIntSet32 >>> i) & 1) == 1;
    }

    /// Indique si le premier ensemble contient la totalité des éléments du second.
    /// @param pkIntSet32a L'ensemble supposé contenant.
    /// @param pkIntSet32b L'ensemble dont on cherche les éléments.
    /// @return Vrai si pkIntSet32a est un sur-ensemble de pkIntSet32b.
    public static boolean containsAll(int pkIntSet32a, int pkIntSet32b) {
        return (pkIntSet32a & pkIntSet32b) == pkIntSet32b;
    }

    /// Ajoute un entier à l'ensemble empaqueté.
    /// @param pkIntSet32 L'ensemble d'origine.
    /// @param i L'entier à ajouter (doit être entre 0 et 31).
    /// @return Un nouvel ensemble empaqueté contenant i.
    public static int add(int pkIntSet32, int i) {
        assert i >= 0 && i < Integer.SIZE;
        return pkIntSet32 | (1 << i);
    }

    /// Retire un entier de l'ensemble empaqueté.
    /// @param pkIntSet32 L'ensemble d'origine.
    /// @param i L'entier à retirer (doit être entre 0 et 31).
    /// @return Un nouvel ensemble empaqueté ne contenant plus i.
    public static int remove(int pkIntSet32, int i) {
        assert i >= 0 && i < Integer.SIZE;
        return pkIntSet32 & ~(1 << i);
    }


}
