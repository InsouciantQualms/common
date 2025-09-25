/**
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package dev.iq.test.arch

import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition

object DependencyRules : ArchUnitRuleSet {

    fun noClassesShouldDependOnDeprecatedClasses(): ArchRule = noClasses().should()
        .dependOnClassesThat()
        .areAnnotatedWith(Deprecated::class.java)
        .because("Classes should not depend on deprecated classes")

    fun noCyclicDependencies(): ArchRule = SlicesRuleDefinition.slices()
        .matching("dev.iq.(**)..")
        .should()
        .beFreeOfCycles()

    fun noClassesShouldThrowGenericExceptions(): ArchRule = noClasses().that()
        .areNotAnnotatedWith("dev.iq.archunit.core.AllowGenericExceptions")
        .should()
        .accessClassesThat()
        .areAssignableTo(Exception::class.java)
        .because("Throwing generic exceptions should be avoided (use @AllowGenericExceptions to suppress)")

    fun utilityClassesShouldBeStateless(): ArchRule = classes().that()
        .haveSimpleNameEndingWith("Utils")
        .or().haveSimpleNameEndingWith("Helper")
        .or().haveSimpleNameEndingWith("Util")
        .and().doNotHaveModifier(JavaModifier.ABSTRACT)
        .and().areNotEnums()
        .and().areNotInterfaces()
        .and().areNotAnnotations()
        .should()
        .haveOnlyPrivateConstructors()
        .because("Utility classes should have private constructors")

    /** @see ArchUnitRuleSet.all */
    override fun all(): List<ArchRule> = listOf(
        noClassesShouldDependOnDeprecatedClasses(),
        noCyclicDependencies(),
        noClassesShouldThrowGenericExceptions(),
        utilityClassesShouldBeStateless(),
    )
}
