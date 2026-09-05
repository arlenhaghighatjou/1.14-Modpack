package com.carrotsearch.hppc.comparators;

/**
 * Compares two <code>char</code> values.
 */
@com.carrotsearch.hppc.Generated(date = "2024-06-04T15:20:17+0200", value = "KTypeComparator.java")
public interface CharComparator {
    static <KType> CharComparator naturalOrder() {
        return Character::compare;
    }

    int compare(char a, char b);
}
