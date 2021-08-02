def foo(a : Int32)
end

def foo(b)
end

def foo(<error descr="Cannot use 'if' as a parameter name">if</error> : Int32)
end

def foo(<error descr="Cannot use 'unless' as a parameter name">unless</error>)
end

def foo(x : Int32, *, y : String, <error descr="Double splat must have a name">**</error>)
end