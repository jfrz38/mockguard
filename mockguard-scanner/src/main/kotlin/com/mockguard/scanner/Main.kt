package com.mockguard.scanner

import com.mockguard.scanner.baseline.Baseline
import com.mockguard.scanner.config.ScannerConfig
import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.output.ConsoleReporter
import com.mockguard.scanner.output.JsonReporter
import com.mockguard.scanner.output.SonarQubeReporter
import com.mockguard.scanner.scanner.ClassFileScanner
import java.nio.file.Paths

fun main(args: Array<String>) {
    val config = try {
        parseArgs(args) ?: return
    } catch (error: IllegalArgumentException) {
        System.err.println("[mockguard-scanner] Error: ${error.message}")
        printHelp()
        kotlin.system.exitProcess(1)
    }

    val classDirs = config.classDirs.map { Paths.get(it) }
    for (classDir in classDirs) {
        if (!classDir.toFile().exists()) {
            System.err.println("[mockguard-scanner] Error: class directory not found: $classDir")
            kotlin.system.exitProcess(1)
        }
    }

    val rawResult = classDirs
        .map { classDir ->
            ClassFileScanner(
                classDir = classDir,
                includes = config.includes,
                excludes = config.excludes,
            ).scan()
        }
        .combine()

    config.writeBaseline?.let { baselinePath ->
        Baseline.write(Paths.get(baselinePath), rawResult.violations)
    }

    val result = config.baseline
        ?.let { baselinePath -> Baseline.apply(rawResult, Paths.get(baselinePath)) }
        ?: rawResult

    val report = when (config.format) {
        ScannerConfig.OutputFormat.CONSOLE -> ConsoleReporter.report(result, verbose = config.verbose)
        ScannerConfig.OutputFormat.JSON -> JsonReporter.report(result)
        ScannerConfig.OutputFormat.SONARQUBE -> SonarQubeReporter.report(result)
    }

    if (config.output != null) {
        java.nio.file.Files.writeString(Paths.get(config.output), report)
    } else {
        println(report)
    }

    val hasViolations = when (config.failOn) {
        ScannerConfig.FailOn.VIOLATIONS -> rawResult.violations.isNotEmpty()
        ScannerConfig.FailOn.NEW -> result.violations.isNotEmpty()
    }
    when (config.mode) {
        ScannerConfig.OutputMode.FAIL -> if (hasViolations) kotlin.system.exitProcess(1)
        ScannerConfig.OutputMode.WARN -> if (hasViolations) System.err.println("[mockguard-scanner] WARNING: unverified mocks found.")
        ScannerConfig.OutputMode.OFF -> Unit
    }
}

private fun parseArgs(args: Array<String>): ScannerConfig? {
    val classDirs = mutableListOf<String>()
    var mode = ScannerConfig.OutputMode.FAIL
    var format = ScannerConfig.OutputFormat.CONSOLE
    var output: String? = null
    var verbose = false
    var baseline: String? = null
    var writeBaseline: String? = null
    var failOn = ScannerConfig.FailOn.VIOLATIONS
    val includes = mutableListOf<String>()
    val excludes = mutableListOf<String>()

    var index = 0
    while (index < args.size) {
        val arg = args[index]
        when {
            arg.startsWith("--class-dir=") -> classDirs += arg.removePrefix("--class-dir=")
            arg == "--class-dir" -> classDirs += args.valueAfter(index, arg).also { index++ }
            arg.startsWith("--mode=") -> mode = ScannerConfig.OutputMode.fromString(arg.removePrefix("--mode="))
            arg == "--mode" -> mode = ScannerConfig.OutputMode.fromString(args.valueAfter(index, arg).also { index++ })
            arg.startsWith("--format=") -> format = ScannerConfig.OutputFormat.fromString(arg.removePrefix("--format="))
            arg == "--format" -> format = ScannerConfig.OutputFormat.fromString(args.valueAfter(index, arg).also { index++ })
            arg.startsWith("--output=") -> output = arg.removePrefix("--output=")
            arg == "--output" -> output = args.valueAfter(index, arg).also { index++ }
            arg.startsWith("--baseline=") -> baseline = arg.removePrefix("--baseline=")
            arg == "--baseline" -> baseline = args.valueAfter(index, arg).also { index++ }
            arg.startsWith("--write-baseline=") -> writeBaseline = arg.removePrefix("--write-baseline=")
            arg == "--write-baseline" -> writeBaseline = args.valueAfter(index, arg).also { index++ }
            arg.startsWith("--fail-on=") -> failOn = ScannerConfig.FailOn.fromString(arg.removePrefix("--fail-on="))
            arg == "--fail-on" -> failOn = ScannerConfig.FailOn.fromString(args.valueAfter(index, arg).also { index++ })
            arg.startsWith("--include=") -> includes += arg.removePrefix("--include=")
            arg == "--include" -> includes += args.valueAfter(index, arg).also { index++ }
            arg.startsWith("--exclude=") -> excludes += arg.removePrefix("--exclude=")
            arg == "--exclude" -> excludes += args.valueAfter(index, arg).also { index++ }
            arg == "--verbose" -> verbose = true
            arg == "--help" || arg == "-h" -> {
                printHelp()
                return null
            }
            else -> throw IllegalArgumentException("Unknown option: $arg")
        }
        index++
    }

    if (classDirs.isEmpty()) {
        System.err.println("[mockguard-scanner] Error: at least one --class-dir is required")
        printHelp()
        kotlin.system.exitProcess(1)
    }

    return ScannerConfig(
        classDirs = classDirs,
        mode = mode,
        format = format,
        output = output,
        verbose = verbose,
        baseline = baseline,
        writeBaseline = writeBaseline,
        failOn = failOn,
        includes = includes,
        excludes = excludes,
    )
}

private fun Array<String>.valueAfter(index: Int, option: String): String {
    val value = getOrNull(index + 1)
    require(!value.isNullOrBlank() && !value.startsWith("--")) { "Missing value for $option" }
    return value
}

private fun List<ScanResult>.combine(): ScanResult = ScanResult(
    totalClasses = sumOf { it.totalClasses },
    violations = flatMap { it.violations },
    skippedClasses = flatMap { it.skippedClasses },
)

private fun printHelp() {
    println(
        """
        |mockguard-scanner - Static bytecode scanner for unverified Mockito mocks
        |
        |Usage:
        |  mockguard-scanner --class-dir=<path> [--class-dir=<path> ...] [options]
        |  java -jar mockguard-scanner.jar --class-dir=<path> [--class-dir=<path> ...] [options]
        |
        |Options:
        |  --class-dir=<path>    Directory containing .class files to scan. Can be repeated (required)
        |  --mode=FAIL|WARN|OFF  Output mode (default: FAIL)
        |  --format=console|json|sonarqube Output format (default: console)
        |  --output=<file>       Write output to file instead of stdout
        |  --baseline=<file>     Ignore violations already present in a baseline file
        |  --write-baseline=<file> Write current violations to a baseline file
        |  --fail-on=VIOLATIONS|NEW Failure criterion when mode=FAIL (default: VIOLATIONS)
        |  --include=<pattern>   Include only matching class paths or names. Can be repeated
        |  --exclude=<pattern>   Exclude matching class paths or names. Can be repeated
        |  --verbose             Include skipped class details in console output
        |  --help, -h            Show this help
        |
        |Options with values also accept a space instead of '=', for example --class-dir build/classes/kotlin/test.
        """.trimMargin(),
    )
}
