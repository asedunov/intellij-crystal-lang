macro foo(x, y)
end

macro foo(x, *, y)
end

macro foo(x, *, y, **z)
end

macro foo(x, *, y, **z, <error descr="Only block parameter is allowed after double splat">u</error>)
end

macro foo(x, *, y, **z, &u)
end

macro foo(x, *, y, **z, &u, <error descr="No parameters are allowed after block">&v</error>)
end

macro foo(x, *, y, <error descr="Multiple splat parameters are not allowed">*</error>)
end

macro foo(x, *, y, <error descr="Multiple splat parameters are not allowed">*z</error>)
end

macro foo(x, *, y, &, <error descr="No parameters are allowed after block">**z</error>)
end

macro foo(x, **y, <error descr="Only block parameter is allowed after double splat">z</error>, <error descr="Only block parameter is allowed after double splat">**u</error>)
end

macro foo(&, <error descr="No parameters are allowed after block">**x</error>)
end

macro foo(x, *y)
end

macro foo(x, *y)
end

macro foo(x, <error descr="Named parameters must follow bare *">*</error>, **y)
end

macro foo(x, <error descr="Named parameters must follow bare *">*</error>, &y)
end