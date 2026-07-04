package com.mockguard.scanner.baseline

import com.mockguard.scanner.model.BaselineSummary
import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.Violation
import java.nio.file.Files
import java.nio.file.Path

object Baseline {
    fun write(path: Path, violations: List<Violation>) {
        path.parent?.let { Files.createDirectories(it) }
        Files.writeString(path, serialize(violations.map { it.key() }.distinct().sorted()))
    }

    fun apply(result: ScanResult, path: Path): ScanResult {
        val baselineKeys = read(path).toSet()
        val currentByKey = result.violations.associateBy { it.key() }
        val currentKeys = currentByKey.keys

        val newKeys = currentKeys - baselineKeys
        val knownKeys = currentKeys intersect baselineKeys
        val resolvedKeys = baselineKeys - currentKeys

        return result.copy(
            violations = newKeys.sorted().mapNotNull { currentByKey[it] },
            baselineSummary = BaselineSummary(
                baselineEntries = baselineKeys.size,
                knownViolations = knownKeys.size,
                newViolations = newKeys.size,
                resolvedViolations = resolvedKeys.size,
            ),
        )
    }

    internal fun read(path: Path): List<ViolationKey> {
        if (!Files.exists(path)) {
            error("Baseline file not found: $path")
        }

        val content = Files.readString(path)
        val entryRegex = Regex("\\{\\s*\"className\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"\\s*,\\s*\"fieldName\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"\\s*,\\s*\"fieldType\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"\\s*}")
        return entryRegex.findAll(content)
            .map { match ->
                ViolationKey(
                    className = unescapeJson(match.groupValues[1]),
                    fieldName = unescapeJson(match.groupValues[2]),
                    fieldType = unescapeJson(match.groupValues[3]),
                )
            }
            .toList()
    }

    private fun serialize(keys: List<ViolationKey>): String = buildString {
        appendLine("{")
        appendLine("  \"version\": 1,")
        appendLine("  \"violations\": [")

        for ((index, key) in keys.withIndex()) {
            appendLine("    {")
            appendLine("      \"className\": ${escapeJson(key.className)},")
            appendLine("      \"fieldName\": ${escapeJson(key.fieldName)},")
            appendLine("      \"fieldType\": ${escapeJson(key.fieldType)}")
            append("    }")
            if (index < keys.lastIndex) appendLine(",") else appendLine()
        }

        appendLine("  ]")
        append("}")
    }

    private fun Violation.key(): ViolationKey = ViolationKey(
        className = className,
        fieldName = fieldName,
        fieldType = fieldType,
    )

    private fun escapeJson(value: String): String {
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }

    private fun unescapeJson(value: String): String {
        val result = StringBuilder()
        var index = 0
        while (index < value.length) {
            val char = value[index]
            if (char == '\\' && index + 1 < value.length) {
                when (val escaped = value[index + 1]) {
                    '\\' -> result.append('\\')
                    '"' -> result.append('"')
                    'n' -> result.append('\n')
                    'r' -> result.append('\r')
                    't' -> result.append('\t')
                    else -> result.append(escaped)
                }
                index += 2
            } else {
                result.append(char)
                index++
            }
        }
        return result.toString()
    }
}

internal data class ViolationKey(
    val className: String,
    val fieldName: String,
    val fieldType: String,
) : Comparable<ViolationKey> {
    override fun compareTo(other: ViolationKey): Int =
        compareValuesBy(this, other, ViolationKey::className, ViolationKey::fieldName, ViolationKey::fieldType)
}
