package ch.epfl.ajul.intarray;

import java.util.Arrays;

/// Classe-mère abstraite pour les différentes mises en œuvre de tableaux d'entiers.
/// @author Rayane Taoufik Benchekroun (412052)
public abstract class AbstractIntArray implements ReadOnlyIntArray{

    private final int[] elements;

    /// Construit un tableau d'entiers à partir d'un tableau primitif.
    /// Ce constructeur est destiné à être utilisé par les sous-classes.
    ///
    /// @param elements le tableau primitif contenant les éléments à stocker
    protected AbstractIntArray ( int[] elements){
        this.elements = elements;
    }

    /// @return la taille du tableau
    @Override
    public int size() {
        return elements.length;
    }

    /// @param i index d'un élément du tableau
    /// @throws IndexOutOfBoundsException si cet index est invalide
    /// @return l'élement d'index i
    @Override
    public int get(int i) {
        return elements[i];
    }

    /// @return un tableau d'entier immuable ayant les mêmes éléments
    /// et la même taille qu ele tableau auquel on applique la méthode.
    @Override
    public ImmutableIntArray immutable() {
        return ImmutableIntArray.copyOf(elements);
    }

    /// @return une copie du tableau primitif interne (obtenue par clonage)
    @Override
    public int[] toArray() {
        return elements.clone();
    }

    /// @return une représentation textuelle du tableau identique à Arrays.toString
    @Override
    public String toString() {
        return Arrays.toString(elements) ;
    }
}
