package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSTypeReference

/**
 * Calling this method is expensive because it calls [KSTypeReference.resolve]
 */
internal fun KSAnnotation.qualifiedName() =
    annotationType.resolve().declaration.qualifiedName?.asString()