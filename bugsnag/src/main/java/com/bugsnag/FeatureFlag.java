package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Objects;

/**
 * Represents a feature flag with a name and optional variant.
 * Feature flags can be used to annotate events with information about
 * active experiments or A/B tests.
 */
public class FeatureFlag {
    private final String name;
    private final String variant;

    /**
     * Create a feature flag with a name and no variant.
     *
     * @param name the name of the feature flag
     */
    public FeatureFlag(String name) {
        this(name, null);
    }

    /**
     * Create a feature flag with a name and variant.
     *
     * @param name the name of the feature flag
     * @param variant the variant of the feature flag (can be null)
     */
    public FeatureFlag(String name, String variant) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Feature flag name cannot be null or empty");
        }
        this.name = name;
        this.variant = variant;
    }

    /**
     * Get the name of the feature flag.
     *
     * @return the feature flag name
     */
    @Expose
    public String getName() {
        return name;
    }

    /**
     * Get the variant of the feature flag.
     *
     * @return the feature flag variant, or null if not set
     */
    @Expose
    public String getVariant() {
        return variant;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FeatureFlag that = (FeatureFlag) obj;
        return Objects.equals(name, that.name) && Objects.equals(variant, that.variant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, variant);
    }

    @Override
    public String toString() {
        return "FeatureFlag{name='" + name + "', variant='" + variant + "'}";
    }
}
