/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.reflection

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method

fun <T: AccessibleObject> T.accessible(): T = this.apply { isAccessible = true }

@Suppress("UNCHECKED_CAST")
fun <T> Field.getCasted(instance: Any?): T = get(instance) as T

@Suppress("UNCHECKED_CAST")
fun <T> Method.invokeCasted(instance: Any?, vararg args: Any?): T = invoke(instance, *args) as T

val Class<*>.className: String get() = name.run { substring(lastIndexOf(".") + 1) }

fun Class<*>.getEnum(name: String): Any = getField(name).accessible().get(null)