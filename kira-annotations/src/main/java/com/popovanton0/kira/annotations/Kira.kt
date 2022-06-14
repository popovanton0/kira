package com.popovanton0.kira.annotations

/**
 * @param name needs to be specified (not [isBlank]) if:
 * 1. the function's name contains unicode characters that require escaping using backticks
 *     - [name] MUST be a simple name of the function (without package name)
 * 2. there is another function overload with the same name
 *     - [name] MUST be a simple name of the function (without package name)
 * 3. [Kira] is used as a parameter in [KiraRoot.externalFunctionsToProcess]
 *     - [name] MUST be qualified name of the function (__with__ package name)
 *
 * @param useDefaultValueForParams names of function parameters, processing for which should be
 * skipped. Those parameters should have default values, or else generated injector would not
 * compile
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public annotation class Kira(
    val name: String = "",
    val useDefaultValueForParams: Array<String> = []
) {
    public companion object {
        public const val GENERATED_PACKAGE_PREFIX: String = "com.popovanton0.kira.generated"
    }
}
