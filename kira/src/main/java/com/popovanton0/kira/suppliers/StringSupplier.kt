package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.dataclass.DataClassSupplierSupport
import com.popovanton0.kira.ui.NullableTextField
import com.popovanton0.kira.ui.TextField
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSuperclassOf

public fun KiraScope.string(
    paramName: String,
    defaultValue: String = "Lorem",
): StringSupplierBuilder =
    StringSupplierBuilder(paramName, defaultValue).also(::addSupplierBuilder)

public fun KiraScope.nullableString(
    paramName: String,
    defaultValue: String? = null,
): NullableStringSupplierBuilder =
    NullableStringSupplierBuilder(paramName, defaultValue).also(::addSupplierBuilder)

public class StringSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: String,
) : SupplierBuilder<String>() {
    override fun provideSupplier(): Supplier<String> =
        NullableStringSupplierImpl(paramName, defaultValue, nullable = false)
}

public class NullableStringSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: String?,
) : SupplierBuilder<String?>() {
    override fun provideSupplier(): Supplier<String?> =
        NullableStringSupplierImpl(paramName, defaultValue, nullable = true)
}

private class NullableStringSupplierImpl<T : String?>(
    private val paramName: String,
    defaultValue: T,
    private val nullable: Boolean,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(defaultValue)

    @Composable
    override fun Ui(params: Any?) {
        if (nullable) {
            NullableTextField(
                value = currentValue,
                onValueChange = { currentValue = it as T },
                paramName = paramName,
                type = ClassType("String", nullable = true),
            )
        } else {
            TextField(
                value = currentValue!!,
                onValueChange = { currentValue = it as T },
                paramName = paramName,
                type = ClassType("String"),
            )
        }
    }
}

internal object StringInDataClass : DataClassSupplierSupport {
    override fun KiraScope.provideSupplierBuilderForParam(
        param: KParameter, paramClass: KClass<Any>, nullable: Boolean, defaultValue: Any?
    ): SupplierBuilder<*>? = when {
        !paramClass.isSuperclassOf(String::class) -> null
        nullable -> nullableString(param.name!!, defaultValue as String?)
        defaultValue != null -> string(param.name!!, defaultValue as String)
        else -> string(param.name!!)
    }
}

@Preview
@Composable
private fun Preview() = KiraScope().string("param name").build().Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableString("param name").build().Ui()