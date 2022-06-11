// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.functions_with_strange_param_names

import com.popovanton0.kira.suppliers.BooleanSupplierBuilder
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraMisses
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.Injector
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.kira
import kotlin.Boolean
import kotlin.Throwable
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.listOf

/**
 * @param injector wasn't generated because:
 *  - Functions with generics are not supported. Provide types manually
 */
public class Kira_ExampleFunction(
  private val missesProvider: KiraScope.() -> Misses,
  private val injector: ExampleFunctionScope.() -> Injector<Unit>,
) : KiraProvider<ExampleFunctionScope> {
  private val misses: Misses = KiraScope().missesProvider()

  public override val kira: Kira<ExampleFunctionScope> = kira(ExampleFunctionScope()) {
    misses = boolean(paramName = "misses")
    missesProvider = boolean(paramName = "missesProvider")
    kira = boolean(paramName = "kira")
    injector = boolean(paramName = "injector")
    string = boolean(paramName = "string")
    Supplier = boolean(paramName = "Supplier")
    ExampleFunction = boolean(paramName = "ExampleFunction")
    functions_with_strange_names = boolean(paramName = "functions_with_strange_names")
    BooleanSupplierBuilder = boolean(paramName = "BooleanSupplierBuilder")
    scope = boolean(paramName = "scope")
    `scope$` = boolean(paramName = "scope${'$'}")
    Throwable = boolean(paramName = "Throwable")
    supplierImplsScope = boolean(paramName = "supplierImplsScope")
    collectSuppliers = boolean(paramName = "collectSuppliers")
    suppliers = boolean(paramName = "suppliers")
    `value` = boolean(paramName = "value")
    implChanged = boolean(paramName = "implChanged")
    sdf = this@Kira_ExampleFunction.misses.sdf
    injector()
  }


  public data class Misses(
    public val sdf: SupplierBuilder<Throwable>,
  ) : KiraMisses
}

public class ExampleFunctionScope :
    GeneratedKiraScopeWithImpls<ExampleFunctionScope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var misses: SupplierBuilder<Boolean>

  public lateinit var missesProvider: SupplierBuilder<Boolean>

  public lateinit var kira: SupplierBuilder<Boolean>

  public lateinit var injector: SupplierBuilder<Boolean>

  public lateinit var string: SupplierBuilder<Boolean>

  public lateinit var Supplier: SupplierBuilder<Boolean>

  public lateinit var ExampleFunction: SupplierBuilder<Boolean>

  public lateinit var functions_with_strange_names: SupplierBuilder<Boolean>

  public lateinit var BooleanSupplierBuilder: SupplierBuilder<Boolean>

  public lateinit var scope: SupplierBuilder<Boolean>

  public lateinit var `scope$`: SupplierBuilder<Boolean>

  public lateinit var Throwable: SupplierBuilder<Boolean>

  public lateinit var supplierImplsScope: SupplierBuilder<Boolean>

  public lateinit var collectSuppliers: SupplierBuilder<Boolean>

  public lateinit var suppliers: SupplierBuilder<Boolean>

  public lateinit var `value`: SupplierBuilder<Boolean>

  public lateinit var implChanged: SupplierBuilder<Boolean>

  public lateinit var sdf: SupplierBuilder<Throwable>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(misses,
      missesProvider, kira, injector, string, Supplier, ExampleFunction,
      functions_with_strange_names, BooleanSupplierBuilder, scope, `scope$`, Throwable,
      supplierImplsScope, collectSuppliers, suppliers, `value`, implChanged, sdf, )

  public class SupplierImplsScope(
    private val `scope$$`: ExampleFunctionScope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var misses: BooleanSupplierBuilder
      get() = `scope$$`.misses as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.misses = value
      }

    public var missesProvider: BooleanSupplierBuilder
      get() = `scope$$`.missesProvider as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.missesProvider = value
      }

    public var kira: BooleanSupplierBuilder
      get() = `scope$$`.kira as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.kira = value
      }

    public var injector: BooleanSupplierBuilder
      get() = `scope$$`.injector as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.injector = value
      }

    public var string: BooleanSupplierBuilder
      get() = `scope$$`.string as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.string = value
      }

    public var Supplier: BooleanSupplierBuilder
      get() = `scope$$`.Supplier as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.Supplier = value
      }

    public var ExampleFunction: BooleanSupplierBuilder
      get() = `scope$$`.ExampleFunction as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.ExampleFunction = value
      }

    public var functions_with_strange_names: BooleanSupplierBuilder
      get() = `scope$$`.functions_with_strange_names as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.functions_with_strange_names = value
      }

    public var BooleanSupplierBuilder: BooleanSupplierBuilder
      get() = `scope$$`.BooleanSupplierBuilder as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.BooleanSupplierBuilder = value
      }

    public var scope: BooleanSupplierBuilder
      get() = `scope$$`.scope as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.scope = value
      }

    public var `scope$`: BooleanSupplierBuilder
      get() = `scope$$`.`scope$` as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.`scope$` = value
      }

    public var Throwable: BooleanSupplierBuilder
      get() = `scope$$`.Throwable as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.Throwable = value
      }

    public var supplierImplsScope: BooleanSupplierBuilder
      get() = `scope$$`.supplierImplsScope as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.supplierImplsScope = value
      }

    public var collectSuppliers: BooleanSupplierBuilder
      get() = `scope$$`.collectSuppliers as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.collectSuppliers = value
      }

    public var suppliers: BooleanSupplierBuilder
      get() = `scope$$`.suppliers as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.suppliers = value
      }

    public var `value`: BooleanSupplierBuilder
      get() = `scope$$`.`value` as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.`value` = value
      }

    public var implChanged: BooleanSupplierBuilder
      get() = `scope$$`.implChanged as? BooleanSupplierBuilder ?: implChanged()
      set(`value`) {
        `scope$$`.implChanged = value
      }
  }
}
