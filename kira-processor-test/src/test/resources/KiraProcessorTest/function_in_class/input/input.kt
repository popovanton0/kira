package function_in_class

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

object SomeClass {
    @Kira
    fun ExampleFunction() = Unit
}