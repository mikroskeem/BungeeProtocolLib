/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.reflection

private val defineClassMethod = ClassLoader::class.java
        .getDeclaredMethod("defineClass", String::class.java, ByteArray::class.java, Int::class.java, Int::class.java)
        .accessible()

fun ClassLoader.defineClass(className: String, classData: ByteArray): Class<*> {
    return defineClassMethod.invokeCasted(this, className, classData, 0, classData.size)
}

@Suppress("UNCHECKED_CAST")
fun <T> findClass(name: String) = Class.forName(name) as Class<T>