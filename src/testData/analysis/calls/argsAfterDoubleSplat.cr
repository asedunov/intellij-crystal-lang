foo(**x, **y)

foo(**x, &y)

foo(**x, &.y)

foo(**x) {}

foo(**x) do
end

foo(**x, <error descr="Splat not allowed after double splat">*y</error>)

foo(**x, <error descr="Argument not allowed after double splat">y</error>)

foo(**x, <error descr="Argument not allowed after double splat">y: 1</error>)

foo(**x, <error descr="Out argument not allowed after double splat">out y</error>)