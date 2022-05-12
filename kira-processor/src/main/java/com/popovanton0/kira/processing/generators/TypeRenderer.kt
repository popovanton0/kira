package com.popovanton0.kira.processing.generators

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Variance
import com.popovanton0.kira.processing.qualifiedName

internal fun KSType.render(variance: Variance = Variance.INVARIANT): String {
    check(!isError)
    if (isFunctionType || isSuspendFunctionType) {
        return renderFunctionalType(variance)
    }
    // assuming that type represents a class
    return renderClassType(variance)
}

private fun KSType.renderClassType(variance: Variance): String {
    val code = buildString {
        val className = declaration.qualifiedName!!.asString()
        if (variance.label.isNotEmpty()) {
            append(variance.label)
            append(' ')
        }
        append(className)
        if (arguments.isNotEmpty()) {
            append('<')
            arguments.forEachIndexed { index, arg ->
                append(arg.type!!.resolve().render(arg.variance))
                if (index != arguments.lastIndex) append(',')
            }
            append('>')
        }
        if (isMarkedNullable) append("?")
    }
    return code
}

private fun KSType.renderFunctionalType(variance: Variance): String {
    require(arguments.size == 1) {
        "Currently, only subtypes of `() -> Any?` are supported for functional types"
    }
    val isSuspend = declaration.simpleName.asString().startsWith("Suspend")
    val hasExtension = annotations.any {
        // doesn't work because of https://github.com/google/ksp/issues/985
        it.qualifiedName() == "kotlin.ExtensionFunctionType"
    }
    val isComposable = annotations.any {
        it.qualifiedName() == "androidx.compose.runtime.Composable"
    }
    val code = buildString {
        if (variance.label.isNotEmpty()) {
            append(variance.label)
            append(' ')
        }
        if (isMarkedNullable) append('(')
        if (isComposable) append("@androidx.compose.runtime.Composable ")
        if (isSuspend) append("suspend ")
        // assume that there is no explicitly specified variance in
        // function types like `Function1<Char, Unit>`
        // todo add parentheses if child type is functional
        val args = arguments.map { it.type!!.resolve().render() }

        if (hasExtension) append("${args[0]}.")
        append("(")
        args.forEachIndexed { index, arg ->
            // first arg type is the type of the extension
            if (index == 0 && hasExtension) return@forEachIndexed
            // last arg type is the return type
            if (index == args.lastIndex) return@forEachIndexed
            append(arg)
            if (index != args.lastIndex - 1) append(',')
        }
        append(") -> ")
        append(args.last()) // Function always has the return type
        if (isMarkedNullable) append(")?")
    }
    return code
}
