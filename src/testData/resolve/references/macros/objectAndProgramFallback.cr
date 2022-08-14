class <warning descr="#1: Class \"A\"">A</warning>
  macro <warning descr="#2: Macro \"foo\"">foo</warning>(a)

  end

  macro <warning descr="#3: Macro \"bar\"">bar</warning>(a)

  end
end

module <warning descr="#4: Module \"M\"">M</warning>
  macro <warning descr="#5: Macro \"foo\"">foo</warning>(a)

  end
end

class <warning descr="#6: Class \"Object\"">Object</warning>
  macro <warning descr="#7: Macro \"foo\"">foo</warning>(a)

  end

  macro <warning descr="#8: Macro \"foo\"">foo</warning>(a, b)

  end
end

macro <warning descr="#9: Macro \"foo\"">foo</warning>(a)

end

macro <warning descr="#10: Macro \"foo\"">foo</warning>(a, b)

end

macro <warning descr="#11: Macro \"foo\"">foo</warning>(a, b, c)

end

<warning descr="#1: Class \"A\"">A</warning>.<warning descr="#2: Macro \"foo\"">foo</warning> 1
<warning descr="#4: Module \"M\"">M</warning>.<warning descr="#5: Macro \"foo\"">foo</warning> 1

<warning descr="#1: Class \"A\"">A</warning>.<warning descr="#8: Macro \"foo\"">foo</warning> 1, 2
<warning descr="#4: Module \"M\"">M</warning>.<warning descr="#8: Macro \"foo\"">foo</warning> 1, 2

<warning descr="#1: Class \"A\"">A</warning>.<warning descr="#11: Macro \"foo\"">foo</warning> 1, 2, 3
<warning descr="#4: Module \"M\"">M</warning>.<warning descr="#11: Macro \"foo\"">foo</warning> 1, 2, 3

<warning descr="#1: Class \"A\"">A</warning>.<warning descr="#3: Macro \"bar\"">bar</warning> 1
<warning descr="#4: Module \"M\"">M</warning>.bar 1