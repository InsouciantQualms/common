/**
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.annotation

import org.junit.jupiter.api.Tag

/**
 * Conveinence annotation to consistently declare an integration test running in JUnit
 * that requires human interaction.  These tests typically require some input from
 * stdin or interacting with a user interface.  These tests are not normally run as
 * part of a test suite or Gradle build.
 */
@Retention(AnnotationRetention.RUNTIME)
@Tag(TestConstants.HUMAN_INTERACTIVE_TEST)
annotation class HumanInteractiveTest
