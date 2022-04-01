# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M

class A
end

class ::B
end

<caret>class M::C
end

<caret>class ::M::D
end

module M
  <caret>class A
  end

  class ::B
  end

  class <caret>C::D
  end

  class ::E::F
  end

  <caret>class ::M::G
  end
end