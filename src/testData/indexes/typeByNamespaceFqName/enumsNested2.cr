# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M::N

enum A
  X
  Y
end

enum ::B
  X
  Y
end

enum M::C
  X
  Y
end

enum ::M::D
  X
  Y
end

<caret>enum M::N::C
  X
  Y
end

<caret>enum ::M::N::D
  X
  Y
end

module M
  enum A
    X
    Y
  end

  enum ::B
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

  <caret>enum N::D
    X
    Y
  end

  enum ::N::F
    X
    Y
  end

<caret>enum ::M::N::G
    X
    Y
  end

  module N
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

    <caret>enum ::M::N::G
      X
      Y
    end
  end
end

module M::N
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

  <caret>enum ::M::N::G
    X
    Y
  end
end