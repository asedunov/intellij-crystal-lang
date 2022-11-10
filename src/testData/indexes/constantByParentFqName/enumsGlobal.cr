# INDEX: CrystalConstantParentFqNameIndex
# KEY:

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

enum <caret>B::A
  X
  Y
  Z
end

enum <caret>A::B
  X
  Y
  Z
end

<caret>module M
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