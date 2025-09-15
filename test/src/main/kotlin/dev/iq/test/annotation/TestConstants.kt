/**
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.annotation

/**
 * Constants for testing.
 */
object TestConstants {

    /** Constant combined with @Tag annotation to run in integrationTest phase rather than (unit) test phase. */
    const val INTEGRATION_TEST: String = "IntegrationTest"

    /** Constant combined with @Tag annotation to run in containerTest phase rather than unit or integration phases. */
    const val CONTAINER_TEST: String = "ContainerTest"

    /** Constant combined with @Tag annotation to never run (normally because user input is required). */
    const val HUMAN_INTERACTIVE_TEST: String = "HumanInteractiveTest"

    /** Constant combined with @Tag annotation to run ArchUnit tests. */
    const val ARCH_UNIT_TEST: String = "ArchUnitTest"
}
