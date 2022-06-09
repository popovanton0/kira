package non_root_module

import com.popovanton0.kira.annotations.Kira

@Kira fun ExampleFunction1() = Unit
@Kira fun ExampleFunction2() = Unit
@Kira(name = "CustomName") fun ExampleFunction3() = Unit