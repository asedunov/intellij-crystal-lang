macro foo(a)
end

macro foo(<error descr="Cannot use 'if' as a parameter name">if</error>)
end

macro foo(<error descr="Cannot use 'unless' as a parameter name">unless</error>)
end

macro foo(x, *, y, <error descr="Double splat must have a name">**</error>)
end