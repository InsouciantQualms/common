/*
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.serde;

import dev.iq.common.fp.Io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Serialization methods for a java.util.Properties object.
 */
public final class PropertiesSerde {

    /**
     * Type contains only static members.
     */
    private PropertiesSerde() {}

    /**
     * Parses the local filesystem URI into a map of string key/value pairs.  The XML file should
     * be in standard java.util.Properties format.
     */
    public static Map<String, String> deserialize(final Path path) {

        return Io.withReturn(() -> {
            try (final InputStream in = new FileInputStream(path.toFile())) {
                return deserialize(in);
            }
        });
    }

    /**
     * Parses the specified stream into a map of string key/value pairs.  The XML file should
     * be in standard java.util.Properties format.
     */
    public static Map<String, String> deserialize(final InputStream in) {

        final var props = new Properties();
        Io.withVoid(() -> props.load(in));
        return props.entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
    }

    /**
     * Outputs the specified map as XML properties to the filesystem URI specified.
     */
    public static void serialize(final Map<String, String> map, final Path target) {

        Io.withVoid(() -> {
            try (final OutputStream out = new FileOutputStream(target.toFile())) {
                serialize(map, out);
            }
        });
    }

    /**
     * Outputs the specified map as a XML properties.
     */
    public static void serialize(final Map<String, String> map, final OutputStream out) {

        final var props = new Properties();
        props.putAll(map);
        Io.withVoid(() -> props.storeToXML(out, null));
    }
}
