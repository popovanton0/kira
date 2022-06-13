package data_class_processor

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira
fun ExampleFunction(car: Car, carN: Car?) = Unit

data class Car(
    val model: String,
    val isFast: Boolean,
)