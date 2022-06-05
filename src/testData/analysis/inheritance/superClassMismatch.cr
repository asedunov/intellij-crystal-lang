class X

end

class Y

end

class B

end

class B < <error descr="Superclass mismatch: Reference is expected, but X is found">X</error>

end

class C < X

end

class C

end

class D < X

end

class D < <error descr="Superclass mismatch: X is expected, but Y is found">Y</error>

end