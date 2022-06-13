package whole_number_processor

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira
fun ExampleFunction(
    byte: Byte,
    short: Short,
    int: Int,
    long: Long,
    uByte: UByte,
    uShort: UShort,
    uInt: UInt,
    uLong: ULong,
    nullableByte: Byte?,
    nullableShort: Short?,
    nullableInt: Int?,
    nullableLong: Long?,
    nullableUByte: UByte?,
    nullableUShort: UShort?,
    nullableUInt: UInt?,
    nullableULong: ULong?,
) = Unit
