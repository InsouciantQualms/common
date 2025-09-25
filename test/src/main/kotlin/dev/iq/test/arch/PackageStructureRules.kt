/**
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.arch

import com.tngtech.archunit.lang.ArchRule

/**
 * Package structure rules that are not based on package-layer organization.
 * Package-based layer rules have been removed as the project organizes by component/function.
 */
object PackageStructureRules : ArchUnitRuleSet {

    /** @see ArchUnitRuleSet.all */
    override fun all(): List<ArchRule> = emptyList()
}
