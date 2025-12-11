package org.mch.colls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * A fixed-size ring buffer with queue semantics that overwrites the oldest
 * element when full. Null elements are not permitted.
 *
 * @param <T> element type
 */
public class RingBuffer<T> implements Iterable<T> {
    private final Object[] elements;
    private final int capacity;
    private int head; // index of the oldest element
    private int size;

    /**
     * Creates a ring buffer with fixed {@code capacity}.
     *
     * @param capacity maximum number of elements that can be stored
     */
    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.elements = new Object[capacity];
    }

    /**
     * Adds an element to the buffer, overwriting the oldest when full.
     *
     * @param element value to add (must not be null)
     * @return the evicted element when overwriting, or {@link Optional#empty()} otherwise
     * @throws NullPointerException if {@code element} is null
     */
    public Optional<T> add(T element) {
        Objects.requireNonNull(element, "element");

        if (isFull()) {
            @SuppressWarnings("unchecked")
            T evicted = (T) elements[head];
            elements[head] = element;
            head = (head + 1) % capacity;
            return Optional.of(evicted);
        }

        int tail = (head + size) % capacity;
        elements[tail] = element;
        size++;
        return Optional.empty();
    }

    /**
     * Returns, but does not remove, the oldest element.
     *
     * @return the oldest element, or empty if buffer is empty
     */
    public Optional<T> peek() {
        if (isEmpty()) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        T value = (T) elements[head];
        return Optional.of(value);
    }

    /**
     * Retrieves and removes the oldest element.
     *
     * @return the removed element, or empty if buffer is empty
     */
    public Optional<T> poll() {
        if (isEmpty()) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        T value = (T) elements[head];
        elements[head] = null;
        head = (head + 1) % capacity;
        size--;
        return Optional.of(value);
    }

    /** Removes all elements and resets indices. */
    public void clear() {
        if (size == 0) {
            return;
        }

        for (int i = 0; i < size; i++) {
            elements[(head + i) % capacity] = null;
        }
        head = 0;
        size = 0;
    }

    /**
     * Number of stored elements.
     *
     * @return current element count
     */
    public int size() {
        return size;
    }

    /**
     * Returns true when {@link #size()} equals the fixed capacity.
     *
     * @return whether the buffer is full
     */
    public boolean isFull() {
        return size == capacity;
    }

    /**
     * Indicates whether no elements are stored.
     *
     * @return true if empty, else false
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns contents from oldest to newest.
     *
     * @return snapshot list in encounter order
     */
    public List<T> toList() {
        List<T> result = new ArrayList<>(size);
        for (T value : this) {
            result.add(value);
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                @SuppressWarnings("unchecked")
                T value = (T) elements[(head + cursor) % capacity];
                cursor++;
                return value;
            }
        };
    }
}
