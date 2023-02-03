macro foo(<error descr="Duplicated parameter name: a">a</error>, b, <error descr="Duplicated parameter name: a">a</error>)
end

macro foo(a, b, c)
end

macro foo(x a, y b, z c)
end

macro foo(x <error descr="Duplicated parameter name: a">a</error>, y b, z <error descr="Duplicated parameter name: a">a</error>)
end

macro foo(<error descr="Duplicated external name name: x">x</error> a, y b, <error descr="Duplicated external name name: x">x</error> c)
end

macro foo(<error descr="Duplicated external name name: x">"x"</error> a, "y" b, <error descr="Duplicated external name name: x">x</error> c)
end

macro foo(<error descr="When specified, external name must be different than internal name">a</error> a, y b, z c)
end

macro foo(x a, <error descr="When specified, external name must be different than internal name">"b"</error> b, z c)
end