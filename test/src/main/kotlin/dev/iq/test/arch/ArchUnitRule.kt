package dev.iq.test.arch

import com.tngtech.archunit.lang.ArchRule

/**
 * Common interface for all ArchUnit rules.  Unit tests will run all registered implementations
 * of this interface during the archUnitTest task.
 */
interface ArchUnitRule {

    /** Lists all available ArchUnit rules. */
    fun all(): List<ArchRule>
}
