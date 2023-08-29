/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial implementation
 *
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(libs.edc.control.plane.core)
    implementation(libs.edc.data.plane.selector.core)
    implementation(libs.edc.api.observability)

    implementation(libs.edc.configuration.filesystem)
    implementation(libs.edc.iam.mock)

    implementation(libs.edc.auth.tokenbased)
    implementation(libs.edc.management.api)

    implementation(libs.edc.dsp)

    implementation(project(":transfer:transfer-01-file-transfer:transfer-file-local"))
    runtimeOnly(libs.edc.monitor.jdk.logger)
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("provider.jar")
}

tasks.withType<GradleBuild> {
    val download = { url: String, destFile: File -> ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile)) }

    val agentFile = projectDir.resolve("../opentelemetry-javaagent.jar")

    if (!agentFile.exists()) {
        logger.lifecycle("Downloading OpenTelemetry Agent")
        download(
                "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.29.0/opentelemetry-javaagent.jar",
                agentFile
        )
    }
}
