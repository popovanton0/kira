// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.unknown_parameter_types

import com.popovanton0.kira.suppliers.BooleanSupplierBuilder
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraMisses
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.NullableBooleanSupplierBuilder
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.nullableBoolean
import kotlin.Boolean
import kotlin.collections.List
import kotlin.collections.listOf
import unknown_parameter_types.A
import unknown_parameter_types.B
import unknown_parameter_types.ExampleFunction

public class Kira_ExampleFunction(
  private val missesProvider: KiraScope.() -> Misses,
) : KiraProvider<ExampleFunctionScope> {
  private val misses: Misses = KiraScope().missesProvider()

  public override val kira: Kira<ExampleFunctionScope> = kira(ExampleFunctionScope()) {
    ds5 = boolean(paramName = "ds5")
    ds6 = nullableBoolean(paramName = "ds6")
    ds8 = this@Kira_ExampleFunction.misses.ds8
    ds9 = this@Kira_ExampleFunction.misses.ds9
    injector {
      ExampleFunction(
        ds5 = ds5.build().currentValue(),
        ds6 = ds6.build().currentValue(),
        ds8 = this@Kira_ExampleFunction.misses.ds8.build().currentValue(),
        ds9 = this@Kira_ExampleFunction.misses.ds9.build().currentValue(),
      )
    }
  }


  public data class Misses(
    public val ds8: SupplierBuilder<B?>,
    public val ds9: SupplierBuilder<A>,
  ) : KiraMisses
}

public class ExampleFunctionScope :
    GeneratedKiraScopeWithImpls<ExampleFunctionScope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var ds5: SupplierBuilder<Boolean>

  public lateinit var ds6: SupplierBuilder<Boolean?>

  public lateinit var ds8: SupplierBuilder<B?>

  public lateinit var ds9: SupplierBuilder<A>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(ds5, ds6, ds8,
      ds9, )

  public class SupplierImplsScope(
    private val scope: ExampleFunctionScope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var ds5: BooleanSupplierBuilder
      get() = scope.ds5 as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.ds5 = value
      }

    public var ds6: NullableBooleanSupplierBuilder
      get() = scope.ds6 as? NullableBooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.ds6 = value
      }
  }
}
