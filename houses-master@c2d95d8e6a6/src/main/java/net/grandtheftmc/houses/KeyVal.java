package net.grandtheftmc.houses;

public class KeyVal<K, V> {
    private final K key;
    private final V val;

    public KeyVal(K key, V val) {
        this.key = key;
        this.val = val;
    }

    public K getKey() {
        return key;
    }

    public V getVal() {
        return val;
    }
}
