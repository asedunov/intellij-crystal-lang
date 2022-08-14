class <warning descr="#1: Class \"A\"">A</warning>
  class <warning descr="#2: Class \"X\"">X</warning>

  end
end

module <warning descr="#3: Module \"M\"">M</warning>
  class <warning descr="#4: Class \"X\"">X</warning>

  end
end

module <warning descr="#5: Module \"N\"">N</warning>
  class <warning descr="#6: Class \"X\"">X</warning>

  end
end

class <warning descr="#7: Class \"B\"">B</warning> < <warning descr="#1: Class \"A\"">A</warning>
  x : <warning descr="#2: Class \"X\"">X</warning>
end

class <warning descr="#8: Class \"C\"">C</warning> < <warning descr="#1: Class \"A\"">A</warning>
  include <warning descr="#3: Module \"M\"">M</warning>
  x : <warning descr="#4: Class \"X\"">X</warning>
end

class <warning descr="#9: Class \"D\"">D</warning> < <warning descr="#1: Class \"A\"">A</warning>
  include <warning descr="#3: Module \"M\"">M</warning>
  include <warning descr="#5: Module \"N\"">N</warning>
  x : <warning descr="#6: Class \"X\"">X</warning>
end