macro foo(x = 1)
end

macro foo(*x = <error descr="Splat parameter can't have a default value">1</error>)
end

macro foo(**x = <error descr="Double splat parameter can't have a default value">1</error>)
end

macro foo(x = 1, <error descr="Parameter must have a default value">y</error>)
end

macro foo(x = 1, *y, z)
end

macro foo(x = 1, &y)
end