package ch.heigvd.dil.util;

/**
 * Permet de stocker 2 données en une seule
 *
 * @author Géraud Silvestri
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
