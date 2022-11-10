# INDEX: CrystalConstantFqNameIndex
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

<caret>enum A::B
  X
  Y
  Z
end

<caret>enum ::A::B
  X
  Y
  Z
end

enum <caret>A::B::C
  X
  Y
  Z
end

module M
  enum A::B
    X
    Y
    Z
  end

  <caret>enum ::A::B
    X
    Y
    Z
  end
end

module A
  <caret>enum B
    X
    Y
    Z
  end

  enum ::B
    X
    Y
    Z
  end

  <caret>enum ::A::B
    X
    Y
    Z
  end
end