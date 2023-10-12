# HINT: x, y, [*z], u, v

macro foo(x, y, *z, u, v)
  puts {{x}}, {{y}}
end

foo(1, 2, 3, <caret>4, v: 5, u: 6)