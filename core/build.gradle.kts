/*
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

plugins {

    id("java-common")
    `java-library`
}

dependencies {

    api(libs.jakarta.validation)
    api(libs.vavr)

    implementation(platform(libs.jackson.bom))
    implementation(libs.bundles.jackson)
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j2)
    api(libs.jnanoid)

    testFixturesApi(project(":test"))
}
