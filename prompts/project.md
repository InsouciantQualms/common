# Goals

Primary:  This is a common framework/library project in Java for general use in other projects
Primary:  Code generally in a FP/streaming style (immutable, avoid imperative loops where possible, etc.)
Primary:  Avoid checked exceptions (use the Try or Io types and their methods withVoid() and withReturn())
Secondary:  Make all types and variables final except where that would be redundant
Secondary:  Ensure that all checkstyle checks pass via the Gradle build
Requirement:  Use JDK 21
Requirement:  Gradle is used for builds, there is no Maven in this project
