# INDEX: CrystalConstantParentFqNameIndex
# KEY: A::B

enum A
  X
  Y
  Z
end

enum ::A
  X
  Y
  Z
end

enum B::A
  X
  Y
  Z
end

enum A::B
  X
  Y
  Z
end

<caret>enum A::B::C
  X
  Y
  Z
end

<caret>enum ::A::B::C
  X
  Y
  Z
end


module M
  enum A::B::C
    X
    Y
    Z
  end

  <caret>enum ::A::B::C
    X
    Y
    Z
  end
end

module A
  <caret>enum B::C
    X
    Y
    Z
  end

  enum ::B::C
    X
    Y
    Z
  end

  <caret>enum ::A::B::C
    X
    Y
    Z
  end

  module B
    <caret>enum C
      X
      Y
      Z
    end

    enum ::C
      X
      Y
      Z
    end

    <caret>enum ::A::B::C
      X
      Y
      Z
    end
  end
end

module A::B
  <caret>enum C
    X
    Y
    Z
  end

  enum ::C
    X
    Y
    Z
  end

  <caret>enum ::A::B::C
    X
    Y
    Z
  end
end