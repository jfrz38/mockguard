package com.mockguard.scanner

import com.mockguard.scanner.scanner.MockGuardClassVisitor
import net.bytebuddy.jar.asm.ClassReader
import net.bytebuddy.jar.asm.ClassWriter
import net.bytebuddy.jar.asm.Opcodes
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class MockGuardClassVisitorTest {

    @Test
    fun `detects unverified mock with invocation`() {
        val classBytes = generateTestClass(
            hasVerify = false,
            hasInvocation = true,
        )
        val violations = scan(classBytes)
        assertEquals(1, violations.size)
        assertEquals("myMock", violations[0].fieldName)
        assertTrue(violations[0].hadInvocations)
    }

    @Test
    fun `passes verified mock`() {
        val classBytes = generateTestClass(
            hasVerify = true,
            hasInvocation = true,
        )
        val violations = scan(classBytes)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `detects unverified mock without invocation`() {
        val classBytes = generateTestClass(
            hasVerify = false,
            hasInvocation = false,
        )
        val violations = scan(classBytes)
        assertEquals(1, violations.size)
        assertEquals("myMock", violations[0].fieldName)
        assertEquals(false, violations[0].hadInvocations)
    }

    @Test
    fun `detects verifyNoInteractions as valid verification`() {
        val classBytes = generateTestClassWithVerifyNoInteractions()
        val violations = scan(classBytes)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `passes ignored mock`() {
        val classBytes = generateTestClassWithIgnore()
        val violations = scan(classBytes)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `detects spy field without verification`() {
        val classBytes = generateTestClassWithSpy()
        val violations = scan(classBytes)
        assertEquals(1, violations.size)
        assertEquals("mySpy", violations[0].fieldName)
    }

    @Test
    fun `violation message is descriptive`() {
        val classBytes = generateTestClass(
            hasVerify = false,
            hasInvocation = true,
        )
        val violations = scan(classBytes)
        assertEquals("com.example.MyTest", violations[0].className)
        assertContains(violations[0].fieldType, "List")
    }

    private fun scan(classBytes: ByteArray): List<com.mockguard.scanner.model.Violation> {
        try {
            val reader = ClassReader(classBytes)
            val visitor = MockGuardClassVisitor()
            reader.accept(visitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
            return visitor.getViolations()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun generateTestClass(
        hasVerify: Boolean,
        hasInvocation: Boolean,
    ): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cw.visit(
            Opcodes.V17,
            Opcodes.ACC_PUBLIC,
            "com/example/MyTest",
            null,
            "java/lang/Object",
            null,
        )

        cw.visitSource("MyTest.java", null)

        val mockAnnotation = cw.visitAnnotation("Lorg/mockito/Mock;", true)
        mockAnnotation.visitEnd()

        val fv = cw.visitField(
            Opcodes.ACC_PRIVATE,
            "myMock",
            "Ljava/util/List;",
            null,
            null,
        )
        fv.visitAnnotation("Lorg/mockito/Mock;", true).visitEnd()
        fv.visitEnd()

        val mv = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            "testMethod",
            "()V",
            null,
            null,
        )
        mv.visitCode()

        if (hasInvocation) {
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(Opcodes.GETFIELD, "com/example/MyTest", "myMock", "Ljava/util/List;")
            mv.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                "java/util/List",
                "size",
                "()I",
                true,
            )
            mv.visitInsn(Opcodes.POP)
        }

        if (hasVerify) {
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(Opcodes.GETFIELD, "com/example/MyTest", "myMock", "Ljava/util/List;")
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "org/mockito/Mockito",
                "verify",
                "(Ljava/lang/Object;)Ljava/lang/Object;",
                false,
            )
            mv.visitInsn(Opcodes.POP)
        }

        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()

        cw.visitEnd()
        return cw.toByteArray()
    }

    private fun generateTestClassWithVerifyNoInteractions(): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cw.visit(
            Opcodes.V17,
            Opcodes.ACC_PUBLIC,
            "com/example/VerifyNoInteractionsTest",
            null,
            "java/lang/Object",
            null,
        )
        cw.visitSource("VerifyNoInteractionsTest.java", null)

        val fv = cw.visitField(
            Opcodes.ACC_PRIVATE,
            "myMock",
            "Ljava/util/List;",
            null,
            null,
        )
        fv.visitAnnotation("Lorg/mockito/Mock;", true).visitEnd()
        fv.visitEnd()

        val mv = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            "testMethod",
            "()V",
            null,
            null,
        )
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(Opcodes.GETFIELD, "com/example/VerifyNoInteractionsTest", "myMock", "Ljava/util/List;")
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "org/mockito/Mockito",
            "verifyNoInteractions",
            "(Ljava/lang/Object;)V",
            false,
        )
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()

        cw.visitEnd()
        return cw.toByteArray()
    }

    private fun generateTestClassWithIgnore(): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cw.visit(
            Opcodes.V17,
            Opcodes.ACC_PUBLIC,
            "com/example/IgnoreTest",
            null,
            "java/lang/Object",
            null,
        )
        cw.visitSource("IgnoreTest.java", null)

        val fv = cw.visitField(
            Opcodes.ACC_PRIVATE,
            "myMock",
            "Ljava/util/List;",
            null,
            null,
        )
        fv.visitAnnotation("Lorg/mockito/Mock;", true).visitEnd()
        fv.visitAnnotation("Lcom/mockguard/MockGuardIgnore;", true).visitEnd()
        fv.visitEnd()

        val mv = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            "testMethod",
            "()V",
            null,
            null,
        )
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(Opcodes.GETFIELD, "com/example/IgnoreTest", "myMock", "Ljava/util/List;")
        mv.visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            "java/util/List",
            "size",
            "()I",
            true,
        )
        mv.visitInsn(Opcodes.POP)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()

        cw.visitEnd()
        return cw.toByteArray()
    }

    private fun generateTestClassWithSpy(): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cw.visit(
            Opcodes.V17,
            Opcodes.ACC_PUBLIC,
            "com/example/SpyTest",
            null,
            "java/lang/Object",
            null,
        )
        cw.visitSource("SpyTest.java", null)

        val fv = cw.visitField(
            Opcodes.ACC_PRIVATE,
            "mySpy",
            "Ljava/util/List;",
            null,
            null,
        )
        fv.visitAnnotation("Lorg/mockito/Spy;", true).visitEnd()
        fv.visitEnd()

        val mv = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            "testMethod",
            "()V",
            null,
            null,
        )
        mv.visitCode()
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(0, 0)
        mv.visitEnd()

        cw.visitEnd()
        return cw.toByteArray()
    }
}
