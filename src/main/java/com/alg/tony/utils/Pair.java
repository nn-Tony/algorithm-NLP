/**
 * 
 */
package com.alg.tony.utils;

/**
 * Used for query rewriting and expanding result.
 * @author zhouyang@zhubajie.com
 *
 */
public class Pair<K extends Comparable<K>, V extends Comparable<V>> implements Comparable<Pair<K, V>> {
    public K k = null;
    public V v = null;
    
    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }
    
    public int hashCode() {
        return (k.hashCode() * 31) + v.hashCode();
    }
    
    public boolean equals(Pair<K, V> p) {
        return k.equals(p.k) && v.equals(p.v);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Pair<K, V> o) {
        int diff = k.compareTo(o.k);
        if (0 == diff)
            diff = v.compareTo(o.v);
        return diff;
    }
}
