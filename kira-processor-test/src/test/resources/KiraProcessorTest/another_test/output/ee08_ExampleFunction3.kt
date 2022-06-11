// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.another_test

import another_test.B
import another_test.Engine
import another_test.Quality
import another_test.Rock
import com.popovanton0.kira.suppliers.BooleanSupplierBuilder
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraMisses
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.NullableBooleanSupplierBuilder
import com.popovanton0.kira.suppliers.NullableObjectSupplierBuilder
import com.popovanton0.kira.suppliers.NullableStringSupplierBuilder
import com.popovanton0.kira.suppliers.ObjectSupplierBuilder
import com.popovanton0.kira.suppliers.OneOfManySupplierBuilder
import com.popovanton0.kira.suppliers.StringSupplierBuilder
import com.popovanton0.kira.suppliers.`enum`
import com.popovanton0.kira.suppliers.`object`
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.Injector
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.nullableBoolean
import com.popovanton0.kira.suppliers.nullableEnum
import com.popovanton0.kira.suppliers.nullableObject
import com.popovanton0.kira.suppliers.nullableString
import com.popovanton0.kira.suppliers.string
import kotlin.Boolean
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.listOf

/**
 * @param injector wasn't generated because:
 *  - Functions with vararg params are not supported
 */
public class Kira_ExampleFunction3(
  private val missesProvider: KiraScope.() -> Misses,
  private val injector: ExampleFunction3Scope.() -> Injector<Unit>,
) : KiraProvider<ExampleFunction3Scope> {
  private val misses: Misses = KiraScope().missesProvider()

  public override val kira: Kira<ExampleFunction3Scope> = kira(ExampleFunction3Scope()) {
    ds1 = `enum`(
      paramName = "ds1",
      qualifiedName = "another_test.Quality",
    )

    ds2 = nullableEnum(
      paramName = "ds2",
      qualifiedName = "another_test.Quality",
    )

    ds3 = string(paramName = "ds3")
    ds4 = nullableString(paramName = "ds4")
    ds5 = boolean(paramName = "ds5")
    ds6 = nullableBoolean(paramName = "ds6")
    ds7 = `object`(
      paramName = "ds7",
      qualifiedName = "another_test.Rock",
      value = Rock,
    )

    ds8 = nullableObject(
      paramName = "ds8",
      qualifiedName = "another_test.Rock",
      value = Rock,
    )

    ds9 = this@Kira_ExampleFunction3.misses.ds9
    ds10 = this@Kira_ExampleFunction3.misses.ds10
    injector()
  }


  public data class Misses(
    public val ds9: SupplierBuilder<B?>,
    public val ds10: SupplierBuilder<Engine?>,
  ) : KiraMisses
}

public class ExampleFunction3Scope :
    GeneratedKiraScopeWithImpls<ExampleFunction3Scope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var ds1: SupplierBuilder<Quality>

  public lateinit var ds2: SupplierBuilder<Quality?>

  public lateinit var ds3: SupplierBuilder<String>

  public lateinit var ds4: SupplierBuilder<String?>

  public lateinit var ds5: SupplierBuilder<Boolean>

  public lateinit var ds6: SupplierBuilder<Boolean?>

  public lateinit var ds7: SupplierBuilder<Rock>

  public lateinit var ds8: SupplierBuilder<Rock?>

  public lateinit var ds9: SupplierBuilder<B?>

  public lateinit var ds10: SupplierBuilder<Engine?>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(ds1, ds2, ds3,
      ds4, ds5, ds6, ds7, ds8, ds9, ds10, )

  public class SupplierImplsScope(
    private val scope: ExampleFunction3Scope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var ds1: OneOfManySupplierBuilder<Quality>
      get() = scope.ds1 as? OneOfManySupplierBuilder<Quality> ?: implChanged()
      set(`value`) {
        scope.ds1 = value
      }

    public var ds2: OneOfManySupplierBuilder<Quality?>
      get() = scope.ds2 as? OneOfManySupplierBuilder<Quality?> ?: implChanged()
      set(`value`) {
        scope.ds2 = value
      }

    public var ds3: StringSupplierBuilder
      get() = scope.ds3 as? StringSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.ds3 = value
      }

    public var ds4: NullableStringSupplierBuilder
      get() = scope.ds4 as? NullableStringSupplierBuilder ?: implChanged()
      set(`value`) {
        scope.ds4 = value
      }

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

    public var ds7: ObjectSupplierBuilder<Rock>
      get() = scope.ds7 as? ObjectSupplierBuilder<Rock> ?: implChanged()
      set(`value`) {
        scope.ds7 = value
      }

    public var ds8: NullableObjectSupplierBuilder<Rock>
      get() = scope.ds8 as? NullableObjectSupplierBuilder<Rock> ?: implChanged()
      set(`value`) {
        scope.ds8 = value
      }
  }
}
