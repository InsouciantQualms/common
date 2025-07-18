/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the Listener functional interface covering event notification and handling. */
public final class ListenerTest {

    @Test
    public void testListenerNotifyReturnValue() {

        final var listener = new TestListener();

        assertTrue(listener.notify("test event"));
        assertFalse(listener.notify("unhandled event"));
    }

    @Test
    public void testListenerReceivesEvent() {

        final var events = new ArrayList<String>();
        final Listener<String> listener = event -> {
            events.add(event);
            return true;
        };

        listener.notify("first event");
        listener.notify("second event");

        assertEquals(2, events.size());
        assertEquals("first event", events.get(0));
        assertEquals("second event", events.get(1));
    }

    @Test
    public void testListenerWithDifferentTypes() {

        final var intListener = new TypedListener<Integer>();
        final var stringListener = new TypedListener<String>();

        assertTrue(intListener.notify(42));
        assertTrue(stringListener.notify("hello"));

        assertEquals(42, intListener.lastEvent);
        assertEquals("hello", stringListener.lastEvent);
    }

    @Test
    public void testListenerChaining() {

        final var events = new ArrayList<String>();
        final Listener<String> listener1 = event -> {
            events.add("listener1: " + event);
            return true;
        };

        final Listener<String> listener2 = event -> {
            events.add("listener2: " + event);
            return true;
        };

        listener1.notify("test");
        listener2.notify("test");

        assertEquals(2, events.size());
        assertEquals("listener1: test", events.get(0));
        assertEquals("listener2: test", events.get(1));
    }

    @Test
    public void testListenerHandlesNull() {

        final var nullListener = new TypedListener<String>();

        assertTrue(nullListener.notify(null));
        assertNull(nullListener.lastEvent);
    }

    @Test
    public void testListenerAsLambda() {

        final var handled = new boolean[1];
        final Listener<String> listener = event -> {
            handled[0] = (event != null) && event.startsWith("test");
            return handled[0];
        };

        assertTrue(listener.notify("test event"));
        assertTrue(handled[0]);

        assertFalse(listener.notify("other event"));
        assertFalse(handled[0]);
    }

    @Test
    public void testListenerWithComplexEvent() {

        final var eventListener = new TypedListener<TestEvent>();
        final var event = new TestEvent("test", 42);

        assertTrue(eventListener.notify(event));
        assertEquals(event, eventListener.lastEvent);
        assertEquals("test", eventListener.lastEvent.name);
        assertEquals(42, eventListener.lastEvent.value);
    }

    @Test
    public void testMultipleListenersForSameEvent() {

        final var results = new ArrayList<String>();
        final var listeners = List.of(
                event -> {
                    results.add("A: " + event);
                    return true;
                },
                event -> {
                    results.add("B: " + event);
                    return false;
                },
                (Listener<String>) event -> {
                    results.add("C: " + event);
                    return true;
                });

        listeners.forEach(listener -> listener.notify("event"));

        assertEquals(3, results.size());
        assertEquals("A: event", results.get(0));
        assertEquals("B: event", results.get(1));
        assertEquals("C: event", results.get(2));
    }

    @Test
    public void testListenerWithException() {

        final Listener<String> listener = event -> {
            if ("error".equals(event)) {
                throw new RuntimeException("Test exception");
            }
            return true;
        };

        assertTrue(listener.notify("good"));
        assertThrows(RuntimeException.class, () -> listener.notify("error"));
    }

    /** Simple test listener implementation. */
    private static final class TestListener implements Listener<String> {

        @Override
        public boolean notify(final String event) {

            return "test event".equals(event);
        }
    }

    /** Generic typed listener for testing. */
    private static final class TypedListener<T> implements Listener<T> {

        private T lastEvent;

        @Override
        public boolean notify(final T event) {

            lastEvent = event;
            return true;
        }
    }

    /** Simple test event record. */
    private record TestEvent(String name, int value) {}
}
