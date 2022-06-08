// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.function_with_unicode_name_and_specified_alternative_name

import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.StringSupplierBuilder
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.string
import function_with_unicode_name_and_specified_alternative_name.`ExampleFunction😃`
import kotlin.String
import kotlin.collections.List
import kotlin.collections.listOf

public class Kira_ExampleFunction_SmileyFace() : KiraProvider<ExampleFunction_SmileyFaceScope> {
  public override val kira: Kira<ExampleFunction_SmileyFaceScope> =
      kira(ExampleFunction_SmileyFaceScope()) {
    param1 = string(paramName = "param1", defaultValue = "Lorem")
    injector {
      `ExampleFunction😃`(
        param1 = param1.currentValue(),
      )
    }
  }

}

public class ExampleFunction_SmileyFaceScope :
    GeneratedKiraScopeWithImpls<ExampleFunction_SmileyFaceScope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var param1: Supplier<String>

  public override fun collectSuppliers(): List<Supplier<*>> = listOf(param1, )

  public class SupplierImplsScope(
    private val scope: ExampleFunction_SmileyFaceScope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var param1: StringSupplierBuilder
      get() = scope.param1 as? StringSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.param1 = value
      }
  }
}