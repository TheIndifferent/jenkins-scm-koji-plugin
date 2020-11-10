package org.fakekoji.core.utils.matrix.cell;

public interface Cell {
    default boolean isEmpty() {
        return false;
    }
    
    default int getSpan() {
        return 1;
    }

    default int size() {
        return 1;
    }
}
