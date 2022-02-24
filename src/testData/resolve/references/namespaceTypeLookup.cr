class <warning descr="#1: Class \"X\"">X</warning>
  class <warning descr="#2: Class \"A\"">A</warning>

  end
end

class <warning descr="#1: Class \"X\"">X</warning>::<warning descr="#3: Class \"Y\"">Y</warning>
  class <warning descr="#4: Class \"B\"">B</warning>

  end
end

class <warning descr="#1: Class \"X\"">X</warning>
  class <warning descr="#3: Class \"Y\"">Y</warning>
    class <warning descr="#5: Class \"Z\"">Z</warning>
      class <warning descr="#6: Class \"C\"">C</warning>

      end
    end
  end
end

class <warning descr="#1: Class \"X\"">X</warning>::<warning descr="#3: Class \"Y\"">Y</warning>::<warning descr="#5: Class \"Z\"">Z</warning>::<warning descr="#7: Class \"D\"">D</warning>
  a1 : <warning descr="#2: Class \"A\"">A</warning>
  a2 : <warning descr="#1: Class \"X\"">X</warning>::<warning descr="#2: Class \"A\"">A</warning>
  a3 : ::<warning descr="#1: Class \"X\"">X</warning>::<warning descr="#2: Class \"A\"">A</warning>

  b1 : <warning descr="#4: Class \"B\"">B</warning>
  b2 : <warning descr="#3: Class \"Y\"">Y</warning>::<warning descr="#4: Class \"B\"">B</warning>
  b3 : <warning descr="#1: Class \"X\"">X</warning>::<warning descr="#3: Class \"Y\"">Y</warning>::<warning descr="#4: Class \"B\"">B</warning>
  b4 : ::<warning descr="#1: Class \"X\"">X</warning>::<warning descr="#3: Class \"Y\"">Y</warning>::<warning descr="#4: Class \"B\"">B</warning>

  c1 : <warning descr="#6: Class \"C\"">C</warning>
  c2 : <warning descr="#5: Class \"Z\"">Z</warning>::<warning descr="#6: Class \"C\"">C</warning>
  c3 : <warning descr="#3: Class \"Y\"">Y</warning>::<warning descr="#5: Class \"Z\"">Z</warning>::<warning descr="#6: Class \"C\"">C</warning>
  c4 : <warning descr="#1: Class \"X\"">X</warning>::<warning descr="#3: Class \"Y\"">Y</warning>::<warning descr="#5: Class \"Z\"">Z</warning>::<warning descr="#6: Class \"C\"">C</warning>
  c5 : ::<warning descr="#1: Class \"X\"">X</warning>::<warning descr="#3: Class \"Y\"">Y</warning>::<warning descr="#5: Class \"Z\"">Z</warning>::<warning descr="#6: Class \"C\"">C</warning>
end