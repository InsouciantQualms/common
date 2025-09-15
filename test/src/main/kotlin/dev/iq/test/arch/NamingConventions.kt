/**
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.arch

import com.tngtech.archunit.lang.ArchRule

/**
 * Generic naming conventions that are not framework-specific.
 * Spring-related naming conventions have been moved to archunit-rules-spring-boot.
 */
object NamingConventions : ArchUnitRule {

    /** @see ArchUnitRule.all */
    override fun all(): List<ArchRule> = emptyList()
}
