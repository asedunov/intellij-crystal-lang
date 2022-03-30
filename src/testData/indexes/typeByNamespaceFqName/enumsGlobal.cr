# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY:

<caret>enum A
  X
  Y
end

<caret>enum ::B
  X
  Y
end

enum C::D
  X
  Y
end

enum ::E::F
  X
  Y
end

<caret>module M
  enum A
    X
    Y
  end

  <caret>enum ::B
    X
    Y
  end

  enum C::D
    X
    Y
  end

  enum ::E::F
    X
    Y
  end
end

<caret>lib L
  enum A
    X
    Y
  end
end