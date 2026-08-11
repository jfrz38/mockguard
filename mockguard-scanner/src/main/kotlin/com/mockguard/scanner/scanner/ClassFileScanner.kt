package com.mockguard.scanner.scanner

import com.mockguard.scanner.config.TestSelector
import com.mockguard.scanner.model.ScanResult
import com.mockguard.scanner.model.SkippedClass
import com.mockguard.scanner.model.Violation
import net.bytebuddy.jar.asm.ClassReader
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class ClassFileScanner(
    private val classDirs: List<Path>,
    private val includes: List<String> = emptyList(),
    private val excludes: List<String> = emptyList(),
    private val tests: List<TestSelector> = emptyList(),
) {
    constructor(
        classDir: Path,
        includes: List<String> = emptyList(),
        excludes: List<String> = emptyList(),
        tests: List<TestSelector> = emptyList(),
    ) : this(listOf(classDir), includes, excludes, tests)

    fun scan(): ScanResult {
        val discoveredClasses = mutableListOf<ClassFile>()

        for (classDir in classDirs) {
            Files.walkFileTree(classDir, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (file.fileName.toString().endsWith(".class")) {
                        val relativePath = classDir.relativize(file).toString().replace('\\', '/')
                        discoveredClasses += ClassFile(
                            path = file,
                            relativePath = relativePath,
                            className = relativePath.removeSuffix(".class").replace('/', '.'),
                        )
                    }
                    return FileVisitResult.CONTINUE
                }
            })
        }

        val classFiles = selectClasses(discoveredClasses)

        val allViolations = mutableListOf<Violation>()
        val skippedClasses = mutableListOf<SkippedClass>()

        for (classFile in classFiles) {
            try {
                val bytes = Files.readAllBytes(classFile.path)
                val reader = ClassReader(bytes)
                val classTests = tests.filter { it.className == classFile.className }
                val visitor = MockGuardClassVisitor(classTests)
                reader.accept(visitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                allViolations.addAll(visitor.getViolations())
            } catch (error: Exception) {
                if (tests.isNotEmpty()) {
                    throw IllegalArgumentException(
                        "Could not scan selected class ${classFile.className}: ${error.message ?: error::class.java.simpleName}",
                        error,
                    )
                }
                skippedClasses += SkippedClass(
                    path = classFile.relativePath,
                    reason = error.message ?: error::class.java.simpleName,
                )
            }
        }

        return ScanResult(
            totalClasses = classFiles.size,
            violations = allViolations,
            skippedClasses = skippedClasses,
        )
    }

    private fun selectClasses(discoveredClasses: List<ClassFile>): List<ClassFile> {
        if (tests.isEmpty()) {
            return discoveredClasses.filter(::passesClassFilters)
        }

        val selectedClassNames = tests.map { it.className }.toSet()
        for (className in selectedClassNames) {
            val matches = discoveredClasses.filter { it.className == className }
            require(matches.isNotEmpty()) { "Selected test class not found: $className" }
            require(matches.size == 1) {
                "Selected test class found in multiple class directories: $className"
            }
            require(passesClassFilters(matches.single())) {
                "Selected test class was excluded by --include or --exclude: $className"
            }
        }

        return discoveredClasses.filter { it.className in selectedClassNames && passesClassFilters(it) }
    }

    private fun passesClassFilters(classFile: ClassFile): Boolean {
        val relativePath = classFile.relativePath
        val className = classFile.className

        val included = includes.isEmpty() || includes.any { it.matchesClass(relativePath, className) }
        val excluded = excludes.any { it.matchesClass(relativePath, className) }

        return included && !excluded
    }

    private fun String.matchesClass(relativePath: String, className: String): Boolean {
        val regex = wildcardRegex()
        return regex.matches(relativePath) || regex.matches(className)
    }

    private fun String.wildcardRegex(): Regex {
        val pattern = buildString {
            append('^')
            for (char in this@wildcardRegex) {
                when (char) {
                    '*' -> append(".*")
                    '?' -> append('.')
                    else -> append(Regex.escape(char.toString()))
                }
            }
            append('$')
        }
        return Regex(pattern)
    }

    private data class ClassFile(
        val path: Path,
        val relativePath: String,
        val className: String,
    )
}
