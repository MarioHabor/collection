package org.mch.colls;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RingBufferTest {

    @Test
    void addsWithoutEvictionUntilFull() {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);

        assertEquals(Optional.empty(), buffer.add(1));
        assertEquals(Optional.empty(), buffer.add(2));
        assertEquals(Optional.empty(), buffer.add(3));

        assertEquals(3, buffer.size());
        assertTrue(buffer.isFull());
        assertEquals(Optional.of(1), buffer.peek());
        assertEquals(List.of(1, 2, 3), buffer.toList());
    }

    @Test
    void addOverwritesOldestWhenFull() {
        RingBuffer<String> buffer = new RingBuffer<>(2);
        buffer.add("a");
        buffer.add("b");

        assertEquals(Optional.of("a"), buffer.add("c"));
        assertEquals(List.of("b", "c"), buffer.toList());

        assertEquals(Optional.of("b"), buffer.add("d"));
        assertEquals(List.of("c", "d"), buffer.toList());
        assertEquals(2, buffer.size());
    }

    @Test
    void peekAndPollFollowQueueOrder() {
        RingBuffer<Integer> buffer = new RingBuffer<>(2);
        buffer.add(10);
        buffer.add(20);

        assertEquals(Optional.of(10), buffer.peek());
        assertEquals(Optional.of(10), buffer.poll());
        assertEquals(Optional.of(20), buffer.poll());
        assertTrue(buffer.poll().isEmpty());
        assertTrue(buffer.isEmpty());
    }

    @Test
    void clearResetsState() {
        RingBuffer<Integer> buffer = new RingBuffer<>(2);
        buffer.add(1);
        buffer.add(2);

        buffer.clear();
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());

        buffer.add(3);
        assertEquals(List.of(3), buffer.toList());
    }

    @Test
    void iteratorRespectsEncounterOrder() {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        buffer.add(4); // overwrites 1

        List<Integer> iterated = new ArrayList<>();
        for (Integer value : buffer) {
            iterated.add(value);
        }

        assertEquals(List.of(2, 3, 4), iterated);
    }

    @Test
    void rejectsNullElements() {
        RingBuffer<String> buffer = new RingBuffer<>(1);
        assertThrows(NullPointerException.class, () -> buffer.add(null));
    }
}
