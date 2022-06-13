// This file is autogenerated. Do not edit it
package com.popovanton0.kira.generated.data_class_processor

import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.GeneratedKiraScopeWithImpls
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.dataclass.DataClassSupplierBuilder
import com.popovanton0.kira.suppliers.dataclass.NullableDataClassSupplierBuilder
import com.popovanton0.kira.suppliers.dataclass.dataClass
import com.popovanton0.kira.suppliers.dataclass.nullableDataClass
import com.popovanton0.kira.suppliers.kira
import data_class_processor.Car
import data_class_processor.ExampleFunction
import kotlin.collections.List
import kotlin.collections.listOf

public class Kira_ExampleFunction() : KiraProvider<ExampleFunctionScope> {
  public override val kira: Kira<ExampleFunctionScope> = kira(ExampleFunctionScope()) {
    car = dataClass(paramName = "car", dataClass = Car::class)
    carN = nullableDataClass(paramName = "carN", dataClass = Car::class)
    injector {
      ExampleFunction(
        car = car.build().currentValue(),
        carN = carN.build().currentValue(),
      )
    }
  }

}

public class ExampleFunctionScope :
    GeneratedKiraScopeWithImpls<ExampleFunctionScope.SupplierImplsScope>() {
  protected override val `$$$supplierImplsScope$$$`: SupplierImplsScope = SupplierImplsScope(this)

  public lateinit var car: SupplierBuilder<Car>

  public lateinit var carN: SupplierBuilder<Car?>

  public override fun collectSupplierBuilders(): List<SupplierBuilder<*>> = listOf(car, carN, )

  public class SupplierImplsScope(
    private val scope: ExampleFunctionScope,
  ) : GeneratedKiraScopeWithImpls.SupplierImplsScope() {
    public var car: DataClassSupplierBuilder<Car>
      get() = scope.car as? DataClassSupplierBuilder<Car> ?: implChanged()
      set(`value`) {
        scope.car = value
      }

    public var carN: NullableDataClassSupplierBuilder<Car>
      get() = scope.carN as? NullableDataClassSupplierBuilder<Car> ?: implChanged()
      set(`value`) {
        scope.carN = value
      }
  }
}
