def foo(x : Int32 = 1)
end

def foo(*x : Int32 = <error descr="Splat parameter can't have a default value">1</error>)
end

def foo(**x : Int32 = <error descr="Double splat parameter can't have a default value">1</error>)
end

def foo(x : Int32 = 1, <error descr="Parameter must have a default value">y : Int32</error>)
end

def foo(x : Int32 = 1, *y, z : Int32)
end

def foo(x : Int32 = 1, &y)
end