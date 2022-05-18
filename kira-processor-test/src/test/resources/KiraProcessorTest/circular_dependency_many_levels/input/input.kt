package pkg2

import com.popovanton0.kira.annotations.Kira

class A(d: D, b: B)
class B(d: D, c: C, d2: D)
class C(b: B)
class D(s: String)

@Kira
fun ExampleFunction(a: A) = Unit