package com.popovanton0.kira.processing

sealed class Misses {
    data class Single(
        val paramName: String,
        val type: String,
    ) : Misses()

    data class Class(
        val paramName: String,
        val list: List<Misses>
    ) : Misses() {
        init {
            require(list.isNotEmpty())
        }
    }
}
