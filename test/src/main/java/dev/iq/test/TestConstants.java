/*
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.test;

/**
 * Constants for testing.
 */
public final class TestConstants {

    /** Constant combined with @Tag annotation to run in integrationTest phase rather than (unit) test phase. */
    public static final String INTEGRATION_TEST = "IntegrationTest";

    /** Constant combined with @Tag annotation to run in containerTest phase rather than unit or integration phases. */
    public static final String CONTAINER_TEST = "ContainerTest";

    /** Constant combined with @Tag annotation to never run (normally because user input is required). */
    public static final String HUMAN_INTERACTION_TEST = "HumanInteractionTest";

    /**
     * Type contains only static members.
     */
    private TestConstants() {}
}
