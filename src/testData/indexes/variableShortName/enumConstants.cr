# INDEX: CrystalVariableShortNameIndex
# KEY: X

enum A
  <caret>X
  Y
  Z
end

module M
  enum A
    <caret>X
    Y
    Z
  end
end

lib L
  enum A
    <caret>X
    Y
    Z
  end
end