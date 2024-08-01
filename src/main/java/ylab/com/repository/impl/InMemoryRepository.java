package ylab.com.repository.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public abstract class InMemoryRepository<K extends Comparable, T> {
    protected final TreeMap<K, T> storage;
    protected long index;
    public InMemoryRepository() {
        storage = new TreeMap<>();
    }

    public Optional<T> findBy(K key) {
        return Optional.ofNullable(storage.get(key));
    }

    protected T save(K key,T o) {
        storage.put(key, o);
        return o;
    }

    protected Optional<T> findByKey(K key) {
        return Optional.ofNullable(storage.get(key));
    }

    protected Collection<T> getAll() {
        return storage.values();
    }

    protected T removeById(K key) {
        return storage.remove(key);
    }
}
