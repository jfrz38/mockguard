package com.mockguard.scanner.scanner

import com.mockguard.scanner.model.MockField
import com.mockguard.scanner.model.Violation
import net.bytebuddy.jar.asm.AnnotationVisitor
import net.bytebuddy.jar.asm.ClassVisitor
import net.bytebuddy.jar.asm.FieldVisitor
import net.bytebuddy.jar.asm.MethodVisitor
import net.bytebuddy.jar.asm.Opcodes
import net.bytebuddy.jar.asm.Type

private const val MOCK_DESC = "Lorg/mockito/Mock;"
private const val SPY_DESC = "Lorg/mockito/Spy;"
private const val MOCKGUARD_IGNORE_DESC = "Lcom/mockguard/MockGuardIgnore;"
private const val GUARDED_MOCK_DESC = "Lcom/mockguard/GuardedMock;"

private val EMPTY_ANNOTATION_VISITOR = object : AnnotationVisitor(Opcodes.ASM9) {}

class MockGuardClassVisitor : ClassVisitor(Opcodes.ASM9) {

    private val verificationCallMatcher = CompositeVerificationCallMatcher()

    private var className = ""
    private var sourceFile: String? = null
    private val mockFields = mutableListOf<MockField>()
    private val verifiedFieldNames = mutableSetOf<String>()
    private val invokedFieldNames = mutableSetOf<String>()
    private val violations = mutableListOf<Violation>()
    private var hasGuardedMock = false

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<String>?,
    ) {
        className = name.replace('/', '.')
    }

    override fun visitSource(source: String?, debug: String?) {
        sourceFile = source
    }

    override fun visitField(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        value: Any?,
    ): FieldVisitor {
        return object : FieldVisitor(Opcodes.ASM9) {
            private var isMock = false
            private var isSpy = false
            private var isIgnored = false
            private var isGuarded = false

            override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
                when (descriptor) {
                    MOCK_DESC -> isMock = true
                    SPY_DESC -> isSpy = true
                    MOCKGUARD_IGNORE_DESC -> isIgnored = true
                    GUARDED_MOCK_DESC -> isGuarded = true
                }
                return EMPTY_ANNOTATION_VISITOR
            }

            override fun visitEnd() {
                if (isMock || isSpy) {
                    if (isGuarded) hasGuardedMock = true
                    mockFields.add(
                        MockField(
                            name = name,
                            descriptor = descriptor,
                            isSpy = isSpy,
                            isIgnored = isIgnored,
                            isGuarded = isGuarded,
                        ),
                    )
                }
            }
        }
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<String>?,
    ): MethodVisitor {
        return MethodInsnTracker(
            opcodesVersion = Opcodes.ASM9,
            delegate = null,
            internalClassName = className,
            knownMockFields = mockFields,
            verifiedNames = verifiedFieldNames,
            invokedNames = invokedFieldNames,
            verificationCallMatcher = verificationCallMatcher,
        )
    }

    override fun visitEnd() {
        val effectiveMocks = if (hasGuardedMock) {
            mockFields.filter { it.isGuarded }
        } else {
            mockFields
        }

        for (field in effectiveMocks) {
            if (field.isIgnored) continue
            if (field.name in verifiedFieldNames) continue

            val hadInvocations = field.name in invokedFieldNames

            violations.add(
                Violation(
                    className = className,
                    sourceFile = sourceFile,
                    lineNumber = 0,
                    fieldName = field.name,
                    fieldType = typeDescriptorToName(field.descriptor),
                    hadInvocations = hadInvocations,
                ),
            )
        }
    }

    fun getViolations(): List<Violation> = violations.toList()

    companion object {
        fun typeDescriptorToName(descriptor: String): String {
            return try {
                Type.getType(descriptor).className
            } catch (_: Exception) {
                descriptor
            }
        }
    }
}

private class MethodInsnTracker(
    opcodesVersion: Int,
    delegate: MethodVisitor?,
    private val internalClassName: String,
    knownMockFields: List<MockField>,
    private val verifiedNames: MutableSet<String>,
    private val invokedNames: MutableSet<String>,
    private val verificationCallMatcher: VerificationCallMatcher,
) : MethodVisitor(opcodesVersion, delegate) {

    private val mockFieldNames = knownMockFields.map { it.name }.toSet()
    private val recentLoads = ArrayDeque<String>(4)

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        if (opcode == Opcodes.GETFIELD && owner.replace('/', '.') == internalClassName && name in mockFieldNames) {
            recentLoads.addFirst(name)
            if (recentLoads.size > 4) recentLoads.removeLast()
        }
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean,
    ) {
        if (opcode == Opcodes.INVOKESTATIC) {
            when (verificationCallMatcher.match(owner, name, descriptor)) {
                VerificationCall.Verify,
                VerificationCall.VerifyNoInteractions,
                VerificationCall.VerifyNoMoreInteractions,
                -> {
                    if (recentLoads.isNotEmpty()) {
                        verifiedNames.addAll(recentLoads)
                        recentLoads.clear()
                        return
                    }
                }

                VerificationCall.InOrder -> {
                    recentLoads.clear()
                    return
                }

                null -> Unit
            }
        }

        if (opcode == Opcodes.INVOKEVIRTUAL && owner.replace('/', '.') == internalClassName && name.startsWith("get")) {
            val propertyName = name.removePrefix("get").replaceFirstChar { it.lowercaseChar() }
            if (propertyName in mockFieldNames) {
                recentLoads.addFirst(propertyName)
                if (recentLoads.size > 4) recentLoads.removeLast()
                return
            }
        }

        if ((opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE) && recentLoads.isNotEmpty()) {
            invokedNames.addAll(recentLoads)
            recentLoads.clear()
        }
    }
}
