package dev.iq.test.arch

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaMethod

/**
 * Helper to help process annotations associated with ArchUnit rules.
 */
class AnnotationHelper {

    /**
     * Determines if a method has an annotation of the specified type and will look for a matching
     * annotation attribute name that has any of the supplied values.
     *
     * ```
     * Example:  @SuppressWarnings("unused") -> attribute: "value", values: ["unused"]
     * ```
     */
    fun methodHasAnnotationWithAnyString(
        annotationType: Class<out Annotation>,
        attribute: String,
        vararg values: String,
    ): DescribedPredicate<JavaMethod> = object : DescribedPredicate<JavaMethod>(
        "@${annotationType.simpleName}($attribute contains any of ${values.joinToString()}) on method",
    ) {
        override fun test(m: JavaMethod): Boolean {
            val anns = m.annotations.filter { it.rawType.isEquivalentTo(annotationType) }
            if (anns.isEmpty()) return false
            if (values.isEmpty()) return true
            val wanted = values.toSet()
            return anns.any { ann ->
                ann.get(attribute)?.let { value ->
                    anyStringMatches(value, wanted)
                } ?: false
            }
        }
    }

    /**
     * Convenience method to process the @SuppressWarnings annotation specifically.
     */
    fun methodIsSuppressing(vararg values: String): DescribedPredicate<JavaMethod> = methodHasAnnotationWithAnyString(SuppressWarnings::class.java, "value", *values)

    /**
     * Helper method to determine if any of the supplied values match the target value.
     */
    private fun anyStringMatches(target: Any?, matches: Set<String>): Boolean = when (target) {
        is String -> target in matches
        is Array<*> -> target.any { it is String && it in matches }
        is Iterable<*> -> target.any { it is String && it in matches }
        else -> false
    }
}
