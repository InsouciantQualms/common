/**
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.annotation

import org.junit.jupiter.api.Tag

/**
 * Convenience annotation to consistently declare an integration test running in JUnit.
 * This will be run in the integrationTest task rather than the normal test task.
 * Integration tests must be invoked separately via gradlew integrationTest
 */
@Retention(AnnotationRetention.RUNTIME)
@Tag(TestConstants.CONTAINER_TEST)
annotation class ContainerTest
