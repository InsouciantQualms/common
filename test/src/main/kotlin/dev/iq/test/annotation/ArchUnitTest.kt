/**
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.annotation

import org.junit.jupiter.api.Tag

/**
 * Convenience annotation to consistently declare an architecture test running in ArchUnit.
 * This will be run in the arcuUnitTest task rather than the normal test task.
 * Archiecture tests must be invoked separately via gradlew archUnitTest
 */
@Retention(AnnotationRetention.RUNTIME)
@Tag(TestConstants.ARCH_UNIT_TEST)
annotation class ArchUnitTest
