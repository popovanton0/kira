// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.function_with_default_vararg_params

import com.popovanton0.kira.suppliers.BooleanSupplierBuilder
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import function_with_default_vararg_params.ExampleFunction
import kotlin.Boolean
import kotlin.collections.List
import kotlin.collections.listOf

public class Kira_ExampleFunction() : KiraProvider<ExampleFunctionScope> {
  public override val kira: Kira<ExampleFunctionScope> = kira(ExampleFunctionScope()) {
    param1 = boolean(paramName = "param1")
    injector {
      ExampleFunction(
        param1 = param1.build().currentValue(),
      )
    }
  }

}

public class ExampleFunctionScope :
    GeneratedKiraScopeWithImpls<ExampleFunctionScope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var param1: SupplierBuilder<Boolean>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(param1, )

  public class SupplierImplsScope(
    private val scope: ExampleFunctionScope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var param1: BooleanSupplierBuilder
      get() = scope.param1 as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.param1 = value
      }
  }
}
