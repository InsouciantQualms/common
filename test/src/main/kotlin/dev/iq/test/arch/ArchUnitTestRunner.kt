package dev.iq.test.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import dev.iq.test.annotation.ArchUnitTest
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

/**
 * Scans all packages for violations of ArchUnit rules, running as a standard JUnit5 test.
 *
 * By default, it will use the top-level (first two components) of this class's package name
 * as the root to scan.  Override with the system property("archunit.scan.packages").
 *
 * This class requires JUnit5 to be set-up.  If you are running with the build-logic conventions,
 * the test will execute after unit tests in their own Gradle task `archUnitTest`.
 */
@ArchUnitTest
@Suppress("TestInProductSource")
class ArchUnitTestRunner {

    /** Package root to scan. */
    private val packageRoot = System.getProperty(
        "archunit.scan.packages",
        this::class.java.packageName.split(".").take(2).joinToString("."),
    )

    /** All source classes to scan for violations. */
    private val classes: JavaClasses = ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
        .importPackages(packageRoot)

    /** All ArchUnit rule providers to apply to the source classes. */
    private val testProviders: List<ArchUnitRuleSet> = discoverTestProviders()

    /**
     * Factory to apply all ArchUnit rules to the source classes .
     */
    @TestFactory
    @Suppress("TestMethodWithoutAssertion")
    fun executeAllArchRules(): Stream<DynamicTest> = testProviders.stream()
        .flatMap { provider ->
            provider.all().stream()
                .map { rule ->
                    DynamicTest.dynamicTest(
                        "[${provider::class.simpleName}] ${rule.description}",
                    ) {
                        rule.check(classes)
                    }
                }
        }

    /**
     * Scans for implementations of the ArchRuleSet interface to collect all available rules.
     */
    private fun discoverTestProviders(): List<ArchUnitRuleSet> = ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
        .importPackages(packageRoot)
        .filter { javaClass -> javaClass.isAssignableTo(ArchUnitRuleSet::class.java) }
        .map { javaClass ->
            Class.forName(javaClass.name).getDeclaredConstructor().newInstance() as ArchUnitRuleSet
        }
}
