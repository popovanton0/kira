package sdf


import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.enum
import com.popovanton0.kira.suppliers.nullableEnum
import com.popovanton0.kira.suppliers.string
import com.popovanton0.kira.suppliers.nullableString
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.nullableBoolean
import com.popovanton0.kira.suppliers.compound.compound
import com.popovanton0.kira.suppliers.compound.nullableCompound
import com.popovanton0.kira.suppliers.string
import com.popovanton0.kira.suppliers.compound.nullableCompound

public data class ExampleFunction3Misses(
    val ds8: Supplier<sdf.B?>,
    val ds9: Supplier<sdf.A>,
    val ds10: ExampleFunction3Misses.ds10Misses,
) {
    public data class ds10Misses(
        val engine2: ds10Misses.engine2Misses,
        val sdf: Supplier<sdf.A?>,
    ) {
        public data class engine2Misses(
            val adsad: Supplier<sdf.A>,
            val hnghgj: Supplier<sdf.B>,
        ) {
        }

    }

}

public fun ExampleFunction3RootSupplier(
    misses: ExampleFunction3Misses
) = com.popovanton0.kira.suppliers.compound.root() {
    val ds1 = enum<sdf.Quality>(paramName = "ds1")
    val ds2 = nullableEnum<sdf.Quality?>(paramName = "ds2", defaultValue = null)
    val ds3 = string(paramName = "paramName", defaultValue = "Example")
    val ds4 = nullableString(paramName = "paramName", defaultValue = null)
    val ds5 = boolean(paramName = "paramName", defaultValue = false)
    val ds6 = nullableBoolean(paramName = "paramName", defaultValue = null)
    val ds7 = compound<sdf.Rock>(
        paramName = "ds7",
        label = "sdf.Rock"
    ) {
        injector {
            sdf.Rock
        }
    }
    val ds8 = misses.ds8
    val ds9 = misses.ds9
    val ds10 = nullableCompound<sdf.Engine>(
        paramName = "ds10",
        label = "sdf.Engine",
        isNullByDefault = true
    ) {
        val model = string(paramName = "paramName", defaultValue = "Example")
        val engine2 = nullableCompound<sdf.Engine2>(
            paramName = "engine2",
            label = "sdf.Engine2",
            isNullByDefault = true
        ) {
            val adsad = misses.ds10.engine2.adsad
            val hnghgj = misses.ds10.engine2.hnghgj

            injector {
                sdf.Engine2(
                    adsad = adsad.currentValue(),
                    hnghgj = hnghgj.currentValue()
                )
            }
        }
        val sdf = misses.ds10.sdf

        injector {
            sdf.Engine(
                model = model.currentValue(),
                engine2 = engine2.currentValue(),
                sdf = sdf.currentValue()
            )
        }
    }

    injector {
        sdf.ExampleFunction3(
            ds1 = ds1.currentValue(),
            ds2 = ds2.currentValue(),
            ds3 = ds3.currentValue(),
            ds4 = ds4.currentValue(),
            ds5 = ds5.currentValue(),
            ds6 = ds6.currentValue(),
            ds7 = ds7.currentValue(),
            ds8 = ds8.currentValue(),
            ds9 = ds9.currentValue(),
            ds10 = ds10.currentValue()
        )
    }
}
