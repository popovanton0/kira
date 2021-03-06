// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.function_with_unicode_name

import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.StringSupplierBuilder
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.string
import function_with_unicode_name.`ExampleFunction😃`
import kotlin.String
import kotlin.collections.List
import kotlin.collections.listOf

public class `Kira_ExampleFunction😃`() : KiraProvider<`ExampleFunction😃Scope`> {
  public override val kira: Kira<`ExampleFunction😃Scope`> = kira(`ExampleFunction😃Scope`()) {
    param1 = string(paramName = "param1")
    injector {
      `ExampleFunction😃`(
        param1 = param1.build().currentValue(),
      )
    }
  }

}

public class `ExampleFunction😃Scope` :
    GeneratedKiraScopeWithImpls<`ExampleFunction😃Scope`.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var param1: SupplierBuilder<String>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(param1, )

  public class SupplierImplsScope(
    private val scope: `ExampleFunction😃Scope`,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var param1: StringSupplierBuilder
      get() = scope.param1 as? StringSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.param1 = value
      }
  }
}
