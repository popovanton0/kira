import com.popovanton0.kira.annotations.Kira
import Composable as DF

@Target(AnnotationTarget.TYPE)
private annotation class Composable


@Kira
fun ExampleFunction2(ds: () -> Unit, ds2: suspend () -> Unit, ds3: @DF (@DF Char.()->Unit).(Int) -> Unit) = Unit