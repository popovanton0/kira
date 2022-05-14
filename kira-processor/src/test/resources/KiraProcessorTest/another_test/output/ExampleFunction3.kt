public fun ExampleFunction3RootSupplier() = com.popovanton0.kira.suppliers.compound.root() {
    val ds1 = com.popovanton0.kira.suppliers.enum(paramName = "ds1")
    val ds2 = com.popovanton0.kira.suppliers.nullableEnum(paramName = "ds2", defaultValue = null)
    val ds3 = com.popovanton0.kira.suppliers.string(paramName = "ds3", defaultValue = "Example")
    val ds4 = com.popovanton0.kira.suppliers.nullableString(paramName = "ds4", defaultValue = null)
    val ds5 = com.popovanton0.kira.suppliers.boolean(paramName = "ds5", defaultValue = false)
    val ds6 = com.popovanton0.kira.suppliers.nullableBoolean(paramName = "ds6", defaultValue = null)
    val ds7 = com.popovanton0.kira.suppliers.compound.compound(
        paramName = "ds7",
        label = "Rock"
    ) {
        com.popovanton0.kira.suppliers.compound.injector {
            Rock
        }
    }
    val ds8 = com.popovanton0.kira.suppliers.compound.nullableCompound(
        paramName = "ds8",
        label = "Rock?",
        defaultValue = null
    ) {
        com.popovanton0.kira.suppliers.compound.injector {
            Rock
        }
    }
    val ds9 = com.popovanton0.kira.suppliers.compound.compound(
        paramName = "ds9",
        label = "Engine"
    ) {
        val model = com.popovanton0.kira.suppliers.string(paramName = "model", defaultValue = "Example")
        val diesel = com.popovanton0.kira.suppliers.compound.nullableCompound(
            paramName = "diesel",
            label = "Engine2?",
            defaultValue = null
        ) {
            val adsad = com.popovanton0.kira.suppliers.string(paramName = "adsad", defaultValue = "Example")
            val hnghgj = com.popovanton0.kira.suppliers.boolean(paramName = "hnghgj", defaultValue = false)
            
            com.popovanton0.kira.suppliers.compound.injector {
                Engine2(
                    adsad = adsad.currentValue(),
                    hnghgj = hnghgj.currentValue()
                )
            }
        }
        
        com.popovanton0.kira.suppliers.compound.injector {
            Engine(
                model = model.currentValue(),
                diesel = diesel.currentValue()
            )
        }
    }
    
    com.popovanton0.kira.suppliers.compound.injector {
        ExampleFunction3(
            ds1 = ds1.currentValue(),
            ds2 = ds2.currentValue(),
            ds3 = ds3.currentValue(),
            ds4 = ds4.currentValue(),
            ds5 = ds5.currentValue(),
            ds6 = ds6.currentValue(),
            ds7 = ds7.currentValue(),
            ds8 = ds8.currentValue(),
            ds9 = ds9.currentValue()
        )
    }
}
