# INDEX: CrystalTypeShortNameIndex
# KEY: A

<caret>enum A
  X
  Y
end

enum B
  X
  Y
end

<caret>enum ::A
  X
  Y
end

enum ::B
  X
  Y
end

enum A::X
  X
  Y
end

<caret>enum X::A
  X
  Y
end

module M
  <caret>enum A
    X
    Y
  end

  enum B
    X
    Y
  end

  <caret>enum ::A
    X
    Y
  end

  enum ::B
    X
    Y
  end

  enum A::X
    X
    Y
  end

  <caret>enum X::A
    X
    Y
  end
end

lib L
  <caret>enum A
    X
    Y
  end

  enum B
    X
    Y
  end
end