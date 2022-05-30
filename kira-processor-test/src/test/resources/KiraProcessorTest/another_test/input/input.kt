package another_test

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot
import java.util.function.Supplier

@Target(AnnotationTarget.TYPE)
private annotation class Composable


//@Kira
fun ExampleFunction2(
    ds: () -> Unit,
    ds2: suspend () -> Unit,
    ds3: @Composable (@Composable Char.() -> Unit).(Int) -> Unit,
    ds4: List<MutableList<in () -> Map<Any?, (suspend () -> Float?)?>>>
) {

}

enum class Quality { GOOD, BAD }
object Rock
data class Engine(
    val model: String = "Merlin",
    val engine2: Engine2?,
    val sdf: A?,
)
data class Engine2(
    val adsad: A,
    val hnghgj: B,
)

public data class ExampleFunction3Misses(
    val ds10: ds10Misses
) {
    public data class ds10Misses(
        val sdf: Supplier<A?>,
        val engine2: ds10Misses.engine2Misses,
    ) {
        public data class engine2Misses(
            val adsad: Supplier<A>,
        )
    }
}


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
    ds9: B?,
    //ds9: A,
    //ds10: Engine?,
    vararg ds10: Engine? = arrayOf(null),
) {

}

@KiraRoot
object RootModule

abstract class A(b: B, c: C)
interface B//(c: C)
class C(a: A)