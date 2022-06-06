package many_kira_roots

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@KiraRoot(generateRegistry = false)
object RootModule2

@Kira
fun ExampleFunction() = Unit