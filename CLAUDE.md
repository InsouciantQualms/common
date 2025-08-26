# Critical

Treat ~/Dev/common as the root of the project workspace
Never modify any files outside the project workspace
Never push code to Github
Do not modify @Stable annotated types (you may instead pause to prompt me before continuing if you want to propose a change)
Summarize changes performed at the end

# Goals

Primary:  This is a common framework/library project in Java for general use in other projects
Primary:  Code generally in a FP/streaming style (immutable, avoid imperative loops where possible, etc.)
Primary:  Avoid checked exceptions (use the Try or Io types and their methods withVoid() and withReturn())
Secondary:  Make all types and variables final except where that would be redundant
Secondary:  Ensure that all checkstyle checks pass via the Gradle build
Requirement:  Use JDK 21
Requirement:  Gradle is used for builds, there is no Maven in this project

# Current Instructions

* There are numerous checkstyle issues with the project, please fix them both in source and test trees
* Any formatting issues can be fixed with the spotless/palantir Gradle plugin that is available
* Run the Gradle build to ensure that all tests pass and that none are skipped

# Java Conventions

Make concrete types, fields, parameters and local variables final where possible
Do not create abstract classes (use composition instead)
Utilitiy classes should have all static methods and a private constructor
Use var for local variables
Javadoc comments should contain a single sentence and no @param, @return or @throws attributes
Prefer placing comments in Javadoc rather than inline
Use either Try or Io (in dev.iq.common.fp) in lieu of try/catch blocks (or to convert an exception to unchecked to propagate up)
Use either Try or Io to avoid declaring checked exceptions in a throws clause
Use Io when an operation has side effects and Try otherwise
Please add a blank line between a method or constructor signature/declaration and the body of the method