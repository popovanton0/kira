package com.popovanton0.kira.suppliers.base

import com.popovanton0.kira.suppliers.base.ClassType.ClassModifier
import kotlin.reflect.KClass

public sealed class Type {
    public abstract val nullable: Boolean?

    public abstract fun nullable(): Type
    public abstract fun notNullable(): Type
}

public data class ClassType constructor(
    val qualifiedName: String,
    val variant: Variant = Variant.CLASS,
    val typeArgs: List<Type> = emptyList(),
    val modifiers: Set<ClassModifier> = emptySet(),
    public override val nullable: Boolean? = null,
) : Type() {

    public enum class Variant {
        CLASS, OBJECT, INTERFACE
    }

    public enum class ClassModifier {
        ENUM, SEALED, ANNOTATION, DATA, ABSTRACT, VALUE,
    }

    override fun nullable(): Type = if (nullable != true) copy(nullable = true) else this
    override fun notNullable(): Type = if (nullable != false) copy(nullable = false) else this
}

public data class LambdaType(
    val displayName: String,
    public override val nullable: Boolean? = null,
) : Type() {
    override fun nullable(): Type = if (nullable != true) copy(nullable = true) else this
    override fun notNullable(): Type = if (nullable != false) copy(nullable = false) else this
}

public fun <T : Any> KClass<T>.toClassType(nullable: Boolean? = null): ClassType = ClassType(
    nullable = nullable,
    qualifiedName = qualifiedName!!,
    variant = when {
        java.isInterface && !java.isAnnotation -> ClassType.Variant.INTERFACE
        objectInstance != null -> ClassType.Variant.OBJECT
        else -> ClassType.Variant.CLASS
    },
    typeArgs = typeParameters.map { ClassType("*", nullable = false) },
    modifiers = buildSet {
        if (java.isEnum) add(ClassModifier.ENUM)
        if (isSealed) add(ClassModifier.SEALED)
        if (java.isAnnotation) add(ClassModifier.ANNOTATION)
        if (isData) add(ClassModifier.DATA)
        if (isAbstract) add(ClassModifier.ABSTRACT)
        if (isValue) add(ClassModifier.VALUE)
    },
)
