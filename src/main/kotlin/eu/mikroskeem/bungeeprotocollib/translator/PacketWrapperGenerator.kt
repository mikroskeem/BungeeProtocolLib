/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.translator

import com.github.steveice10.packetlib.packet.Packet
import eu.mikroskeem.bungeeprotocollib.BungeeProtocolLib
import eu.mikroskeem.bungeeprotocollib.api.packet.WrappedPacket
import eu.mikroskeem.bungeeprotocollib.injector.PacketHelper
import eu.mikroskeem.bungeeprotocollib.reflection.MagicConstructor
import eu.mikroskeem.bungeeprotocollib.reflection.className
import eu.mikroskeem.bungeeprotocollib.reflection.defineClass
import eu.mikroskeem.bungeeprotocollib.reflection.magicAccessorBridge
import io.netty.buffer.ByteBuf
import net.md_5.bungee.protocol.AbstractPacketHandler
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.ACC_FINAL
import org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_SUPER
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.Opcodes.DUP
import org.objectweb.asm.Opcodes.GETFIELD
import org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.Opcodes.IRETURN
import org.objectweb.asm.Opcodes.NEW
import org.objectweb.asm.Opcodes.PUTFIELD
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.Opcodes.V1_8
import org.objectweb.asm.Type


private val packetType = Type.getType(Packet::class.java)
private val wrappedPacketType = Type.getType(WrappedPacket::class.java)

private val magicImplType = Type.getType(magicAccessorBridge)
private val magicConstructorType = Type.getType(MagicConstructor::class.java)

private val byteBufType = Type.getType(ByteBuf::class.java)
private val aphType = Type.getType(AbstractPacketHandler::class.java)

private val helperType = Type.getType(PacketHelper::class.java)

/**
 * Wraps [Packet] into [WrappedPacket] which can be used freely with BungeeCord
 */
object PacketWrapperGenerator {
    private val wrapperClasses = HashMap<Class<out Packet>, Class<out WrappedPacket>>()
    private val constructorClasses = HashMap<Class<out Packet>, MagicConstructor<out Packet>>()

    /**
     * Gets new packet wrapper class
     *
     * @param packet [Packet] class to wrap
     * @return [WrappedPacket] wrapping given [Packet]
     */
    @JvmStatic
    fun <P: Packet> getPacketWrapperClass(packet: Class<out P>): Class<out WrappedPacket> {
        return wrapperClasses.computeIfAbsent(packet) {
            return@computeIfAbsent generatePacketWrapper(packet)
        }
    }

    /**
     * Gets new fast constructor for given [Packet] class
     *
     * @param packet [Packet] class to construct
     * @return An instance of [MagicConstructor]
     */
    @JvmStatic
    fun <P: Packet> getPacketConstructor(packet: Class<out P>): MagicConstructor<out Packet> {
        return constructorClasses.computeIfAbsent(packet) {
            return@computeIfAbsent generateConstructor(packet)
        }
    }

    /**
     * Generates packet for [Packet] class
     */
    private fun <P: Packet, W: WrappedPacket> generatePacketWrapper(packet: Class<out P>): Class<out W> {
        val className = PacketWrapperGenerator::class.java.name + ".generated.Wrapper${packet.className}"
        val classNameInternal = className.replace('.', '/')
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS)
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, classNameInternal, null, wrappedPacketType.internalName, null)
        cw.visitField(ACC_PRIVATE + ACC_FINAL, "helper", helperType.descriptor, null, null).visitEnd()

        // Define methods
        // Constructor
        cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).also { init ->
            init.visitCode()
            init.visitVarInsn(ALOAD, 0)
            init.visitMethodInsn(INVOKESPECIAL, wrappedPacketType.internalName, "<init>", "()V", false)
            init.visitVarInsn(ALOAD, 0)
            init.visitVarInsn(ALOAD, 0)
            init.visitLdcInsn(packet.name)
            init.visitMethodInsn(INVOKESTATIC, helperType.internalName, "newHelperClass", "(${wrappedPacketType.descriptor}Ljava/lang/String;)${helperType.descriptor}", false)
            init.visitFieldInsn(PUTFIELD, classNameInternal, "helper", helperType.descriptor)
            init.visitInsn(RETURN)
            init.visitMaxs(0, 0)
            init.visitEnd()
        }

        // Bridge: helper.getWrappedPacket()
        cw.visitMethod(ACC_PUBLIC, "getWrappedPacket", "()${packetType.descriptor}", null, null).also { getWrappedPacket ->
            getWrappedPacket.visitCode()
            getWrappedPacket.visitVarInsn(ALOAD, 0)
            getWrappedPacket.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            getWrappedPacket.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "getWrappedPacket", "()${packetType.descriptor}", false)
            getWrappedPacket.visitInsn(ARETURN)
            getWrappedPacket.visitMaxs(0, 0)
            getWrappedPacket.visitEnd()
        }

        // Bridge: helper.setWrappedPacket(Packet)
        cw.visitMethod(ACC_PUBLIC, "setWrappedPacket", "(${packetType.descriptor})V", null, null).also { setWrappedPacket ->
            setWrappedPacket.visitCode()
            setWrappedPacket.visitVarInsn(ALOAD, 0)
            setWrappedPacket.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            setWrappedPacket.visitVarInsn(ALOAD, 1)
            setWrappedPacket.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "setWrappedPacket", "(${packetType.descriptor})V", false)
            setWrappedPacket.visitInsn(RETURN)
            setWrappedPacket.visitMaxs(0, 0)
            setWrappedPacket.visitEnd()
        }

        // Bridge + helper: helper.read(ByteBuf)
        cw.visitMethod(ACC_PUBLIC, "read", "(${byteBufType.descriptor})V", null, null).also { read ->
            read.visitCode()
            read.visitVarInsn(ALOAD, 0)
            read.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            read.visitVarInsn(ALOAD, 1)
            read.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "read", "(${byteBufType.descriptor})V", false)
            read.visitInsn(RETURN)
            read.visitMaxs(0, 0)
            read.visitEnd()
        }

        // Bridge + helper: helper.write(ByteBuf)
        cw.visitMethod(ACC_PUBLIC, "write", "(${byteBufType.descriptor})V", null, null).also { write ->
            write.visitCode()
            write.visitVarInsn(ALOAD, 0)
            write.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            write.visitVarInsn(ALOAD, 1)
            write.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "write", "(${byteBufType.descriptor})V", false)
            write.visitInsn(RETURN)
            write.visitMaxs(0, 0)
            write.visitEnd()
        }

        // Bridge: helper.handle(AbstractPacketHandler)
        cw.visitMethod(ACC_PUBLIC, "handle", "(${aphType.descriptor})V", null, null).also { abh ->
            abh.visitCode()
            abh.visitVarInsn(ALOAD, 0)
            abh.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            abh.visitVarInsn(ALOAD, 1)
            abh.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "handle", "(${aphType.descriptor})V", false)
            abh.visitInsn(RETURN)
            abh.visitMaxs(0, 0)
            abh.visitEnd()
        }

        // Bridge + helper: helper.equals(Object)
        cw.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)I", null, null).also { equals ->
            equals.visitCode()
            equals.visitVarInsn(ALOAD, 0)
            equals.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            equals.visitVarInsn(ALOAD, 1)
            equals.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "equals", "(Ljava/lang/Object;)I", false)
            equals.visitInsn(IRETURN)
            equals.visitMaxs(0, 0)
            equals.visitEnd()
        }

        // helper: helper.hashCode()
        cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null).also { hashCode ->
            hashCode.visitCode()
            hashCode.visitVarInsn(ALOAD, 0)
            hashCode.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            hashCode.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "hashCode", "()I", false)
            hashCode.visitInsn(IRETURN)
            hashCode.visitMaxs(0, 0)
            hashCode.visitEnd()
        }

        // helper: helper.toString()
        cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null).also { toString ->
            toString.visitCode()
            toString.visitVarInsn(ALOAD, 0)
            toString.visitFieldInsn(GETFIELD, classNameInternal, "helper", helperType.descriptor)
            toString.visitMethodInsn(INVOKEVIRTUAL, helperType.internalName, "toString", "()Ljava/lang/String;", false)
            toString.visitInsn(ARETURN)
            toString.visitMaxs(0, 0)
            toString.visitEnd()
        }
        cw.visitEnd()

        // Define and load the class
        @Suppress("UNCHECKED_CAST")
        return BungeeProtocolLib::class.java.classLoader.defineClass(className, cw.toByteArray()) as Class<out W>
    }

    /**
     * Generates fast constructor class for given [Packet] class
     */
    private fun <P: Packet, C: MagicConstructor<out P>> generateConstructor(packet: Class<out P>): C {
        val packetClassNameInternal = packet.name.replace('.', '/')
        val className = PacketWrapperGenerator::class.java.name + ".generated.Constructor${packet.className}"
        val classNameInternal = className.replace('.', '/')
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS)
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, classNameInternal, null, magicImplType.internalName, arrayOf(magicConstructorType.internalName))

        cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).also { init ->
            init.visitVarInsn(ALOAD, 0)
            init.visitMethodInsn(INVOKESPECIAL, magicImplType.internalName, "<init>", "()V", false)
            init.visitInsn(RETURN)
            init.visitMaxs(1, 0)
            init.visitEnd()
        }

        cw.visitMethod(ACC_PUBLIC, "construct", "()${packetType.descriptor}", null, null).also { construct ->
            construct.visitTypeInsn(NEW, packetClassNameInternal)
            construct.visitInsn(DUP)
            construct.visitMethodInsn(INVOKESPECIAL, packetClassNameInternal, "<init>", "()V", false)
            construct.visitInsn(ARETURN)
            construct.visitMaxs(0, 0)
            construct.visitEnd()
        }

        cw.visitEnd()
        val cClass = BungeeProtocolLib::class.java.classLoader.defineClass(className, cw.toByteArray())

        @Suppress("UNCHECKED_CAST")
        return cClass.getConstructor().newInstance() as C
    }
}