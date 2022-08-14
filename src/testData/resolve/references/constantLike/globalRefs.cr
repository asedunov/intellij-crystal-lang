class <warning descr="#1: Class \"A\"">A</warning>

end

class <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#3: Class \"A\"">A</warning>

end

module <warning descr="#4: Module \"Y\"">Y</warning>
  class <warning descr="#5: Class \"A\"">A</warning>

  end
end

struct <warning descr="#6: Struct \"B\"">B</warning>

end

struct <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#7: Struct \"B\"">B</warning>

end

module <warning descr="#4: Module \"Y\"">Y</warning>
  struct <warning descr="#8: Struct \"B\"">B</warning>

  end
end

alias <warning descr="#9: Alias \"C\"">C</warning> = <warning descr="#10: Struct \"Int32\"">Int32</warning>

alias <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#11: Alias \"C\"">C</warning> = <warning descr="#10: Struct \"Int32\"">Int32</warning>

module <warning descr="#4: Module \"Y\"">Y</warning>
  alias <warning descr="#12: Alias \"C\"">C</warning> = <warning descr="#10: Struct \"Int32\"">Int32</warning>
end

module <warning descr="#13: Module \"D\"">D</warning>

end

module <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#14: Module \"D\"">D</warning>

end

module <warning descr="#4: Module \"Y\"">Y</warning>
  module <warning descr="#15: Module \"D\"">D</warning>

  end
end

enum <warning descr="#16: Enum \"E\"">E</warning>
  X
end

enum <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#17: Enum \"E\"">E</warning>
  X
end

module <warning descr="#4: Module \"Y\"">Y</warning>
  enum <warning descr="#18: Enum \"E\"">E</warning>
    X
  end
end

a1 : <warning descr="#1: Class \"A\"">A</warning>
a2 : <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#3: Class \"A\"">A</warning>
a3 : <warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#5: Class \"A\"">A</warning>
a4 : ::<warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#5: Class \"A\"">A</warning>

b1 : <warning descr="#6: Struct \"B\"">B</warning>
b2 : <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#7: Struct \"B\"">B</warning>
b3 : <warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#8: Struct \"B\"">B</warning>
b4 : ::<warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#8: Struct \"B\"">B</warning>

c1 : <warning descr="#9: Alias \"C\"">C</warning>
c2 : <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#11: Alias \"C\"">C</warning>
c3 : <warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#12: Alias \"C\"">C</warning>
c4 : ::<warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#12: Alias \"C\"">C</warning>

d1 : <warning descr="#13: Module \"D\"">D</warning>
d2 : <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#14: Module \"D\"">D</warning>
d3 : <warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#15: Module \"D\"">D</warning>
d4 : ::<warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#15: Module \"D\"">D</warning>

e1 : <warning descr="#16: Enum \"E\"">E</warning>
e2 : <warning descr="#2: Module \"X\"">X</warning>::<warning descr="#17: Enum \"E\"">E</warning>
e3 : <warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#18: Enum \"E\"">E</warning>
e4 : ::<warning descr="#4: Module \"Y\"">Y</warning>::<warning descr="#18: Enum \"E\"">E</warning>