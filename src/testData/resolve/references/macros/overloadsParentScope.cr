class <warning descr="#1: Class \"A\"">A</warning>
  macro <warning descr="#2: Macro \"foo\"">foo</warning>(a, b)

  end
end

class <warning descr="#3: Class \"B\"">B</warning> < <warning descr="#1: Class \"A\"">A</warning>
  macro <warning descr="#4: Macro \"foo\"">foo</warning>(a, b)

  end
end

class <warning descr="#5: Class \"C\"">C</warning> < <warning descr="#3: Class \"B\"">B</warning>

end

<warning descr="#1: Class \"A\"">A</warning>.<warning descr="#2: Macro \"foo\", call<args: 0, 1, params: 0, 1>">foo</warning> 1, 2
<warning descr="#3: Class \"B\"">B</warning>.<warning descr="#4: Macro \"foo\", call<args: 0, 1, params: 0, 1>">foo</warning> 1, 2
<warning descr="#5: Class \"C\"">C</warning>.<warning descr="#4: Macro \"foo\", call<args: 0, 1, params: 0, 1>">foo</warning> 1, 2