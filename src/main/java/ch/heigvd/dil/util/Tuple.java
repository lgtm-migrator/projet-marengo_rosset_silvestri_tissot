package ch.heigvd.dil.util;

/**
 * Contient toutes les données nécessaires pour la construction d'un site statique
 */
public class Tuple<E, T> {
    private final E first;
    private final T second;

    public Tuple(E dataFirst, T dataSecond) {
        this.first = dataFirst;
        this.second = dataSecond;
    }

    public E getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }
}
