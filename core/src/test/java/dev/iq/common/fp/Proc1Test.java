/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import dev.iq.common.error.IoException;
import dev.iq.common.error.UnexpectedException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Proc1 functional interface covering consumer functionality.
 */
public final class Proc1Test {

    @Test
    public void testAsTryWithSuccessfulConsumer() {

        final var result = new AtomicReference<String>();
        final var consumer = (Proc1<String>) result::set;
        final var wrappedConsumer = Proc1.asTry(consumer);
        
        assertNotNull(wrappedConsumer);
        assertTrue(wrappedConsumer instanceof Consumer);
        
        assertDoesNotThrow(() -> wrappedConsumer.accept("test"));
        assertEquals("test", result.get());
    }

    @Test
    public void testAsTryWithExceptionThrowingConsumer() {

        final var consumer = (Proc1<String>) (s) -> {
            throw new IOException("Test exception");
        };
        
        final var wrappedConsumer = Proc1.asTry(consumer);
        
        assertThrows(UnexpectedException.class, () -> {
            wrappedConsumer.accept("test");
        });
    }

    @Test
    public void testAsTryWithNullInput() {

        final var result = new AtomicReference<String>();
        final var consumer = (Proc1<String>) result::set;
        final var wrappedConsumer = Proc1.asTry(consumer);
        
        assertDoesNotThrow(() -> wrappedConsumer.accept(null));
        assertNull(result.get());
    }

    @Test
    public void testAsIoWithSuccessfulConsumer() {

        final var result = new AtomicReference<String>();
        final var consumer = (Proc1<String>) result::set;
        final var wrappedConsumer = Proc1.asIo(consumer);
        
        assertNotNull(wrappedConsumer);
        assertTrue(wrappedConsumer instanceof Consumer);
        
        assertDoesNotThrow(() -> wrappedConsumer.accept("test"));
        assertEquals("test", result.get());
    }

    @Test
    public void testAsIoWithExceptionThrowingConsumer() {

        final var consumer = (Proc1<String>) (s) -> {
            throw new IOException("Test exception");
        };
        
        final var wrappedConsumer = Proc1.asIo(consumer);
        
        assertThrows(IoException.class, () -> {
            wrappedConsumer.accept("test");
        });
    }

    @Test
    public void testAsIoWithNullInput() {

        final var result = new AtomicReference<String>();
        final var consumer = (Proc1<String>) result::set;
        final var wrappedConsumer = Proc1.asIo(consumer);
        
        assertDoesNotThrow(() -> wrappedConsumer.accept(null));
        assertNull(result.get());
    }

    @Test
    public void testAsTryExceptionWrapping() {

        final var consumer = (Proc1<String>) (s) -> {
            throw new RuntimeException("Original exception");
        };
        
        final var wrappedConsumer = Proc1.asTry(consumer);
        final var thrown = assertThrows(UnexpectedException.class, () -> {
            wrappedConsumer.accept("test");
        });
        
        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testAsIoExceptionWrapping() {

        final var consumer = (Proc1<String>) (s) -> {
            throw new RuntimeException("Original exception");
        };
        
        final var wrappedConsumer = Proc1.asIo(consumer);
        final var thrown = assertThrows(IoException.class, () -> {
            wrappedConsumer.accept("test");
        });
        
        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testDirectProc1Usage() {

        final var result = new AtomicReference<String>();
        final var proc1 = new Proc1<String>() {
            @Override
            public void accept(final String s) throws Exception {
                result.set(s);
            }
        };
        
        assertDoesNotThrow(() -> {
            proc1.accept("direct");
        });
        assertEquals("direct", result.get());
    }

    @Test
    public void testDirectProc1WithException() {

        final var proc1 = new Proc1<String>() {
            @Override
            public void accept(final String s) throws Exception {
                throw new Exception("Direct exception");
            }
        };
        
        assertThrows(Exception.class, () -> {
            proc1.accept("test");
        });
    }

    @Test
    public void testAsTryWithDifferentTypes() {

        final var stringResult = new AtomicReference<String>();
        final var intResult = new AtomicReference<Integer>();
        
        final var stringConsumer = (Proc1<String>) stringResult::set;
        final var intConsumer = (Proc1<Integer>) intResult::set;
        
        final var wrappedStringConsumer = Proc1.asTry(stringConsumer);
        final var wrappedIntConsumer = Proc1.asTry(intConsumer);
        
        wrappedStringConsumer.accept("hello");
        wrappedIntConsumer.accept(42);
        
        assertEquals("hello", stringResult.get());
        assertEquals(42, intResult.get());
    }

    @Test
    public void testAsIoWithDifferentTypes() {

        final var stringResult = new AtomicReference<String>();
        final var intResult = new AtomicReference<Integer>();
        
        final var stringConsumer = (Proc1<String>) stringResult::set;
        final var intConsumer = (Proc1<Integer>) intResult::set;
        
        final var wrappedStringConsumer = Proc1.asIo(stringConsumer);
        final var wrappedIntConsumer = Proc1.asIo(intConsumer);
        
        wrappedStringConsumer.accept("hello");
        wrappedIntConsumer.accept(42);
        
        assertEquals("hello", stringResult.get());
        assertEquals(42, intResult.get());
    }

    @Test
    public void testAsTryWithDifferentExceptionTypes() {

        final var ioConsumer = (Proc1<String>) (s) -> {
            throw new IOException("IO exception");
        };
        
        final var runtimeConsumer = (Proc1<String>) (s) -> {
            throw new RuntimeException("Runtime exception");
        };
        
        final var checkedConsumer = (Proc1<String>) (s) -> {
            throw new Exception("Checked exception");
        };
        
        final var ioResult = Proc1.asTry(ioConsumer);
        final var runtimeResult = Proc1.asTry(runtimeConsumer);
        final var checkedResult = Proc1.asTry(checkedConsumer);
        
        assertThrows(UnexpectedException.class, () -> ioResult.accept("test"));
        assertThrows(UnexpectedException.class, () -> runtimeResult.accept("test"));
        assertThrows(UnexpectedException.class, () -> checkedResult.accept("test"));
    }

    @Test
    public void testAsIoWithDifferentExceptionTypes() {

        final var ioConsumer = (Proc1<String>) (s) -> {
            throw new IOException("IO exception");
        };
        
        final var runtimeConsumer = (Proc1<String>) (s) -> {
            throw new RuntimeException("Runtime exception");
        };
        
        final var checkedConsumer = (Proc1<String>) (s) -> {
            throw new Exception("Checked exception");
        };
        
        final var ioResult = Proc1.asIo(ioConsumer);
        final var runtimeResult = Proc1.asIo(runtimeConsumer);
        final var checkedResult = Proc1.asIo(checkedConsumer);
        
        assertThrows(IoException.class, () -> ioResult.accept("test"));
        assertThrows(IoException.class, () -> runtimeResult.accept("test"));
        assertThrows(IoException.class, () -> checkedResult.accept("test"));
    }

    @Test
    public void testAsTryWithComplexOperation() {

        final var stringBuilder = new StringBuilder();
        final var consumer = (Proc1<String>) (s) -> {
            stringBuilder.append("Processed: ").append(s);
        };
        
        final var wrappedConsumer = Proc1.asTry(consumer);
        
        assertDoesNotThrow(() -> wrappedConsumer.accept("input"));
        assertEquals("Processed: input", stringBuilder.toString());
    }

    @Test
    public void testAsIoWithComplexOperation() {

        final var stringBuilder = new StringBuilder();
        final var consumer = (Proc1<String>) (s) -> {
            stringBuilder.append("Processed: ").append(s);
        };
        
        final var wrappedConsumer = Proc1.asIo(consumer);
        
        assertDoesNotThrow(() -> wrappedConsumer.accept("input"));
        assertEquals("Processed: input", stringBuilder.toString());
    }
}