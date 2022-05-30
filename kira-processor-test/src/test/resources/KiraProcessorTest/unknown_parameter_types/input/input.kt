package unknown_parameter_types

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@Kira
fun ExampleFunction(
    ds5: Boolean,
    ds6: Boolean?,
    ds8: B?,
    ds9: A,
) = Unit

abstract class A(b: B)
interface B

@KiraRoot
object RootModule
