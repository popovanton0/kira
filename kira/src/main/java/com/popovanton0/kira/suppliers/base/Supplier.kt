package com.popovanton0.kira.suppliers.base

import androidx.compose.runtime.Composable

public interface Supplier<T> {

    @Composable
    public fun currentValue(): T

    @Composable
    public fun Ui()

    public fun initialize(): Unit = Unit

    /*
    TODO Char

    TODO Byte
    TODO Short
    TODO Int
    TODO Long

    TODO Float
    TODO Double
    */
}