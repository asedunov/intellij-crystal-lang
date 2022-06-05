abstract struct A(T, U)

end

struct B < <error descr="Generic type arguments must be specified when inheriting A(T, U)">A</error>

end

struct C < A(Int32, String)

end