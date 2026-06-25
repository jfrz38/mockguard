package com.mockguard.scanner.scanner

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
    private val classDir: Path,
    private val includes: List<String> = emptyList(),
    private val excludes: List<String> = emptyList(),
) {
    fun scan(): ScanResult {
        val classFiles = mutableListOf<Path>()

        Files.walkFileTree(classDir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (file.fileName.toString().endsWith(".class") && shouldScan(file)) {
                    classFiles.add(file)
                }
                return FileVisitResult.CONTINUE
            }
        })

        val allViolations = mutableListOf<Violation>()
        val skippedClasses = mutableListOf<SkippedClass>()

        for (classFile in classFiles) {
            try {
                val bytes = Files.readAllBytes(classFile)
                val reader = ClassReader(bytes)
                val visitor = MockGuardClassVisitor()
                reader.accept(visitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                allViolations.addAll(visitor.getViolations())
            } catch (error: Exception) {
                skippedClasses += SkippedClass(
                    path = classDir.relativize(classFile).toString(),
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

    private fun shouldScan(file: Path): Boolean {
        val relativePath = classDir.relativize(file).toString().replace('\\', '/')
        val className = relativePath.removeSuffix(".class").replace('/', '.')

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
}
