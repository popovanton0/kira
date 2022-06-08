package com.popovanton0.kira.annotations

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class KiraDeclarationsAggregator(val qualifiedDeclarationNames: Array<String>)
