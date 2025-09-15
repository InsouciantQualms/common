/**
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.arch

import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

object BaseArchitectureRules : ArchUnitRule {

    fun interfacesShouldNotHaveImplementationSuffix(): ArchRule = noClasses().that()
        .areInterfaces()
        .should()
        .haveSimpleNameEndingWith("Impl")
        .because("Interfaces should not have 'Impl' suffix")

    fun interfacesShouldNotBePrefixedWithI(): ArchRule = noClasses().that()
        .areInterfaces()
        .should()
        .haveSimpleNameStartingWith("I")
        .because("Interfaces should not be prefixed with 'I'")

    /** @see ArchUnitRule.all */
    override fun all(): List<ArchRule> = listOf(
        interfacesShouldNotHaveImplementationSuffix(),
        interfacesShouldNotBePrefixedWithI(),
    )
}
