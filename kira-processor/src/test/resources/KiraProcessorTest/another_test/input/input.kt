import com.popovanton0.kira.annotations.Kira
import Composable as DF

@Target(AnnotationTarget.TYPE)
private annotation class Composable


//@Kira
fun ExampleFunction2(
    ds: () -> Unit,
    ds2: suspend () -> Unit,
    ds3: @DF (@DF Char.() -> Unit).(Int) -> Unit,
    ds4: List<MutableList<in () -> Map<Any?, (suspend () -> Float?)?>>>
) {

}

enum class Quality { GOOD, BAD }
object Rock
data class Engine(
    val model: String = "Merlin",
    val diesel: Engine2,
)
data class Engine2(
    val adsad: String = "Merlin",
    val hnghgj: Boolean = false,
)

@Kira
fun ExampleFunction3(
    ds1: Quality = Quality.GOOD,
    ds2: Quality? = Quality.GOOD,
    ds3: String,
    ds4: String?,
    ds5: Boolean,
    ds6: Boolean?,
    ds7: Rock,
    ds8: Rock?,
    ds9: Engine,
    vararg ds10: Engine? = arrayOf(null),
) {

}

class A(b: B, c: C)
class B(c: C)
class C(a: A)