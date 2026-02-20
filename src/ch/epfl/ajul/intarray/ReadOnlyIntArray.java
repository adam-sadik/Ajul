package ch.epfl.ajul.intarray;

/// Représente un tableau d'entiers en lecture seule.
///
/// @author Rayane Taoufik Benchekroun (412052)
public interface ReadOnlyIntArray {

    /// retourne la taille du tableau
    /// @return la taille du tableau
    int size();

    /// retourne l'élément d'index i
    /// @param i index d'un élément du tableau
    /// @return l'élement d'index i
    /// @throws IndexOutOfBoundsException si cet index est invalide
    int get(int i);

    /// retourne un tableau d'entier immuable ayant les mêmes éléments
    /// et la même taille que le tableau auquel on applique la méthode.
    /// @return un tableau d'entier immuable ayant les mêmes éléments
    /// et la même taille que le tableau auquel on applique la méthode.
    ImmutableIntArray immutable();

    /// retourne un nouveau tableau Java primitif ayant les mêmes éléments
    /// et la même taille que le tableau auquel on applique la méthode.
    /// @return un nouveau tableau Java primitif ayant les mêmes éléments
    /// et la même taille que le tableau auquel on applique la méthode.
    int[] toArray();

}
