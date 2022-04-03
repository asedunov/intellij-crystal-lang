# INDEX: CrystalTypeFqNameIndex
# KEY: A

<caret>enum A
  X
  Y
  Z
end

<caret>enum ::A
  X
  Y
  Z
end

enum B::A
  X
  Y
  Z
end

enum <caret>A::B
  X
  Y
  Z
end

module M
  enum A
    X
    Y
    Z
  end

  <caret>enum ::A
    X
    Y
    Z
  end
end