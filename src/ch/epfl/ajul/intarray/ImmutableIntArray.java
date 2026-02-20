package ch.epfl.ajul.intarray;

/// Représente un tableau d'entiers immuable dont le contenu ne peut pas changer après sa création.
///
/// @author Rayane Taoufik Benchekroun (412052)
public final class ImmutableIntArray extends AbstractIntArray {

    /// Construit un tableau immuable.
    /// Privé pour forcer l'utilisation de la méthode fabrique copyOf.
    ///
    /// @param array le tableau primitif déjà copié
    private ImmutableIntArray (int [] array) {
        super(array);
    }

    /// @param array le tableau primitif à copier
    /// @return un nouveau tableau d'entiers immuable dont la taille
    /// et les éléments sont les mêmes que ceux du tableau primitif array.
    public static ImmutableIntArray copyOf (int[] array){
        return new ImmutableIntArray(array.clone());
    }

    /// @return le récepteur lui-même (this), car il est déjà immuable
    @Override
    public ImmutableIntArray immutable() {
        return this;
    }
}
