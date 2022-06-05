class A(T, U)

end

class B < <error descr="Generic type arguments must be specified when inheriting A(T, U)">A</error>

end

class C < A(Int32, String)

end