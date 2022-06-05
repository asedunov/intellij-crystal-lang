abstract struct X

end

abstract struct Y

end

struct B

end

struct B < <error descr="Superclass mismatch: Struct is expected, but X is found">X</error>

end

struct C < X

end

struct C

end

struct D < X

end

struct D < <error descr="Superclass mismatch: X is expected, but Y is found">Y</error>

end