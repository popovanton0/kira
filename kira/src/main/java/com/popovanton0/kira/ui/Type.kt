package com.popovanton0.kira.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.LambdaType
import com.popovanton0.kira.suppliers.base.Type

@Composable
public fun TypeUi(type: Type) {
    Text(renderTypeString(type))
}

private fun renderTypeString(
    type: Type,
    omitModifiers: Boolean = false
): AnnotatedString = buildAnnotatedString {
    when (type) {
        is ClassType -> {
            append(type.variant.name.lowercase())
            if (!omitModifiers) {
                if (type.modifiers.isNotEmpty()) append(' ')
                renderModifiers(type)
            }
            append(' ')
            append(type.qualifiedName.substringAfterLast('.'))
            renderTypeArgs(type)
        }
        is LambdaType -> append(type.displayName)
    }
    if (type.nullable == true) append('?')
}

private fun AnnotatedString.Builder.renderModifiers(classType: ClassType) {
    classType.modifiers
        .sortedBy { if (it == ClassType.ClassModifier.ABSTRACT) 0 else 1 }
        .forEach { append(it.name.lowercase()) }
}

private fun AnnotatedString.Builder.renderTypeArgs(classType: ClassType) {
    val typeArgs = classType.typeArgs
    if (typeArgs.isEmpty()) return
    append('<')
    typeArgs.forEachIndexed { index, type ->
        append(renderTypeString(type, omitModifiers = true))
        if (index != typeArgs.lastIndex) append(',')
    }
    append('>')
}
