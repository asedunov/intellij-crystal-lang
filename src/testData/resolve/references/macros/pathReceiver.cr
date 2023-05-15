class <warning descr="#1: Class \"A\"">A</warning>
  class <warning descr="#2: Class \"B\"">B</warning>
    class <warning descr="#3: Class \"C\"">C</warning>
      macro <warning descr="#4: Macro \"foo\"">foo</warning>(a, b)

      end
    end
  end

  <warning descr="#2: Class \"B\"">B</warning>::<warning descr="#3: Class \"C\"">C</warning>.<warning descr="#4: Macro \"foo\", call<args: 0, 1, params: 0, 1>">foo</warning> 1, 2
  ::<warning descr="#1: Class \"A\"">A</warning>::<warning descr="#2: Class \"B\"">B</warning>::<warning descr="#3: Class \"C\"">C</warning>.<warning descr="#4: Macro \"foo\", call<args: 0, 1, params: 0, 1>">foo</warning> 1, 2
end