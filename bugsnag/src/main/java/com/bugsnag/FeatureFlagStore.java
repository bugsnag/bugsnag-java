package com.bugsnag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Internal storage for feature flags that maintains insertion order.
 * This class is thread-safe for concurrent access.
 */
class FeatureFlagStore {
    // LinkedHashMap maintains insertion order
    private final Map<String, String> flags = new LinkedHashMap<String, String>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Add a feature flag with the specified name and variant.
     * If the name already exists, the variant will be updated without changing position.
     *
     * @param name    the feature flag name
     * @param variant the feature flag variant (can be null)
     */
    void addFeatureFlag(String name, String variant) {
        if (name == null || name.isEmpty()) {
            return;
        }
        lock.writeLock().lock();
        try {
            flags.put(name, variant);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Add multiple feature flags.
     * If any names already exist, their variants will be updated without changing position.
     *
     * @param featureFlags the feature flags to add
     */
    void addFeatureFlags(Collection<FeatureFlag> featureFlags) {
        if (featureFlags == null || featureFlags.isEmpty()) {
            return;
        }

        lock.writeLock().lock();
        try {
            for (FeatureFlag flag : featureFlags) {
                if (flag != null) {
                    flags.put(flag.getName(), flag.getVariant());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Remove the feature flag with the specified name.
     *
     * @param name the feature flag name to remove
     */
    void clearFeatureFlag(String name) {
        if (name != null) {
            lock.writeLock().lock();
            try {
                flags.remove(name);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    /**
     * Remove all feature flags.
     */
    void clearFeatureFlags() {
        lock.writeLock().lock();
        try {
            flags.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get a list of all feature flags in insertion order.
     *
     * @return an unmodifiable list of feature flags
     */
    List<FeatureFlag> toList() {
        lock.readLock().lock();
        try {
            List<FeatureFlag> result = new ArrayList<>(flags.size());
            for (Map.Entry<String, String> entry : flags.entrySet()) {
                FeatureFlag flag = FeatureFlag.of(entry.getKey(), entry.getValue());
                if (flag != null) {
                    result.add(flag);
                }
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Create a copy of this store with all the same flags.
     *
     * @return a new FeatureFlagStore with the same flags
     */
    FeatureFlagStore copy() {
        FeatureFlagStore copy = new FeatureFlagStore();
        lock.readLock().lock();
        try {
            copy.flags.putAll(this.flags);
        } finally {
            lock.readLock().unlock();
        }
        return copy;
    }

    /**
     * Merge flags from another store into this one.
     * Flags from the other store will overwrite existing flags with the same name,
     * but will not change the position of existing flags.
     *
     * @param other the other store to merge from
     */
    void merge(FeatureFlagStore other) {
        if (other == null || other == this) {
            return;
        }

        // Warning: this *looks* like a classic deadlock pattern, but because this method is only ever called
        // with isolated copies of FeatureFlagStore, the locks will never actually be contended.
        // If this method were to be called with two live stores, then it would be possible for a deadlock to occur.
        other.lock.readLock().lock();
        try {
            // we don't use other.size() because it grabs a lock.readLock() again
            if (other.flags.isEmpty()) {
                return;
            }

            lock.writeLock().lock();
            try {
                flags.putAll(other.flags);
            } finally {
                lock.writeLock().unlock();
            }
        } finally {
            other.lock.readLock().unlock();
        }
    }

    /**
     * Get the number of feature flags.
     *
     * @return the number of feature flags
     */
    int size() {
        lock.readLock().lock();
        try {
            return flags.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
