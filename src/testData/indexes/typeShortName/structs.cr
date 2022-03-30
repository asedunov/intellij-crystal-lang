# INDEX: CrystalTypeShortNameIndex
# KEY: A

<caret>struct A
end

struct B
end

<caret>struct ::A
end

struct ::B
end

struct A::X
end

<caret>struct X::A
end

module M
  <caret>struct A
  end

  struct B
  end

  <caret>struct ::A
  end

  struct ::B
  end

  struct A::X
  end

  <caret>struct X::A
  end
end