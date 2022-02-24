class <warning descr="#1: Class \"A\"">A</warning>
  class <warning descr="#2: Class \"X\"">X</warning>

  end
end

module <warning descr="#3: Module \"M\"">M</warning>
  class <warning descr="#4: Class \"Y\"">Y</warning>

  end
end

class <warning descr="#5: Class \"O\"">O</warning>
  class <warning descr="#6: Class \"X\"">X</warning>

  end

  class <warning descr="#7: Class \"Y\"">Y</warning>

  end

  class <warning descr="#8: Class \"Z\"">Z</warning>

  end

  class <warning descr="#9: Class \"B\"">B</warning> < <warning descr="#1: Class \"A\"">A</warning>
    include <warning descr="#3: Module \"M\"">M</warning>
    x : <warning descr="#2: Class \"X\"">X</warning>
    y : <warning descr="#4: Class \"Y\"">Y</warning>
    z : <warning descr="#8: Class \"Z\"">Z</warning>
  end
end
