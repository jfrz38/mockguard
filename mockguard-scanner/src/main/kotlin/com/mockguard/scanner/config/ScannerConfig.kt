package com.mockguard.scanner.config

data class ScannerConfig(
    val classDirs: List<String>,
    val mode: OutputMode = OutputMode.FAIL,
    val format: OutputFormat = OutputFormat.CONSOLE,
    val output: String? = null,
    val verbose: Boolean = false,
    val baseline: String? = null,
    val writeBaseline: String? = null,
    val failOn: FailOn = FailOn.VIOLATIONS,
    val includes: List<String> = emptyList(),
    val excludes: List<String> = emptyList(),
) {
    enum class OutputMode {
        FAIL, WARN, OFF;

        companion object {
            fun fromString(s: String): OutputMode =
                when (s.uppercase()) {
                    "FAIL" -> FAIL
                    "WARN" -> WARN
                    "OFF" -> OFF
                    else -> throw IllegalArgumentException("Invalid mode: $s. Use FAIL, WARN, or OFF.")
                }
        }
    }

    enum class OutputFormat {
        CONSOLE, JSON, SONARQUBE;

        companion object {
            fun fromString(s: String): OutputFormat =
                when (s.uppercase()) {
                    "CONSOLE" -> CONSOLE
                    "JSON" -> JSON
                    "SONARQUBE" -> SONARQUBE
                    else -> throw IllegalArgumentException("Invalid format: $s. Use CONSOLE, JSON, or SONARQUBE.")
                }
        }
    }

    enum class FailOn {
        VIOLATIONS, NEW;

        companion object {
            fun fromString(s: String): FailOn =
                when (s.uppercase()) {
                    "VIOLATIONS" -> VIOLATIONS
                    "NEW" -> NEW
                    else -> throw IllegalArgumentException("Invalid fail-on: $s. Use VIOLATIONS or NEW.")
                }
        }
    }
}
