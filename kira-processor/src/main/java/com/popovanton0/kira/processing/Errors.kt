package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.processing.KiraProcessor.Companion.kiraAnnClass
import com.popovanton0.kira.processing.KiraProcessor.Companion.kiraRootAnnClass

internal object Errors {
    private fun String.removeNewLines() = replace("\n", "")

    fun manyRootClasses(rootAnns: List<KSAnnotated>): Nothing {
        val annotatedClassNames = rootAnns.joinToString {
            val classDeclaration = it as KSClassDeclaration
            (classDeclaration.qualifiedName ?: classDeclaration.simpleName).asString()
        }
        error(
            "Multiple classes annotated with @${kiraRootAnnClass.simpleName} were detected, " +
                    "but only 1 is allowed: $annotatedClassNames"
        )
    }

    fun funParamWithNoName(fullFunName: String): Nothing = error(
        "One of $fullFunName's params has no name"
    )

    fun reservedParamName(fullFunName: String, paramName: String): Nothing =
        error(
            """Function $fullFunName has a "$paramName" param that has reserved name. Please, 
                |rename this param or remove @${kiraAnnClass.simpleName} annotation from 
                |$fullFunName""".trimMargin().removeNewLines()
        )

    fun resolutionAmbiguity(funNames: String): Nothing = error(
        """Resolution ambiguity: multiple functions found with the same name: $funNames. To fix: 
            |1. provide unique @${kiraAnnClass.simpleName}.name values;
            |2. rename functions or/and move them to different packages;""".trimMargin()
    )
}