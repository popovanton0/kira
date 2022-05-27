package sdf

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

enum class Quality { GOOD, BAD }

@Kira
fun ExampleFunction3(
    ds1: Quality = Quality.GOOD,
    ds2: Quality? = Quality.GOOD,
    ds3: String,
    ds4: String?,
    ds5: Boolean,
    ds6: Boolean?,
    ds8: B?,
    ds9: A,
) {

}

abstract class A(b: B)
interface B

@KiraRoot
object RootModule
