macro <warning descr="#1: Macro \"foo\"">foo</warning>(x, y, *z, u, v)
  puts {{x}}, {{y}}
end

macro <warning descr="#2: Macro \"foo\"">foo</warning>(x, y, z)
  puts {{x}}, {{y}}
end

<warning descr="#1: Macro \"foo\", #2: Macro \"foo\", call<args: 0, params: 0, ???, ???, ???, ???>, call<args: 0, params: 0, ???, ???>">foo</warning>(1)