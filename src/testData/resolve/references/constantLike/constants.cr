puts <warning descr="#1: Constant \"A\"">A</warning>

<warning descr="#1: Constant \"A\"">A</warning> = 1

module <warning descr="#2: Module \"M\"">M</warning>
  puts <warning descr="#3: Constant \"D\"">D</warning>

  <warning descr="#3: Constant \"D\"">D</warning> = 4
end

class <warning descr="#4: Class \"S\"">S</warning>
  <warning descr="#5: Constant \"E\"">E</warning> = 5
end

class <warning descr="#6: Class \"X\"">X</warning> < <warning descr="#4: Class \"S\"">S</warning>
  puts <warning descr="#7: Constant \"B\"">B</warning>

  <warning descr="#7: Constant \"B\"">B</warning> = 2

  class <warning descr="#8: Class \"Y\"">Y</warning>
    puts <warning descr="#3: Constant \"D\"">D</warning>

    include <warning descr="#2: Module \"M\"">M</warning>

    puts <warning descr="#9: Constant \"C\"">C</warning>

    <warning descr="#9: Constant \"C\"">C</warning> = 3

    puts <warning descr="#1: Constant \"A\"">A</warning> + <warning descr="#7: Constant \"B\"">B</warning> + <warning descr="#9: Constant \"C\"">C</warning> + <warning descr="#3: Constant \"D\"">D</warning> + <warning descr="#5: Constant \"E\"">E</warning>
    puts ::<warning descr="#1: Constant \"A\"">A</warning> + <warning descr="#6: Class \"X\"">X</warning>::<warning descr="#7: Constant \"B\"">B</warning> + <warning descr="#8: Class \"Y\"">Y</warning>::<warning descr="#9: Constant \"C\"">C</warning> + <warning descr="#6: Class \"X\"">X</warning>::<warning descr="#8: Class \"Y\"">Y</warning>::<warning descr="#3: Constant \"D\"">D</warning> + <warning descr="#6: Class \"X\"">X</warning>::<warning descr="#5: Constant \"E\"">E</warning>
  end
end

puts <warning descr="#1: Constant \"A\"">A</warning> + <warning descr="#6: Class \"X\"">X</warning>::<warning descr="#7: Constant \"B\"">B</warning> + <warning descr="#6: Class \"X\"">X</warning>::<warning descr="#8: Class \"Y\"">Y</warning>::<warning descr="#9: Constant \"C\"">C</warning> + <warning descr="#6: Class \"X\"">X</warning>::<warning descr="#8: Class \"Y\"">Y</warning>::<warning descr="#3: Constant \"D\"">D</warning> + <warning descr="#4: Class \"S\"">S</warning>::<warning descr="#5: Constant \"E\"">E</warning>