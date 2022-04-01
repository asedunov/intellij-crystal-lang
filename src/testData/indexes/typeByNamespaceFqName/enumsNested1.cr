# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M

enum A
  X
  Y
end

enum ::B
  X
  Y
end

<caret>enum M::C
  X
  Y
end

<caret>enum ::M::D
  X
  Y
end

module M
  <caret>enum A
    X
    Y
  end

  enum ::B
    X
    Y
  end

  enum <caret>C::D
    X
    Y
  end

  enum ::E::F
    X
    Y
  end

  <caret>enum ::M::G
    X
    Y
  end
end