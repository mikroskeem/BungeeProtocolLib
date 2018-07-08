/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.reflection

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_SUPER
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.Opcodes.V1_8

/**
 * Magic accessor bridge class
 */
internal val magicAccessorBridge: Class<*> = run {
    val magicAccessorName = "sun/reflect/MagicAccessorImpl"
    val className = "sun.reflect.MagicAccessorBridge_BungeePacketShim"
    val classNameInternal = className.replace('.', '/')

    val cw = ClassWriter(0)
    cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, classNameInternal, null, magicAccessorName, null)
    cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).apply {
        visitVarInsn(ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, magicAccessorName, "<init>", "()V", false)
        visitInsn(RETURN)
        visitMaxs(1, 0)
        visitEnd()
    }
    cw.visitEnd()

    return@run ClassLoader.getSystemClassLoader().defineClass(className, cw.toByteArray())
}