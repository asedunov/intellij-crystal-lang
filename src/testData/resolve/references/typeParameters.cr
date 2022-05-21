class <warning descr="#1: Class \"C\"">C</warning>(<warning descr="#2: Type parameter \"T\"">T</warning>, <warning descr="#3: Type parameter \"U\"">U</warning>)
  def foo(type : <warning descr="#2: Type parameter \"T\"">T</warning>.class, var : <warning descr="#3: Type parameter \"U\"">U</warning>)

  end
end

struct <warning descr="#4: Struct \"S\"">S</warning>(<warning descr="#5: Type parameter \"T\"">T</warning>, <warning descr="#6: Type parameter \"U\"">U</warning>)
  def foo(type : <warning descr="#5: Type parameter \"T\"">T</warning>.class, var : <warning descr="#6: Type parameter \"U\"">U</warning>)

  end
end

module <warning descr="#7: Module \"M\"">M</warning>(<warning descr="#8: Type parameter \"T\"">T</warning>, <warning descr="#9: Type parameter \"U\"">U</warning>)
  def foo(type : <warning descr="#8: Type parameter \"T\"">T</warning>.class, var : <warning descr="#9: Type parameter \"U\"">U</warning>)

  end
end

def foo(type : <warning descr="#10: Type parameter \"T\"">T</warning>.class, var : <warning descr="#11: Type parameter \"U\"">U</warning>) forall <warning descr="#10: Type parameter \"T\"">T</warning>, <warning descr="#11: Type parameter \"U\"">U</warning>

end