class <warning descr="#1: Class \"A\"">A</warning>
  class <warning descr="#2: Class \"B\"">B</warning>

  end
end

module <warning descr="#3: Module \"M\"">M</warning>
  class <warning descr="#4: Class \"B\"">B</warning>

  end
end

class <warning descr="#5: Class \"B\"">B</warning> < <warning descr="#1: Class \"A\"">A</warning>
  include <warning descr="#3: Module \"M\"">M</warning>
  b : <warning descr="#5: Class \"B\"">B</warning>
end