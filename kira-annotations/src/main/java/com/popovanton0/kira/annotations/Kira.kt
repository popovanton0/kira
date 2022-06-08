package com.popovanton0.kira.annotations

/**
 * @param name needs to be specified if the functions name contains unicode characters that require
 * escaping using backticks OR if there is another function overload with the same name
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public annotation class Kira(val name: String = "") {
    public companion object {
        public const val GENERATED_PACKAGE_PREFIX: String = "com.popovanton0.kira.generated"
    }
}
