module <warning descr="#1: Module \"M\"">M</warning>
  class <warning descr="#2: Class \"C\"">C</warning>

  end
end

a = <warning descr="#2: Class \"C\"">C</warning>.new

extend <warning descr="#1: Module \"M\"">M</warning>

a = <warning descr="#2: Class \"C\"">C</warning>.new