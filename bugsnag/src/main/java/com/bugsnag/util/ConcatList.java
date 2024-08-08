package com.bugsnag.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConcatList<E> extends AbstractList<E> {
    private final List<E> combined;

    /**
     * Constructs a concatenation of two lists, removing duplicates.
     *
     * @param first  the first list of elements to concatenate.
     * @param second the second list of elements to concatenate.
     * @throws NullPointerException if either list is null.
     */
    public ConcatList(List<E> first, List<E> second) {
        if (first == null || second == null) {
            throw new NullPointerException("lists must not be null");
        }
        Set<E> uniqueElements = new LinkedHashSet<>();
        uniqueElements.addAll(first);
        uniqueElements.addAll(second);
        this.combined = new ArrayList<>(uniqueElements);
    }

    @Override
    public int size() {
        return combined.size();
    }

    @Override
    public E get(int index) {
        return combined.get(index);
    }
}
