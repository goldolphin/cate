package net.goldolphin.cate;

/**
 * A data store to be used for reading & writing key-value-paired data.
 * @param <K> key type.
 * @param <V> value type.
 * @author goldolphin
 *         2014-10-01 11:12
 */
public interface IStore<K, V> {
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this store contains no mapping for the key.
     * @param key
     * @return
     */
    public V get(K key);

    /**
     * Associates the specified value with the specified key in this store
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     * @param key
     * @param value
     */
    public void put(K key, V value);

    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).
     * <p>Returns the value to which this map previously associated the key,
     * or <tt>null</tt> if the map contained no mapping for the key.
     * @param key
     * @return
     */
    public V remove(K key);

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified key.
     * @param key
     * @return
     */
    public boolean contains(K key);
}
