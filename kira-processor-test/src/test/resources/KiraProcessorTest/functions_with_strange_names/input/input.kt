package functions_with_strange_names

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira fun misses(sdf: Throwable) = Unit
@Kira fun kira() = Unit
@Kira fun injector() = Unit
@Kira fun string() = Unit
@Kira fun Supplier() = Unit
