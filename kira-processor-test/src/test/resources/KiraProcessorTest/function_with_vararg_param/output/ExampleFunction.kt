// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.function_with_vararg_param

import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.NullableStringSupplierBuilder
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.Injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.nullableString
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.listOf

/**
 * @param injector wasn't generated because:
 *  - Functions with vararg params are not supported
 */
public class Kira_ExampleFunction(
  public val injector: ExampleFunctionScope.() -> Injector<Unit>,
) : KiraProvider<ExampleFunctionScope> {
  public override val kira: Kira<ExampleFunctionScope> = kira(ExampleFunctionScope()) {
    param1 = nullableString(paramName = "param1", defaultValue = null)
    injector()
  }

}

public class ExampleFunctionScope :
    GeneratedKiraScopeWithImpls<ExampleFunctionScope.SupplierImplsScope>() {
  public override val supplierImplsScope: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var param1: Supplier<String?>

  public override fun collectSuppliers(): List<Supplier<*>> = listOf(param1, )

  public class SupplierImplsScope(
    private val scope: ExampleFunctionScope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var param1: NullableStringSupplierBuilder
      get() = scope.param1 as? NullableStringSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.param1 = value
      }
  }
}
