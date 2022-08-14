module <warning descr="#1: Module \"M\"">M</warning>
  include <warning descr="unresolved">N</warning>
end

module <warning descr="#2: Module \"N\"">N</warning>

end

module <warning descr="#3: Module \"P\"">P</warning>
  include <warning descr="#1: Module \"M\"">M</warning>
end