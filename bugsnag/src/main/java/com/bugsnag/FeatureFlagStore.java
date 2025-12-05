package com.bugsnag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal storage for feature flags that maintains insertion order.
 * This class is thread-safe for concurrent access.
 */
class FeatureFlagStore {
    // LinkedHashMap maintains insertion order
    private final Map<String, String> flags = new LinkedHashMap<String, String>();

    /**
     * Add a feature flag with the specified name and variant.
     * If the name already exists, the variant will be updated without changing position.
     *
     * @param name the feature flag name
     * @param variant the feature flag variant (can be null)
     */
    synchronized void addFeatureFlag(String name, String variant) {
        if (name == null || name.isEmpty()) {
            return;
        }
        flags.put(name, variant);
    }

    /**
     * Add multiple feature flags.
     * If any names already exist, their variants will be updated without changing position.
     *
     * @param featureFlags the feature flags to add
     */
    synchronized void addFeatureFlags(Collection<FeatureFlag> featureFlags) {
        if (featureFlags == null) {
            return;
        }
        for (FeatureFlag flag : featureFlags) {
            if (flag != null) {
                addFeatureFlag(flag.getName(), flag.getVariant());
            }
        }
    }

    /**
     * Remove the feature flag with the specified name.
     *
     * @param name the feature flag name to remove
     */
    synchronized void clearFeatureFlag(String name) {
        if (name != null) {
            flags.remove(name);
        }
    }

    /**
     * Remove all feature flags.
     */
    synchronized void clearFeatureFlags() {
        flags.clear();
    }

    /**
     * Get a list of all feature flags in insertion order.
     *
     * @return an unmodifiable list of feature flags
     */
    synchronized List<FeatureFlag> toList() {
        List<FeatureFlag> result = new ArrayList<FeatureFlag>(flags.size());
        for (Map.Entry<String, String> entry : flags.entrySet()) {
            result.add(new FeatureFlag(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * Create a copy of this store with all the same flags.
     *
     * @return a new FeatureFlagStore with the same flags
     */
    synchronized FeatureFlagStore copy() {
        FeatureFlagStore copy = new FeatureFlagStore();
        copy.flags.putAll(this.flags);
        return copy;
    }

    /**
     * Merge flags from another store into this one.
     * Flags from the other store will overwrite existing flags with the same name,
     * but will not change the position of existing flags.
     *
     * @param other the other store to merge from
     */
    synchronized void merge(FeatureFlagStore other) {
        if (other == null) {
            return;
        }
        synchronized (other) {
            for (Map.Entry<String, String> entry : other.flags.entrySet()) {
                flags.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Get the number of feature flags.
     *
     * @return the number of feature flags
     */
    synchronized int size() {
        return flags.size();
    }
}
