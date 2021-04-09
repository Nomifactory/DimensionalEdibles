package jackyy.dimensionaledibles.util;

import mcp.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;

/**
 * Specialization of HashMap which adds the ability to interact with fields of a potentially nonexistent key,
 * using a lazily-evaluated fallback if the key does not exist.
 *
 * @param <K> Class of the map keys
 * @param <V> Class of the map values
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Cache<K, V> extends HashMap<K, V> {
    /**
     * @param key          the key of the map entry
     * @param ifFunction   produces data from the value of the map entry if the key exists
     * @param elseFunction produces the alternative result if the map contains no such key
     * @param <T>          the type of value returned
     * @return the value produced by {@code ifFunction} if the map contains an entry with the specified key, otherwise the
     * result of {@code elseFunction.get()}.
     */
    public <T> T getPropertyIfPresentOrElse(K key,
                                            Function<V, T> ifFunction,
                                            Supplier<T> elseFunction) {
        if (!this.containsKey(key))
            return elseFunction.get();
        return ifFunction.apply(this.get(key));
    }

    /**
     * @param key        the key of the map entry
     * @param ifFunction produces data from the value of the map entry if the key exists
     * @param <T>        the type of value returned
     * @return the value produced by {@code ifFunction} if the map contains an entry with the specified key,
     * otherwise {@code null}.
     * @see Cache#getPropertyIfPresentOrElse(Object, Function, Supplier)
     */
    @Nullable
    public <T> T getPropertyIfPresentOrNull(K key,
                                            Function<V, T> ifFunction) {
        return this.getPropertyIfPresentOrElse(key, ifFunction, () -> null);
    }
}
