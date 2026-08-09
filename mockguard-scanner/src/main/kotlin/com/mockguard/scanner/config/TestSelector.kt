package com.mockguard.scanner.config

data class TestSelector(
    val className: String,
    val methodName: String,
    val methodDescriptor: String? = null,
) {
    companion object {
        fun parse(value: String): TestSelector {
            val separator = value.indexOf('#')
            require(separator > 0 && separator < value.lastIndex) {
                "Invalid test selector: $value. Use <class>#<method> or <class>#<method><descriptor>."
            }

            val className = value.substring(0, separator).replace('/', '.').trim()
            val methodAndDescriptor = value.substring(separator + 1)
            val descriptorStart = methodAndDescriptor.indexOf('(')
            val methodName = if (descriptorStart >= 0) {
                methodAndDescriptor.substring(0, descriptorStart)
            } else {
                methodAndDescriptor
            }
            val descriptor = descriptorStart
                .takeIf { it >= 0 }
                ?.let(methodAndDescriptor::substring)

            require(className.isNotBlank() && !className.any(Char::isWhitespace)) {
                "Invalid class name in test selector: $value"
            }
            require(methodName.isNotBlank()) { "Invalid method name in test selector: $value" }
            if (descriptor != null) {
                require(METHOD_DESCRIPTOR.matches(descriptor)) {
                    "Invalid JVM method descriptor in test selector: $value"
                }
            }

            return TestSelector(
                className = className,
                methodName = methodName,
                methodDescriptor = descriptor,
            )
        }

        private const val JVM_TYPE = "(?:\\[*(?:[BCDFIJSZ]|L[^.;\\[\\]()]+;))"
        private val METHOD_DESCRIPTOR = Regex("^\\((?:$JVM_TYPE)*\\)(?:V|$JVM_TYPE)$")
    }
}
