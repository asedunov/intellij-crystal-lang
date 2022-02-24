lib <warning descr="#1: Library \"L\"">L</warning>
  struct <warning descr="#2: Struct \"S\"">S</warning>

  end

  $foo1 : <warning descr="#2: Struct \"S\"">S</warning>
  $foo2 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#2: Struct \"S\"">S</warning>
  $foo3 : <warning descr="#3: Union \"U\"">U</warning>
  $foo4 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#3: Union \"U\"">U</warning>
  $foo5 : <warning descr="#4: Type declaration \"T\"">T</warning>
  $foo6 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#4: Type declaration \"T\"">T</warning>
end

lib <warning descr="#1: Library \"L\"">L</warning>
  union <warning descr="#3: Union \"U\"">U</warning>

  end

  $foo1 : <warning descr="#2: Struct \"S\"">S</warning>
  $foo2 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#2: Struct \"S\"">S</warning>
  $foo3 : <warning descr="#3: Union \"U\"">U</warning>
  $foo4 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#3: Union \"U\"">U</warning>
  $foo5 : <warning descr="#4: Type declaration \"T\"">T</warning>
  $foo6 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#4: Type declaration \"T\"">T</warning>
end

lib <warning descr="#1: Library \"L\"">L</warning>
  $foo1 : <warning descr="#2: Struct \"S\"">S</warning>
  $foo2 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#2: Struct \"S\"">S</warning>
  $foo3 : <warning descr="#3: Union \"U\"">U</warning>
  $foo4 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#3: Union \"U\"">U</warning>
  $foo5 : <warning descr="#4: Type declaration \"T\"">T</warning>
  $foo6 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#4: Type declaration \"T\"">T</warning>
end

lib <warning descr="#1: Library \"L\"">L</warning>
  type <warning descr="#4: Type declaration \"T\"">T</warning> = <warning descr="#5: Struct \"Int32\"">Int32</warning>

  $foo1 : <warning descr="#2: Struct \"S\"">S</warning>
  $foo2 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#2: Struct \"S\"">S</warning>
  $foo3 : <warning descr="#3: Union \"U\"">U</warning>
  $foo4 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#3: Union \"U\"">U</warning>
  $foo5 : <warning descr="#4: Type declaration \"T\"">T</warning>
  $foo6 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#4: Type declaration \"T\"">T</warning>
end

foo1 : <warning descr="unresolved">S</warning>
foo2 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#2: Struct \"S\"">S</warning>
foo3 : <warning descr="unresolved">U</warning>
foo4 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#3: Union \"U\"">U</warning>
foo5 : <warning descr="unresolved">T</warning>
foo6 : <warning descr="#1: Library \"L\"">L</warning>::<warning descr="#4: Type declaration \"T\"">T</warning>