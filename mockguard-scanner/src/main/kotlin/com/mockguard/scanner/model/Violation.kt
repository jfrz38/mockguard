package com.mockguard.scanner.model

data class Violation(
    val className: String,
    val sourceFile: String?,
    val lineNumber: Int,
    val fieldName: String,
    val fieldType: String,
    val hadInvocations: Boolean,
)
