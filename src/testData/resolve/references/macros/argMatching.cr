macro <warning descr="#1: Macro \"foo\"">foo</warning>(x, y, *z, u, v)
  puts {{x}}, {{y}}
end

<warning descr="#1: Macro \"foo\", call<args: 0, 1, 2, 2, 4, 3, params: 0, 1, (2, 3), 5, 4>">foo</warning>(1, 2, 3, 4, v: 5, u: 6)

<warning descr="#1: Macro \"foo\", call<args: 0, 1, 2, 2, 3, params: 0, 1, (2, 3), 4, ???>">foo</warning>(1, 2, 3, 4, u: 5)

<warning descr="#1: Macro \"foo\", call<args: 1, 0, 4, 3, params: 1, 0, ???, 3, 2>">foo</warning>(y: 2, x: 1, v: 5, u: 6)