package function_with_unicode_name_and_specified_alternative_name

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot
object RootModule

@Suppress("IllegalIdentifier", "NonAsciiCharacters")
@Kira(name = "ExampleFunction_SmileyFace")
fun `ExampleFunction😃`(param1: String) = Unit