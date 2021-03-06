// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.function_overloads_with_specified_kira_name

import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.StringSupplierBuilder
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.string
import function_overloads_with_specified_kira_name.ExampleFunction
import kotlin.String
import kotlin.collections.List
import kotlin.collections.listOf

public class Kira_ExampleFunctionWithParams() : KiraProvider<ExampleFunctionWithParamsScope> {
  public override val kira: Kira<ExampleFunctionWithParamsScope> =
      kira(ExampleFunctionWithParamsScope()) {
    param1 = string(paramName = "param1")
    injector {
      ExampleFunction(
        param1 = param1.build().currentValue(),
      )
    }
  }

}

public class ExampleFunctionWithParamsScope :
    GeneratedKiraScopeWithImpls<ExampleFunctionWithParamsScope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var param1: SupplierBuilder<String>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(param1, )

  public class SupplierImplsScope(
    private val scope: ExampleFunctionWithParamsScope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var param1: StringSupplierBuilder
      get() = scope.param1 as? StringSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.param1 = value
      }
  }
}
