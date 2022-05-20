package com.popovanton0.kira.generated.sdf

import com.popovanton0.kira.suppliers.*
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.compound.CompoundSupplierBuilder
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.KiraScope
import sdf.Quality
import sdf.Rock

public class ExampleFunction3Scope :
    GeneratedKiraScopeWithImpls<ExampleFunction3Scope.SupplierImplsScope>() {
  public override val supplierImplsScope: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var ds1: Supplier<Quality>

  public lateinit var ds2: Supplier<Quality?>

  public lateinit var ds3: Supplier<String>

  public lateinit var ds4: Supplier<String?>

  public lateinit var ds5: Supplier<Boolean>

  public lateinit var ds6: Supplier<Boolean?>

  public lateinit var ds7: Supplier<Rock>

  public override fun collectSuppliers(): List<Supplier<*>> = listOf(ds1, ds2, ds3, ds4, ds5, ds6,
      ds7, )

  public class SupplierImplsScope(
    private val scope: ExampleFunction3Scope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var ds1: EnumSupplierBuilder<Quality>
      get() = scope.ds1 as? EnumSupplierBuilder<Quality> ?: implChanged()
      set(`value`) {
        scope.ds1 = value
      }

    public var ds2: NullableEnumSupplierBuilder<Quality?>
      get() = scope.ds2 as? NullableEnumSupplierBuilder<Quality?> ?: implChanged()
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

    public var ds7: CompoundSupplierBuilder<Rock, KiraScope>
      get() = scope.ds7 as? CompoundSupplierBuilder<Rock, KiraScope> ?: implChanged()
      set(`value`) {
        scope.ds7 = value
      }
  }
}
