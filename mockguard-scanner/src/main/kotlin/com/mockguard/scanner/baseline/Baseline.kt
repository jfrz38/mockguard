package com.mockguard.scanner.baseline

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonArray
import com.mockguard.scanner.json.JsonCodec
import com.mockguard.scanner.model.BaselineSummary
import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.Violation
import java.nio.file.Files
import java.nio.file.Path

internal object Baseline {
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

        val root = try {
            JsonParser.parseString(Files.readString(path)).asJsonObject
        } catch (exception: Exception) {
            throw IllegalArgumentException("Malformed baseline file: $path", exception)
        }
        val version = root.requiredInt("version", path)
        require(version == 1 || version == 2) {
            "Unsupported baseline version $version in $path. Supported versions: 1, 2."
        }
        val violations = root.get("violations")
        require(violations != null && violations.isJsonArray) {
            "Invalid baseline file $path: 'violations' must be an array."
        }

        return violations.asJsonArray.mapIndexed { index, element ->
            require(element.isJsonObject) {
                "Invalid baseline file $path: violation $index must be an object."
            }
            val entry = element.asJsonObject
            val methodName = entry.optionalString("methodName", path, index)
            val methodDescriptor = entry.optionalString("methodDescriptor", path, index)
            require((methodName == null) == (methodDescriptor == null)) {
                "Invalid baseline file $path: violation $index must define both methodName and methodDescriptor."
            }
            ViolationKey(
                className = entry.requiredString("className", path, index),
                methodName = methodName,
                methodDescriptor = methodDescriptor,
                fieldName = entry.requiredString("fieldName", path, index),
                fieldType = entry.requiredString("fieldType", path, index),
            )
        }
    }

    private fun serialize(keys: List<ViolationKey>): String {
        val root = JsonObject().apply {
            addProperty("version", if (keys.any { it.methodName != null }) 2 else 1)
            add(
                "violations",
                JsonArray().apply {
                    keys.forEach { key ->
                        add(
                            JsonObject().apply {
                                addProperty("className", key.className)
                                if (key.methodName != null && key.methodDescriptor != null) {
                                    addProperty("methodName", key.methodName)
                                    addProperty("methodDescriptor", key.methodDescriptor)
                                }
                                addProperty("fieldName", key.fieldName)
                                addProperty("fieldType", key.fieldType)
                            },
                        )
                    }
                },
            )
        }
        return JsonCodec.gson.toJson(root)
    }

    private fun Violation.key(): ViolationKey = ViolationKey(
        className = className,
        methodName = methodName,
        methodDescriptor = methodDescriptor,
        fieldName = fieldName,
        fieldType = fieldType,
    )

}

private fun JsonObject.requiredInt(name: String, path: Path): Int {
    val value = get(name)
    require(value != null && value.isJsonPrimitive && value.asJsonPrimitive.isNumber) {
        "Invalid baseline file $path: '$name' must be a number."
    }
    return try {
        value.asBigDecimal.intValueExact()
    } catch (exception: ArithmeticException) {
        throw IllegalArgumentException("Invalid baseline file $path: '$name' must be an integer.", exception)
    }
}

private fun JsonObject.requiredString(name: String, path: Path, index: Int): String =
    optionalString(name, path, index)
        ?: throw IllegalArgumentException("Invalid baseline file $path: violation $index requires '$name'.")

private fun JsonObject.optionalString(name: String, path: Path, index: Int): String? {
    val value = get(name) ?: return null
    require(!value.isJsonNull && value.isJsonPrimitive && value.asJsonPrimitive.isString) {
        "Invalid baseline file $path: violation $index field '$name' must be a string."
    }
    return value.asString
}

internal data class ViolationKey(
    val className: String,
    val methodName: String? = null,
    val methodDescriptor: String? = null,
    val fieldName: String,
    val fieldType: String,
) : Comparable<ViolationKey> {
    override fun compareTo(other: ViolationKey): Int =
        compareValuesBy(
            this,
            other,
            ViolationKey::className,
            { it.methodName.orEmpty() },
            { it.methodDescriptor.orEmpty() },
            ViolationKey::fieldName,
            ViolationKey::fieldType,
        )
}
