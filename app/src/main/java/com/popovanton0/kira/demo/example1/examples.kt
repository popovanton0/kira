package com.popovanton0.kira.demo.example1

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.generated.com.popovanton0.exampleui.Kira_SimpleTextCard
import com.popovanton0.kira.registry.KiraRegistry

@Preview
@Composable
fun KiraProviderExample() = KiraScreen(Kira_SimpleTextCard())

@Preview
@Composable
fun KiraRegistryExample() {
    val (funName, kiraProvider) = KiraRegistry.kiraProviders.entries.first()
    KiraScreen(kiraProvider)
}

@Kira
@Composable
fun WholeNumbersFun(
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
) {
    Text(
        text = """
        byte = $byte
        short = $short
        int = $int
        long = $long
        uByte = $uByte
        uShort = $uShort
        uInt = $uInt
        uLong = $uLong
        nullableByte = $nullableByte
        nullableShort = $nullableShort
        nullableInt = $nullableInt
        nullableLong = $nullableLong
        nullableUByte = $nullableUByte
        nullableUShort = $nullableUShort
        nullableUInt = $nullableUInt
        nullableULong = $nullableULong
    """.trimIndent(),
        fontSize = 10.sp
    )
}

@Kira
@Composable
fun WholeNumbersInDataClass(wholeNumbers: WholeNumbers) = with(wholeNumbers) {
    Text(
        text = """
        byte = $byte
        short = $short
        int = $int
        long = $long
        nullableByte = $nullableByte
        nullableShort = $nullableShort
        nullableInt = $nullableInt
        nullableLong = $nullableLong
    """.trimIndent(),
        fontSize = 10.sp
    )
}

data class WholeNumbers(
    val byte: Byte = 0,
    val short: Short = 0,
    val int: Int = 0,
    val long: Long = 0,
    val nullableByte: Byte? = 0,
    val nullableShort: Short? = 0,
    val nullableInt: Int? = 0,
    val nullableLong: Long? = 12,
)