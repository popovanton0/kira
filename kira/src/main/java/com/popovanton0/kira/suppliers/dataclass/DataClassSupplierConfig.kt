package com.popovanton0.kira.suppliers.dataclass

import com.popovanton0.kira.suppliers.BooleanInDataClass
import com.popovanton0.kira.suppliers.EnumInDataClass
import com.popovanton0.kira.suppliers.ObjectInDataClass
import com.popovanton0.kira.suppliers.StringInDataClass

internal object DataClassSupplierConfig {
    val paramSupplierProviders: MutableList<DataClassSupplierSupport> = mutableListOf(
        BooleanInDataClass,
        EnumInDataClass,
        StringInDataClass,
        ObjectInDataClass,
    )
}