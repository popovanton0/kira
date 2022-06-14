package com.popovanton0.kira.suppliers.dataclass

import com.popovanton0.kira.suppliers.BooleanInDataClass
import com.popovanton0.kira.suppliers.EnumInDataClass
import com.popovanton0.kira.suppliers.ObjectInDataClass
import com.popovanton0.kira.suppliers.StringInDataClass
import com.popovanton0.kira.suppliers.WholeNumberInDataClass

public object DataClassSupplierConfig {
    internal val paramSupplierProviders: MutableList<DataClassSupplierSupport> = mutableListOf(
        BooleanInDataClass,
        WholeNumberInDataClass,
        StringInDataClass,
        EnumInDataClass,
        ObjectInDataClass,
    )

    public fun addParamSupplierProvider(provider: DataClassSupplierSupport): Unit =
        paramSupplierProviders.add(0, provider)
}