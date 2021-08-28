def foo(x, y)
end

def foo(x, *, y)
end

def foo(x, *, y, **z)
end

def foo(x, *, y, **z, <error descr="Only block parameter is allowed after double splat">u</error>)
end

def foo(x, *, y, **z, &u)
end

def foo(x, *, y, **z, &u, <error descr="No parameters are allowed after block">&v</error>)
end

def foo(x, *, y, <error descr="Multiple splat parameters are not allowed">*</error>)
end

def foo(x, *, y, <error descr="Multiple splat parameters are not allowed">*z</error>)
end

def foo(x, *, y, &, <error descr="No parameters are allowed after block">**z</error>)
end

def foo(x, **y, <error descr="Only block parameter is allowed after double splat">z</error>, <error descr="Only block parameter is allowed after double splat">**u</error>)
end

def foo(&, <error descr="No parameters are allowed after block">**x</error>)
end

def foo(x, *y)
end

def foo(x, *y)
end

def foo(x, <error descr="Named parameters must follow bare *">*</error>, **y)
end

def foo(x, <error descr="Named parameters must follow bare *">*</error>, &y)
end