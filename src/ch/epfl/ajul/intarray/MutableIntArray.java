package ch.epfl.ajul.intarray;
/// Représente un tableau d'entiers non immuable qui "emballe" un tableau primitif.
/// Si le tableau primitif d'origine est modifié, cette instance le sera aussi.
///
/// @author Rayane Taoufik Benchekroun (412052)
public final class MutableIntArray extends AbstractIntArray {

    /// Construit un tableau mutable.
    /// Privé pour forcer l'utilisation de la méthode fabrique wrapping.
    ///
    /// @param array le tableau primitif à emballer
    private MutableIntArray ( int[] array ){
        super(array);
    }

    /// Méthode fabrique qui emballe simplement le tableau reçu sans le copier.
    ///
    /// @param array le tableau primitif à emballer directement
    /// @return une nouvelle instance de MutableIntArray emballant le tableau
    public static MutableIntArray wrapping ( int [] array){
        return new MutableIntArray(array);
    }

}
