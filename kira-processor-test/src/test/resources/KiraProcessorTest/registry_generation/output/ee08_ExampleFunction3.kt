// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.registry_generation

import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraMisses
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.StringSupplierBuilder
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.string
import kotlin.String
import kotlin.Throwable
import kotlin.collections.List
import kotlin.collections.listOf
import registry_generation.ExampleFunction3

public class Kira_ExampleFunction3(
  private val missesProvider: KiraScope.() -> Misses,
) : KiraProvider<ExampleFunction3Scope> {
  private val misses: Misses = KiraScope().missesProvider()

  public override val kira: Kira<ExampleFunction3Scope> = kira(ExampleFunction3Scope()) {
    param1 = string(paramName = "param1", defaultValue = "Lorem")
    param2 = this@Kira_ExampleFunction3.misses.param2
    injector {
      ExampleFunction3(
        param1 = param1.build().currentValue(),
        param2 = this@Kira_ExampleFunction3.misses.param2.build().currentValue(),
      )
    }
  }


  public data class Misses(
    public val param2: SupplierBuilder<Throwable>,
  ) : KiraMisses
}

public class ExampleFunction3Scope :
    GeneratedKiraScopeWithImpls<ExampleFunction3Scope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var param1: SupplierBuilder<String>

  public lateinit var param2: SupplierBuilder<Throwable>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(param1, param2, )

  public class SupplierImplsScope(
    private val scope: ExampleFunction3Scope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var param1: StringSupplierBuilder
      get() = scope.param1 as? StringSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.param1 = value
      }
  }
}
