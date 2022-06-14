// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.another_test

import another_test.Composable
import another_test.ExampleFunction2
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraMisses
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import kotlin.Any
import kotlin.Char
import kotlin.Float
import kotlin.Function0
import kotlin.Function1
import kotlin.Function2
import kotlin.Int
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.listOf
import kotlin.coroutines.SuspendFunction0

public class Kira_ExampleFunction2(
  private val missesProvider: KiraScope.() -> Misses,
) : KiraProvider<ExampleFunction2Scope> {
  private val misses: Misses = KiraScope().missesProvider()

  public override val kira: Kira<ExampleFunction2Scope> = kira(ExampleFunction2Scope()) {
    ds = this@Kira_ExampleFunction2.misses.ds
    ds2 = this@Kira_ExampleFunction2.misses.ds2
    ds3 = this@Kira_ExampleFunction2.misses.ds3
    ds4 = this@Kira_ExampleFunction2.misses.ds4
    injector {
      ExampleFunction2(
        ds = this@Kira_ExampleFunction2.misses.ds.build().currentValue(),
        ds2 = this@Kira_ExampleFunction2.misses.ds2.build().currentValue(),
        ds3 = this@Kira_ExampleFunction2.misses.ds3.build().currentValue(),
        ds4 = this@Kira_ExampleFunction2.misses.ds4.build().currentValue(),
      )
    }
  }


  public data class Misses(
    public val ds: SupplierBuilder<@Composable Function0<Unit>>,
    public val ds2: SupplierBuilder<SuspendFunction0<Unit>>,
    public val ds3: SupplierBuilder<@Composable Function2<Function1<Char, Unit>, Int, Unit>>,
    public val ds4:
        SupplierBuilder<List<MutableList<in Function0<Map<Any?, SuspendFunction0<Float?>?>>>>>,
  ) : KiraMisses
}

public class ExampleFunction2Scope :
    GeneratedKiraScopeWithImpls<ExampleFunction2Scope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var ds: SupplierBuilder<@Composable Function0<Unit>>

  public lateinit var ds2: SupplierBuilder<SuspendFunction0<Unit>>

  public lateinit var ds3: SupplierBuilder<@Composable Function2<Function1<Char, Unit>, Int, Unit>>

  public lateinit var ds4:
      SupplierBuilder<List<MutableList<in Function0<Map<Any?, SuspendFunction0<Float?>?>>>>>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(ds, ds2, ds3,
      ds4, )

  public class SupplierImplsScope(
    private val scope: ExampleFunction2Scope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope()
}