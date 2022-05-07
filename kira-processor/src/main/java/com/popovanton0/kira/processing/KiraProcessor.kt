package com.popovanton0.kira.processing

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated


class KiraProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        //val d: KSClassDeclaration = TODO()
        //d.getSealedSubclasses()
        //val s: KSFunctionDeclaration = TODO()
        //s.parameters.first().type.resolve().starProjection()
        try{
            environment.codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = "com.popovanton0.kira.processing",
                fileName = "Test123"
            ).writer().use {
                it.write("private val a = 0")
            }
        } catch (e: FileAlreadyExistsException) {

        }
        return emptyList()
    }
}

class KiraProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KiraProcessor(environment)
    }
}


