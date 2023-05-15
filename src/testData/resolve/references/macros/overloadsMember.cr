class <warning descr="#1: Class \"A\"">A</warning>
  macro <warning descr="#2: Macro \"foo\"">foo</warning>(a, b)

  end

  macro <warning descr="#3: Macro \"foo\"">foo</warning>(a, b)

  end
end

<warning descr="#1: Class \"A\"">A</warning>.<warning descr="#3: Macro \"foo\", call<args: 0, 1, params: 0, 1>">foo</warning> 1, 2