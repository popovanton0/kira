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
import java.text.DecimalFormat
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSuperclassOf

public fun KiraScope.byte(
    paramName: String,
    defaultValue: Byte = 0,
): WholeNumberSupplierBuilder<Byte> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Byte)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableByte(
    paramName: String,
    defaultValue: Byte? = null,
): NullableWholeNumberSupplierBuilder<Byte> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Byte)
        .also(::addSupplierBuilder)

public fun KiraScope.short(
    paramName: String,
    defaultValue: Short = 0,
): WholeNumberSupplierBuilder<Short> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Short)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableShort(
    paramName: String,
    defaultValue: Short? = null,
): NullableWholeNumberSupplierBuilder<Short> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Short)
        .also(::addSupplierBuilder)

public fun KiraScope.int(
    paramName: String,
    defaultValue: Int = 0,
): WholeNumberSupplierBuilder<Int> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Int)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableInt(
    paramName: String,
    defaultValue: Int? = null,
): NullableWholeNumberSupplierBuilder<Int> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Int)
        .also(::addSupplierBuilder)

public fun KiraScope.long(
    paramName: String,
    defaultValue: Long = 0,
): WholeNumberSupplierBuilder<Long> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Long)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableLong(
    paramName: String,
    defaultValue: Long? = null,
): NullableWholeNumberSupplierBuilder<Long> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.Long)
        .also(::addSupplierBuilder)

public fun KiraScope.uByte(
    paramName: String,
    defaultValue: UByte = 0u,
): WholeNumberSupplierBuilder<UByte> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.UByte)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableUByte(
    paramName: String,
    defaultValue: UByte? = null,
): NullableWholeNumberSupplierBuilder<UByte> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.UByte)
        .also(::addSupplierBuilder)

public fun KiraScope.uShort(
    paramName: String,
    defaultValue: UShort = 0u,
): WholeNumberSupplierBuilder<UShort> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.UShort)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableUShort(
    paramName: String,
    defaultValue: UShort? = null,
): NullableWholeNumberSupplierBuilder<UShort> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.UShort)
        .also(::addSupplierBuilder)

public fun KiraScope.uInt(
    paramName: String,
    defaultValue: UInt = 0u,
): WholeNumberSupplierBuilder<UInt> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.UInt)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableUInt(
    paramName: String,
    defaultValue: UInt? = null,
): NullableWholeNumberSupplierBuilder<UInt> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.UInt)
        .also(::addSupplierBuilder)

public fun KiraScope.uLong(
    paramName: String,
    defaultValue: ULong = 0u,
): WholeNumberSupplierBuilder<ULong> =
    WholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.ULong)
        .also(::addSupplierBuilder)

public fun KiraScope.nullableULong(
    paramName: String,
    defaultValue: ULong? = null,
): NullableWholeNumberSupplierBuilder<ULong> =
    NullableWholeNumberSupplierBuilder(paramName, defaultValue, WholeNumberType.ULong)
        .also(::addSupplierBuilder)

internal sealed class WholeNumberType(
    val name: String,
    val convertor: (String) -> Any?,
    val range: ClosedFloatingPointRange<Double>,
) {
    val errorMsg get() = "${range.start.formatNumber()} ≤ x ≤ ${range.endInclusive.formatNumber()}"

    private fun Double.formatNumber(): String = DecimalFormat.getInstance()
        .apply { maximumFractionDigits = 0 }
        .format(this)

    object Int : WholeNumberType(
        name = "Int",
        convertor = { it.toIntOrNull() },
        range = kotlin.Int.MIN_VALUE.toDouble()..kotlin.Int.MAX_VALUE.toDouble()
    )

    object Long : WholeNumberType(
        name = "Long",
        convertor = { it.toLongOrNull() },
        range = kotlin.Long.MIN_VALUE.toDouble()..kotlin.Long.MAX_VALUE.toDouble()
    )

    object Byte : WholeNumberType(
        name = "Byte",
        convertor = { it.toByteOrNull() },
        range = kotlin.Byte.MIN_VALUE.toDouble()..kotlin.Byte.MAX_VALUE.toDouble()
    )

    object Short : WholeNumberType(
        name = "Short",
        convertor = { it.toShortOrNull() },
        range = kotlin.Short.MIN_VALUE.toDouble()..kotlin.Short.MAX_VALUE.toDouble()
    )

    object UInt : WholeNumberType(
        name = "UInt",
        convertor = { it.toUIntOrNull() },
        range = kotlin.UInt.MIN_VALUE.toDouble()..kotlin.UInt.MAX_VALUE.toDouble()
    )

    object ULong : WholeNumberType(
        name = "ULong",
        convertor = { it.toULongOrNull() },
        range = kotlin.ULong.MIN_VALUE.toDouble()..kotlin.ULong.MAX_VALUE.toDouble()
    )

    object UByte : WholeNumberType(
        name = "UByte",
        convertor = { it.toUByteOrNull() },
        range = kotlin.UByte.MIN_VALUE.toDouble()..kotlin.UByte.MAX_VALUE.toDouble()
    )

    object UShort : WholeNumberType(
        name = "UShort",
        convertor = { it.toUShortOrNull() },
        range = kotlin.UShort.MIN_VALUE.toDouble()..kotlin.UShort.MAX_VALUE.toDouble()
    )
}

public class WholeNumberSupplierBuilder<T : Any> internal constructor(
    public var paramName: String,
    public var defaultValue: T,
    private val numberType: WholeNumberType,
) : SupplierBuilder<T>() {
    override fun provideSupplier(): Supplier<T> =
        WholeNumberSupplierImpl(paramName, defaultValue, numberType)
}

public class NullableWholeNumberSupplierBuilder<T : Any> internal constructor(
    public var paramName: String,
    public var defaultValue: T?,
    private val numberType: WholeNumberType,
) : SupplierBuilder<T?>() {
    override fun provideSupplier(): Supplier<T?> =
        NullableWholeNumberSupplierImpl(paramName, defaultValue, numberType)
}

private class WholeNumberSupplierImpl<T : Any>(
    private val paramName: String,
    defaultValue: T,
    private val numberType: WholeNumberType,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(listOf(defaultValue).first())
    private var isError: Boolean by mutableStateOf(false)
    private var textValue: String by mutableStateOf(defaultValue.toString())

    @Composable
    override fun Ui(params: Any?) {
        TextField(
            value = textValue,
            onValueChange = { newText ->
                textValue = newText
                val newNumber = numberType.convertor(newText)
                isError = newNumber == null
                if (newNumber != null) currentValue = newNumber as T
            },
            paramName = paramName,
            type = ClassType(numberType.name),
            errorMsg = if (isError) numberType.errorMsg else null,
            singleLine = true
        )
    }
}

private class NullableWholeNumberSupplierImpl<T : Any?>(
    private val paramName: String,
    defaultValue: T,
    private val numberType: WholeNumberType,
) : PropertyBasedSupplier<T?> {
    override var currentValue: T? by mutableStateOf(defaultValue)
    private var isError: Boolean by mutableStateOf(false)
    private var textValue: String? by mutableStateOf(defaultValue?.toString())

    @Composable
    override fun Ui(params: Any?) {
        NullableTextField(
            value = textValue,
            onValueChange = { newText ->
                textValue = newText
                if (newText == null) {
                    currentValue = null
                    isError = false
                } else {
                    val newNumber = numberType.convertor(newText)
                    isError = newNumber == null
                    if (newNumber != null) currentValue = newNumber as T
                }
            },
            paramName = paramName,
            type = ClassType(numberType.name, nullable = true),
            errorMsg = if (isError) numberType.errorMsg else null,
            singleLine = true
        )
    }
}

internal object WholeNumberInDataClass : DataClassSupplierSupport {
    override fun KiraScope.provideSupplierBuilderForParam(
        param: KParameter, paramClass: KClass<Any>, nullable: Boolean, defaultValue: Any?
    ): SupplierBuilder<*>? = when {
        paramClass.isSuperclassOf(Byte::class) -> when {
            nullable -> nullableByte(param.name!!, defaultValue as Byte?)
            defaultValue != null -> byte(param.name!!, defaultValue as Byte)
            else -> byte(param.name!!)
        }
        paramClass.isSuperclassOf(Short::class) -> when {
            nullable -> nullableShort(param.name!!, defaultValue as Short?)
            defaultValue != null -> short(param.name!!, defaultValue as Short)
            else -> short(param.name!!)
        }
        paramClass.isSuperclassOf(Int::class) -> when {
            nullable -> nullableInt(param.name!!, defaultValue as Int?)
            defaultValue != null -> int(param.name!!, defaultValue as Int)
            else -> int(param.name!!)
        }
        paramClass.isSuperclassOf(Long::class) -> when {
            nullable -> nullableLong(param.name!!, defaultValue as Long?)
            defaultValue != null -> long(param.name!!, defaultValue as Long)
            else -> long(param.name!!)
        }
        else -> null
    }
}

@Preview
@Composable
private fun Preview() = KiraScope().byte("param name").build().Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableByte("param name").build().Ui()
